package com.jzy.game.cluster.server;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.cluster.manager.ServerManager;
import com.jzy.game.engine.mina.TcpServer;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.engine.mina.handler.DefaultProtocolHandler;
import com.jzy.game.engine.server.ServerInfo;
import com.jzy.game.engine.server.Service;
import com.jzy.game.engine.thread.ServerThread;
import com.jzy.game.engine.thread.ThreadPoolExecutorConfig;
import com.jzy.game.engine.thread.ThreadType;
import com.jzy.game.engine.thread.timer.event.ServerHeartTimer;
import com.jzy.game.engine.util.IntUtil;
import com.jzy.game.engine.util.MsgUtil;

/**
 * TCP连接
 *
 * @author JiangZhiYong
 * @date 2017-03-31 QQ:359135103
 */
public class ClusterTcpServer extends Service<MinaServerConfig> {
	private static final Logger log = LoggerFactory.getLogger(ClusterTcpServer.class);

	private final TcpServer minaServer;
	private final MinaServerConfig minaServerConfig;
	public static final String SERVER_INFO = "serverInfo"; // 服务器信息

	public ClusterTcpServer(ThreadPoolExecutorConfig threadExcutorConfig, MinaServerConfig minaServerConfig) {
		super(threadExcutorConfig);
		this.minaServerConfig = minaServerConfig;

		minaServer = new TcpServer(minaServerConfig, new ClusterTcpServerHandler(this));
	}

	@Override
	protected void running() {
		log.debug(" run ... ");
		minaServer.run();
		ServerThread syncThread = getExecutor(ThreadType.SYNC);
		syncThread.addTimerEvent(new ServerHeartTimer());
	}

	@Override
	protected void onShutdown() {
		super.onShutdown();
		log.debug(" stop ... ");
		minaServer.stop();
	}

	@Override
	public String toString() {
		return minaServerConfig.getName();
	}

	public int getId() {
		return minaServerConfig.getId();
	}

	public String getName() {
		return minaServerConfig.getName();
	}

	public String getWeb() {
		return minaServerConfig.getChannel();
	}

	/**
	 * 消息处理器
	 *
	 * @author JiangZhiYong
	 * @date 2017-03-31 QQ:359135103
	 */
	public class ClusterTcpServerHandler extends DefaultProtocolHandler {
		private final Service<MinaServerConfig> service;

		public ClusterTcpServerHandler(Service<MinaServerConfig> service) {
			super(4); // 消息ID+消息内容
			this.service = service;
		}

		@Override
		public void sessionIdle(IoSession ioSession, IdleStatus idleStatus) {
			MsgUtil.close(ioSession, "链接空闲:" + ioSession.toString() + " " + idleStatus.toString()); // 客户端长时间不发送请求，将断开链接LoginTcpServer->minaServerConfig->readerIdleTime
																									// 60
																									// 1分钟
		}

		@Override
		public void sessionClosed(IoSession ioSession) {
			super.sessionClosed(ioSession);
			ServerInfo serverInfo = (ServerInfo) ioSession.getAttribute(SERVER_INFO);
			if (serverInfo != null) {
				log.warn("服务器:" + serverInfo.getName() + "断开链接");
				ServerManager.getInstance().removeServer(serverInfo);
			} else {
				log.error("未知游戏服务器断开链接");
			}
		}

		@Override
		protected void forward(IoSession session, int msgID, byte[] bytes) {
			log.warn("无法找到消息处理器：msgID{},bytes{}", msgID, IntUtil.BytesToStr(bytes));
		}

		@Override
		public Service<MinaServerConfig> getService() {
			return this.service;
		}

	}
}
