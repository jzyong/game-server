package com.jzy.game.engine.mina.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.session.IoSession;

import com.jzy.game.engine.mina.MinaMultiTcpClient;
import com.jzy.game.engine.mina.config.MinaClientConfig;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.engine.mina.handler.DefaultClientProtocolHandler;
import com.jzy.game.engine.mina.message.IDMessage;
import com.jzy.game.engine.server.BaseServerConfig;
import com.jzy.game.engine.server.IMutilTcpClientService;
import com.jzy.game.engine.server.ServerInfo;
import com.jzy.game.engine.server.ServerType;
import com.jzy.game.engine.thread.ThreadPoolExecutorConfig;

/**
 * 多tcp连接客户端
 * <p>
 * 一般用于子游戏服务器和网关服，所有玩家共享连接
 * </p>
 *
 * @author JiangZhiYong
 * @QQ 359135103 2017年6月30日 下午3:23:32
 * @version $Id: $Id
 */
public class MutilMinaTcpClientService extends MinaClientService implements IMutilTcpClientService<MinaServerConfig>{
	protected final MinaMultiTcpClient multiTcpClient = new MinaMultiTcpClient();
	/** 网关服务器 */
	protected final Map<Integer, ServerInfo> serverMap = new ConcurrentHashMap<>();

	/**
	 * <p>Constructor for MutilMinaTcpClientService.</p>
	 *
	 * @param minaClientConfig a {@link com.jzy.game.engine.mina.config.MinaClientConfig} object.
	 */
	public MutilMinaTcpClientService(MinaClientConfig minaClientConfig) {
		super(minaClientConfig);
	}

	/**
	 * <p>Constructor for MutilMinaTcpClientService.</p>
	 *
	 * @param threadPoolExecutorConfig a {@link com.jzy.game.engine.thread.ThreadPoolExecutorConfig} object.
	 * @param minaClientConfig a {@link com.jzy.game.engine.mina.config.MinaClientConfig} object.
	 */
	public MutilMinaTcpClientService(ThreadPoolExecutorConfig threadPoolExecutorConfig, MinaClientConfig minaClientConfig) {
		super(threadPoolExecutorConfig, minaClientConfig);
	}

	/** {@inheritDoc} */
	@Override
	protected void running() {

	}

	/**
	 * {@inheritDoc}
	 *
	 * 移除客户端
	 */
	public void removeTcpClient(int serverId) {
		multiTcpClient.removeTcpClient(serverId);
		serverMap.remove(serverId);
	}

	/**
	 * {@inheritDoc}
	 *
	 * 添加连接服务器
	 */
	public void addTcpClient(ServerInfo serverInfo, int port) {
		addTcpClient(serverInfo, port, new MutilTcpProtocolHandler(serverInfo, this));
//		if (multiTcpClient.containsKey(serverInfo.getId())) {
//			return;
//		}
//		MinaClientConfig hallMinaClientConfig = createMinaClientConfig(serverInfo, port);
//		multiTcpClient.addTcpClient(this, hallMinaClientConfig, new MutilTcpProtocolHandler(serverInfo, this));
	}
	
	/**
	 * 添加连接大厅服务器
	 *
	 * @param serverInfo a {@link com.jzy.game.engine.server.ServerInfo} object.
	 * @param port a int.
	 * @param ioHandler a {@link com.jzy.game.engine.mina.service.MutilMinaTcpClientService.MutilTcpProtocolHandler} object.
	 */
	public void addTcpClient(ServerInfo serverInfo, int port,MutilTcpProtocolHandler ioHandler) {
		if (multiTcpClient.containsKey(serverInfo.getId())) {
			return;
		}
		MinaClientConfig hallMinaClientConfig = createMinaClientConfig(serverInfo, port);
		multiTcpClient.addTcpClient(this, hallMinaClientConfig,ioHandler);
	}

	/**
	 * 创建连接网关配置文件
	 * 
	 * @param serverInfo
	 * @param port
	 * @return
	 */
	private MinaClientConfig createMinaClientConfig(ServerInfo serverInfo, int port) {
		MinaClientConfig conf = new MinaClientConfig();
		conf.setType(ServerType.GATE);
		conf.setId(serverInfo.getId());
		conf.setMaxConnectCount(getMinaClientConfig().getMaxConnectCount());
		conf.setOrderedThreadPoolExecutorSize(getMinaClientConfig().getOrderedThreadPoolExecutorSize());
		MinaClientConfig.MinaClienConnToConfig con = new MinaClientConfig.MinaClienConnToConfig();
		con.setHost(serverInfo.getIp());
		con.setPort(port);
		conf.setConnTo(con);
		return conf;
	}

	/**
	 * <p>getServers.</p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public Map<Integer, ServerInfo> getServers() {
		return serverMap;
	}

	/**
	 * 监测连接状态
	 */
	public void checkStatus() {
		multiTcpClient.getTcpClients().values().forEach(cl -> cl.checkStatus());
	}

	/**
	 * {@inheritDoc}
	 *
	 * 广播所有服务器消息：注意，这里并不是向每个session广播，因为有可能有多个连接到同一个服务器
	 */
	public boolean broadcastMsg(Object obj) {
		if (multiTcpClient == null) {
			return false;
		}
		IDMessage idm = new IDMessage(null, obj, 0);
		serverMap.values().forEach(server -> {
			server.sendMsg(idm);
		});
		return true;
	}
	
	/**
	 * 广播所有服务器消息：注意，这里并不是向每个session广播，因为有可能有多个连接到同一个服务器
	 *
	 * @param obj a {@link java.lang.Object} object.
	 * @param rid a long.
	 * @return a boolean.
	 */
	public boolean broadcastMsg(Object obj,long rid) {
		if (multiTcpClient == null) {
			return false;
		}
		IDMessage idm = new IDMessage(null, obj, rid);
		serverMap.values().forEach(server -> {
			server.sendMsg(idm);
		});
		return true;
	}


	/**
	 * {@inheritDoc}
	 *
	 * 发送消息
	 */
	public boolean sendMsg(Integer serverId, Object msg) {
		if (multiTcpClient == null) {
			return false;
		}
		IDMessage idm = new IDMessage(null, msg, 0);
		return multiTcpClient.sendMsg(serverId, idm);
	}

	/**
	 * 多连接消息处理器
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年7月4日 下午3:22:48
	 */
	public class MutilTcpProtocolHandler extends DefaultClientProtocolHandler {

		private final ServerInfo serverInfo;

		public MutilTcpProtocolHandler(ServerInfo serverInfo, MinaClientService service) {
			super(12, service);
			this.serverInfo = serverInfo;
		}

		@Override
		public void sessionOpened(IoSession session) {
			super.sessionOpened(session);
			serverInfo.onIoSessionConnect(session);
		}

	}

}
