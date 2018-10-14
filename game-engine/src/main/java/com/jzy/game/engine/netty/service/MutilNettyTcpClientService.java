package com.jzy.game.engine.netty.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jzy.game.engine.mina.config.MinaClientConfig;
import com.jzy.game.engine.mina.message.IDMessage;
import com.jzy.game.engine.netty.NettyMutilTcpClient;
import com.jzy.game.engine.netty.code.DefaultClientChannelInitializer;
import com.jzy.game.engine.netty.code.DefaultMessageCodec;
import com.jzy.game.engine.netty.config.NettyClientConfig;
import com.jzy.game.engine.netty.handler.DefaultClientInBoundHandler;
import com.jzy.game.engine.netty.handler.DefaultOutBoundHandler;
import com.jzy.game.engine.server.IMutilTcpClientService;
import com.jzy.game.engine.server.ServerInfo;
import com.jzy.game.engine.server.ServerType;
import com.jzy.game.engine.thread.ThreadPoolExecutorConfig;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * netty 多客户端连接服务,连接多个服务器
 * 
 * <p>
 * 一般用于子游戏服务器和网关服，所有玩家共享连接
 * </p>
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年8月28日 下午1:49:39
 */
public class MutilNettyTcpClientService extends NettyClientService implements IMutilTcpClientService<NettyClientConfig> {
	protected final NettyMutilTcpClient multiTcpClient = new NettyMutilTcpClient();
	/** 网关服务器 */
	protected final Map<Integer, ServerInfo> serverMap = new ConcurrentHashMap<>();

	public MutilNettyTcpClientService(NettyClientConfig nettyClientConfig) {
		super(nettyClientConfig);
	}

	public MutilNettyTcpClientService(ThreadPoolExecutorConfig threadPoolExecutorConfig,
			NettyClientConfig nettyClientConfig) {
		super(threadPoolExecutorConfig, nettyClientConfig);
	}

	@Override
	protected void running() {

	}

	/**
	 * 移除客户端
	 * 
	 * @param serverId
	 */
	public void removeTcpClient(int serverId) {
		multiTcpClient.removeTcpClient(serverId);
		serverMap.remove(serverId);
	}

	/**
	 * 添加连接服务器
	 * 
	 * @param port
	 *            端口
	 * @param serverInfo
	 */
	public void addTcpClient(ServerInfo serverInfo, int port) {
		addTcpClient(serverInfo, port, new MutilNettyClientChannelInitializer(this, serverInfo));
	}

	/**
	 * 添加连接大厅服务器
	 * 
	 * @param serverInfo
	 */
	public void addTcpClient(ServerInfo serverInfo, int port, ChannelInitializer<SocketChannel> channelInitializer) {
		if (multiTcpClient.containsKey(serverInfo.getId())) {
			return;
		}
		NettyClientConfig nettyClientConfig = createNettyClientConfig(serverInfo, port);
		multiTcpClient.addTcpClient(this, nettyClientConfig, channelInitializer);
	}

	/**
	 * 创建连接大厅配置文件
	 * 
	 * @param serverInfo
	 * @param port
	 * @return
	 */
	private NettyClientConfig createNettyClientConfig(ServerInfo serverInfo, int port) {
		NettyClientConfig conf = new NettyClientConfig();
		conf.setType(ServerType.GATE);
		conf.setId(serverInfo.getId());
		conf.setMaxConnectCount(getNettyClientConfig().getMaxConnectCount());
		conf.setIp(serverInfo.getIp());
		conf.setPort(port);
		conf.setGroupThreadNum(getNettyClientConfig().getGroupThreadNum());
		return conf;
	}

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
	 * 广播所有服务器消息：注意，这里并不是向每个session广播，因为有可能有多个连接到同一个服务器
	 *
	 * @param obj
	 * @return
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
	 * 发送消息
	 * 
	 * @param serverId
	 *            目标服务器ID
	 * @param msg
	 * @return
	 */
	public boolean sendMsg(Integer serverId, Object msg) {
		if (multiTcpClient == null) {
			return false;
		}
		IDMessage idm = new IDMessage(null, msg, 0);
		return multiTcpClient.sendMsg(serverId, idm);
	}

	/**
	 * 多客户端连接初始化 <br>
	 * 消息头为12
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年8月28日 下午5:49:23
	 */
	public class MutilNettyClientChannelInitializer extends DefaultClientChannelInitializer {

		public MutilNettyClientChannelInitializer(NettyClientService nettyClientService, ServerInfo serverInfo) {
			super(nettyClientService, serverInfo);
		}

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new DefaultOutBoundHandler());
			ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(50 * 1024, 0, 4)); // 消息包格式:长度(4)+角色ID(8)+消息ID(4)+内容
			ch.pipeline().addLast(new DefaultMessageCodec(12)); // 消息加解密
			ch.pipeline().addLast(new DefaultClientInBoundHandler(nettyClientService,serverInfo)); // 消息处理器
		}

	}

}
