package com.jzy.game.bydr.server;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.bydr.AppBydr;
import com.jzy.game.engine.mina.config.MinaClientConfig;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.engine.mina.service.ClientServerService;
import com.jzy.game.engine.mina.service.GameHttpSevice;
import com.jzy.game.engine.netty.config.NettyClientConfig;
import com.jzy.game.engine.netty.service.MutilNettyTcpClientService;
import com.jzy.game.engine.netty.service.SingleNettyTcpClientService;
import com.jzy.game.engine.redis.jedis.JedisPubListener;
import com.jzy.game.engine.server.BaseServerConfig;
import com.jzy.game.engine.server.IMutilTcpClientService;
import com.jzy.game.engine.server.ITcpClientService;
import com.jzy.game.engine.server.ServerInfo;
import com.jzy.game.engine.server.Service;
import com.jzy.game.engine.thread.ThreadPoolExecutorConfig;
import com.jzy.game.engine.thread.ThreadType;
import com.jzy.game.engine.util.FileUtil;
import com.jzy.game.engine.util.SysUtil;
import com.jzy.game.message.ServerMessage;
import com.jzy.game.model.constant.Config;
import com.jzy.game.model.constant.NetPort;
import com.jzy.game.model.redis.channel.BydrChannel;
import com.jzy.game.model.timer.GameServerCheckTimer;

/**
 * 捕鱼服务器
 * 
 * <p>
 * 可接收大厅服转发来的消息，和客户端直接发来的消息
 * </p>
 * <h4>mina和netty性能测试对比</h4>
 * <p>
 *  开启100个客户端，每个客户端每隔100ms发送一条消息
 * <li>在使用自定义线程池时，mina 50个连接,netty 1个连接:mina通信耗时在1~3毫秒，netty通信在0~100ms(变动幅度很大，多客户端性能较低，当单客户端发消息时变为0~3ms)</li>
 * <li>在使用自定义线程池时，mina 1个连接,netty 1个连接:mina通信耗时在1~3毫秒，netty通信耗时0~100ms. mina才启动比较耗时，然而netty才启动比较快；mina在单客户端比较耗时，多客户端比较快，netty相反</li>
 * <br><b>总结：多客户端情况下使用<code>channel.writeAndFlush()</code>代替<code>channel.write()</code></b>
 * <br><br>
 * 开启1个客户端，每个客户端每隔100ms发送一条消息
 * <li>在使用自定义线程池时，mina 1个连接,netty 1个连接:mina耗时0~3毫秒， netty耗时0~3毫秒</li>
 * 
 * TODO 添加netty多连接测试？ 
 * </p>
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年6月28日 下午3:37:19
 */
public class BydrServer implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(BydrServer.class);

	/** 连接网关 （接收网关转发过来的消息） */
	private final IMutilTcpClientService<? extends BaseServerConfig> bydr2GateClient;

	/** 连接集群服 （获取各服务器信息） */
	private final ITcpClientService<? extends BaseServerConfig> bydr2ClusterClient;

	/** 游戏前端消息服务 （消息直接从手机前端发来,如果没有直接注释掉，不经过大厅网关转发,暂时用engine封装类） */
	private ClientServerService bydrTcpServer;

	/** http服务器 */
	private final BydrHttpServer gameHttpServer;

	/** 服务器状态监测 */
	private final GameServerCheckTimer gameServerCheckTimer;

	/** redis订阅发布 */
	private final JedisPubListener bydrPubListener;

	/**
	 * mina、netty通信服务组件都实现，通过配置netty配置文件<code>NettyClientConfig.info</code> NettyFirst 选择使用netty还是mina
	 * 
	 * @param configPath
	 */
	@SuppressWarnings("unchecked")
	public BydrServer(String configPath) {

		//线程池配置
		ThreadPoolExecutorConfig threadPoolExecutorConfig = FileUtil.getConfigXML(configPath,"threadPoolExecutorConfig.xml", ThreadPoolExecutorConfig.class);
		if (threadPoolExecutorConfig == null) {
			LOGGER.error("{}/threadPoolExecutorConfig.xml未找到", configPath);
			System.exit(0);
		}
		
		// 加载连接网关配置
		MinaClientConfig minaClientConfig_gate = FileUtil.getConfigXML(configPath, "minaClientConfig_gate.xml",MinaClientConfig.class);
		NettyClientConfig nettyClientConfig_gate = FileUtil.getConfigXML(configPath, "nettyClientConfig_gate.xml",NettyClientConfig.class);
		if (minaClientConfig_gate == null&&nettyClientConfig_gate==null) {
			LOGGER.error("{}未配置网关连接客户端", configPath);
			System.exit(0);
		}

		// 加载连接集群配置
		MinaClientConfig minaClientConfig_cluster = FileUtil.getConfigXML(configPath, "minaClientConfig_cluster.xml",MinaClientConfig.class);
		NettyClientConfig nettyClinetConfig_cluster = FileUtil.getConfigXML(configPath, "nettyClientConfig_cluster.xml",NettyClientConfig.class);
		if (minaClientConfig_cluster == null&&nettyClinetConfig_cluster==null) {
			LOGGER.error("{}未配置集群连接客户端", configPath);
			System.exit(0);
		}

		// HTTP通信
		MinaServerConfig minaServerConfig_http = FileUtil.getConfigXML(configPath, "minaServerConfig_http.xml",MinaServerConfig.class);
		
		gameHttpServer = new BydrHttpServer(minaServerConfig_http);

		// 游戏前端消息服务 配置为空，不开启，开启后消息可以不经过网关直接发送到本服务器
		MinaServerConfig minaServerConfig = FileUtil.getConfigXML(configPath, "minaServerConfig.xml",MinaServerConfig.class);
		if (minaServerConfig != null) {
            bydrTcpServer = new ClientServerService(minaServerConfig);
		}

		// 如果netty 优先级高，使用Netty服务,一般不直接使用engine提供的类
		//网关
		if(nettyClientConfig_gate!=null&&"NettyFirst".equalsIgnoreCase(nettyClientConfig_gate.getInfo())){
			//TODO 需要重写channelActive 发送服务器注册消息 ，不然相当于当前客户端和网关只有一个channel连接
            bydr2GateClient =new Bydr2GateClientNetty(threadPoolExecutorConfig, nettyClientConfig_gate);
		}else{
            bydr2GateClient = new Bydr2GateClient(threadPoolExecutorConfig, minaClientConfig_gate);
		}
		
		//集群
		if (nettyClinetConfig_cluster != null && "NettyFirst".equalsIgnoreCase(nettyClinetConfig_cluster.getInfo())) {
			bydr2ClusterClient = new SingleNettyTcpClientService(nettyClinetConfig_cluster);
		} else {
            bydr2ClusterClient = new Bydr2ClusterClient(minaClientConfig_cluster);
		}
		

		// 状态监控
        gameServerCheckTimer = new GameServerCheckTimer(bydr2ClusterClient, bydr2GateClient,
				bydr2GateClient instanceof Bydr2GateClient?minaClientConfig_gate:nettyClientConfig_gate);
		// 订阅发布
        bydrPubListener = new JedisPubListener(BydrChannel.getChannels());
		
		//设置配置相关常量
		Config.SERVER_ID = minaClientConfig_gate.getId();
		Config.SERVER_NAME = minaClientConfig_gate.getName();
	}

	public static BydrServer getInstance() {
		return AppBydr.getBydrServer();
	}

	@Override
	public void run() {
		new Thread(bydr2GateClient).start();
		new Thread(bydr2ClusterClient).start();

		if (bydrTcpServer != null) {
			new Thread(bydrTcpServer).start();
		}
        gameServerCheckTimer.start();
		new Thread(gameHttpServer).start();
		new Thread(bydrPubListener).start();
	}


	public IMutilTcpClientService<? extends BaseServerConfig> getBydr2GateClient() {
		return bydr2GateClient;
	}
	
	/**
	 * 获取线程 在连接网关服的service中获取
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年9月14日 下午4:33:48
	 * @param threadType
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public  <T extends Executor> T getExecutor(ThreadType threadType) {
		return (T) ((Service)bydr2GateClient).getExecutor(threadType);
	}

	public ITcpClientService<? extends BaseServerConfig> getBydr2ClusterClient() {
		return bydr2ClusterClient;
	}

	public GameHttpSevice getGameHttpServer() {
		return gameHttpServer;
	}
	
	/**
	 * 更新可用网关服务器信息
	 * 
	 * @param info
	 */
	public void updateGateServerInfo(ServerMessage.ServerInfo info) {
		ServerInfo serverInfo = getBydr2GateClient().getServers().get(info.getId());
		if (serverInfo == null) {
			serverInfo = getServerInfo(info);
			if(getBydr2GateClient() instanceof Bydr2GateClient){
				Bydr2GateClient service=(Bydr2GateClient)getBydr2GateClient();
				service.addTcpClient(serverInfo, NetPort.GATE_GAME_PORT,service.new MutilConHallHandler(serverInfo, service)); // TODO 暂时，网关服有多个tcp端口
			}else{
				getBydr2GateClient().addTcpClient(serverInfo,  NetPort.GATE_GAME_PORT);
			}
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
		getBydr2GateClient().getServers().put(info.getId(), serverInfo);
	}

	/**
	 * 消息转换
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年8月29日 下午2:21:52
	 * @param info
	 * @return
	 */
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
		serverInfo.setFreeMemory(info.getFreeMemory());
		serverInfo.setTotalMemory(info.getTotalMemory());
		serverInfo.setVersion(info.getVersion());
		return serverInfo;
	}

}
