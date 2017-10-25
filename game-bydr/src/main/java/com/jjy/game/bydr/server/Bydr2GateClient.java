package com.jjy.game.bydr.server;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.bydr.thread.RoomExecutor;
import com.jjy.game.message.ServerMessage;
import com.jjy.game.message.ServerMessage.ServerRegisterRequest;
import com.jjy.game.model.constant.NetPort;
import com.jjy.game.model.script.IGameServerCheckScript;
import com.jzy.game.engine.mina.config.MinaClientConfig;
import com.jzy.game.engine.mina.message.IDMessage;
import com.jzy.game.engine.mina.service.MinaClientService;
import com.jzy.game.engine.mina.service.MutilMinaTcpClientService;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.engine.server.ServerInfo;
import com.jzy.game.engine.server.ServerState;
import com.jzy.game.engine.server.Service;
import com.jzy.game.engine.thread.ServerThread;
import com.jzy.game.engine.thread.ThreadPoolExecutorConfig;
import com.jzy.game.engine.thread.ThreadType;
import com.jzy.game.engine.thread.timer.event.ServerHeartTimer;
import com.jzy.game.engine.util.SysUtil;

/**
 * 捕鱼达人连接大厅 Tcp客户端
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年6月28日 下午4:12:57
 */
public class Bydr2GateClient extends MutilMinaTcpClientService {
	private static final Logger LOGGER = LoggerFactory.getLogger(Bydr2GateClient.class);

	public Bydr2GateClient(ThreadPoolExecutorConfig threadPoolExecutorConfig, MinaClientConfig minaClientConfig) {
		super(threadPoolExecutorConfig, minaClientConfig);
	}

	@Override
	protected void running() {
		// 全局同步线程
		ServerThread syncThread = getExecutor(ThreadType.SYNC);
		syncThread.addTimerEvent(new ServerHeartTimer());

		// 添加房间线程池
		RoomExecutor roomExecutor = new RoomExecutor();
		getServerThreads().put(ThreadType.ROOM, roomExecutor);
	}

	/**
	 * 消息处理器
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年7月11日 下午6:29:34
	 */
	public class MutilConHallHandler extends MutilTcpProtocolHandler {

		public MutilConHallHandler(ServerInfo serverInfo, MinaClientService service) {
			super(serverInfo, service);
		}

		@Override
		public void sessionOpened(IoSession session) {
			super.sessionOpened(session);
			// 向网关服注册session
			ServerRegisterRequest.Builder builder = ServerRegisterRequest.newBuilder();
			ServerMessage.ServerInfo.Builder info = ServerMessage.ServerInfo.newBuilder();
			info.setId(getMinaClientConfig().getId());
			info.setIp("");
			info.setMaxUserCount(1000);
			info.setOnline(1);
			info.setName(getMinaClientConfig().getName());
			info.setState(ServerState.NORMAL.getState());
			info.setType(getMinaClientConfig().getType().getType());
			info.setWwwip("");
			info.setTotalMemory(SysUtil.totalMemory());
			info.setFreeMemory(SysUtil.freeMemory());
			ScriptManager.getInstance().getBaseScriptEntry().executeScripts(IGameServerCheckScript.class,
					script -> script.buildServerInfo(info));
			builder.setServerInfo(info);
			session.write(new IDMessage(session, builder.build(), 0));
		}

	}

}
