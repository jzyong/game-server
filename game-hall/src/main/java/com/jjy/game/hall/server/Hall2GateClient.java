package com.jjy.game.hall.server;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * 捕鱼达人连接大厅 Tcp客户端
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年6月28日 下午4:12:57
 */
public class Hall2GateClient extends MutilMinaTcpClientService {
	private static final Logger LOGGER = LoggerFactory.getLogger(Hall2GateClient.class);

	public Hall2GateClient(ThreadPoolExecutorConfig threadPoolExecutorConfig, MinaClientConfig minaClientConfig) {
		super(threadPoolExecutorConfig, minaClientConfig);
	}

	@Override
	protected void running() {
		ServerThread syncThread = getExecutor(ThreadType.SYNC);
		syncThread.addTimerEvent(new ServerHeartTimer());

	}

	/**
	 * 更新大厅服务器信息
	 * 
	 * @param info
	 */
	public void updateHallServerInfo(ServerMessage.ServerInfo info) {
		ServerInfo serverInfo = serverMap.get(info.getId());
		if (serverInfo == null) {
			serverInfo = getServerInfo(info);
			addTcpClient(serverInfo, NetPort.GATE_GAME_PORT, new MutilConHallHandler(serverInfo, this)); 
		} else {
			serverInfo.setIp(info.getIp());
			serverInfo.setId(info.getId());
			serverInfo.setPort(info.getPort());
			serverInfo.setState(info.getState());
			serverInfo.setOnline(info.getOnline());
			serverInfo.setMaxUserCount(info.getMaxUserCount());
			serverInfo.setName(info.getName());
			serverInfo.setHttpPort(info.getHttpport());
			serverInfo.setWwwip(info.getWwwip());
		}
		serverMap.put(info.getId(), serverInfo);
	}

	private ServerInfo getServerInfo(ServerMessage.ServerInfo info) {
		ServerInfo serverInfo = new ServerInfo();
		serverInfo.setIp(info.getIp());
		serverInfo.setId(info.getId());
		serverInfo.setPort(info.getPort());
		serverInfo.setState(info.getState());
		serverInfo.setOnline(info.getOnline());
		serverInfo.setMaxUserCount(info.getMaxUserCount());
		serverInfo.setName(info.getName());
		serverInfo.setHttpPort(info.getHttpport());
		serverInfo.setWwwip(info.getWwwip());
		return serverInfo;
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
			// 向大厅服注册session TODO
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
			ScriptManager.getInstance().getBaseScriptEntry().executeScripts(IGameServerCheckScript.class,
					script -> script.buildServerInfo(info));
			builder.setServerInfo(info);
			session.write(new IDMessage(session, builder.build(), 0));
		}

	}

}
