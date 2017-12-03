package com.jzy.game.bydr.world.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.bydr.server.Bydr2ClusterClient;
import com.jzy.game.bydr.server.BydrHttpServer;
import com.jzy.game.bydr.world.AppBydrWorld;
import com.jzy.game.engine.mina.config.MinaClientConfig;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.engine.redis.jedis.JedisPubListener;
import com.jzy.game.engine.util.FileUtil;
import com.jzy.game.model.redis.channel.BydrWorldChannel;
import com.jzy.game.model.timer.GameServerCheckTimer;

/**
 * 世界服务器
 * <p>暂时共用捕鱼服务器类，有修改单独写</p>
 * @author JiangZhiYong
 * @QQ 359135103 2017年8月1日 下午5:34:59
 */
public class BydrWorldServer implements Runnable {
	private static final Logger LOGGER=LoggerFactory.getLogger(BydrWorldServer.class);

	/** 连接集群服 （获取各服务器信息） */
	private Bydr2ClusterClient bydr2ClusterClient;
	/**http服务器*/
	private BydrHttpServer gameHttpServer;
	/** 服务器状态监测 */
	private GameServerCheckTimer gameServerCheckTimer;
	/**redis订阅发布*/
	private final JedisPubListener bydrPubListener;
	
	public BydrWorldServer(String configPath) {

		// 加载连接集群配置
		MinaClientConfig minaClientConfig_cluster = FileUtil.getConfigXML(configPath, "minaClientConfig_cluster.xml",
				MinaClientConfig.class);
		if (minaClientConfig_cluster == null) {
			LOGGER.error("{}/minaClientConfig_hall.xml未找到", configPath);
			System.exit(0);
		}

		// HTTP
		MinaServerConfig minaServerConfig_http = FileUtil.getConfigXML(configPath, "minaServerConfig_http.xml",
				MinaServerConfig.class);
		gameHttpServer = new BydrHttpServer(minaServerConfig_http);
		
		this.bydr2ClusterClient = new Bydr2ClusterClient(minaClientConfig_cluster);

		this.gameServerCheckTimer = new GameServerCheckTimer(bydr2ClusterClient, null,
				minaClientConfig_cluster);
		this.bydrPubListener=new JedisPubListener(BydrWorldChannel.getChannels());
	}
	
	public static final BydrWorldServer getInstance(){
		return AppBydrWorld.getBydrWorldServer();
	}
	
	

	public BydrHttpServer getGameHttpServer() {
		return gameHttpServer;
	}

	@Override
	public void run() {
		new Thread(gameHttpServer).start();
		new Thread(bydr2ClusterClient).start();
		this.gameServerCheckTimer.start();
		new Thread(bydrPubListener).start();
	}

}
