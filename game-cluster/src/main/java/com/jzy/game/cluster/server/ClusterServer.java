package com.jzy.game.cluster.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.cluster.AppCluster;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.engine.thread.ThreadPoolExecutorConfig;
import com.jzy.game.engine.util.SysUtil;
import com.jzy.game.model.constant.NetPort;

/**
 * 集群管理服务器
 *
 * @author JiangZhiYong
 * @date 2017-03-31 QQ:359135103
 */
public class ClusterServer implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(ClusterServer.class);

	private final ClusterHttpServer clusterHttpServer;
	private final ClusterTcpServer clusterTcpServer;

	public ClusterServer(ThreadPoolExecutorConfig defaultThreadExcutorConfig4HttpServer,
			MinaServerConfig minaServerConfig4HttpServer, ThreadPoolExecutorConfig defaultThreadExcutorConfig4TcpServer,
			MinaServerConfig minaServerConfig4TcpServer) {

		NetPort.CLUSTER_PORT = minaServerConfig4TcpServer.getPort();
		NetPort.CLUSTER_HTTP_PORT = minaServerConfig4HttpServer.getHttpPort();
		clusterHttpServer = new ClusterHttpServer(defaultThreadExcutorConfig4HttpServer, minaServerConfig4HttpServer);
		clusterTcpServer = new ClusterTcpServer(defaultThreadExcutorConfig4TcpServer, minaServerConfig4TcpServer);
	}

	public static ClusterServer getInstance() {
		return AppCluster.getClusterServer();
	}

	@Override
	public void run() {
		// mainServerThread.addTimerEvent(new ServerHeartTimer());

		// 启动mina相关
		log.info("ClusterServer::clusterHttpServer::start!!!");
		new Thread(clusterHttpServer).start();
		log.info("ClusterServer::clusterTcpServer::start!!!");
		new Thread(clusterTcpServer).start();

		ScriptManager.getInstance().init((str) -> SysUtil.exit(this.getClass(), null, "加载脚本错误"));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			log.error("", ex);
		}
		log.info("---->集群服启动成功<----");
	}

	public ClusterHttpServer getLoginHttpServer() {
		return clusterHttpServer;
	}

	public ClusterTcpServer getLoginTcpServer() {
		return clusterTcpServer;
	}

}
