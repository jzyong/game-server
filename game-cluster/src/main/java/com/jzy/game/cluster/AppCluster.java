package com.jzy.game.cluster;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.cluster.server.ClusterServer;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.engine.redis.jedis.JedisClusterConfig;
import com.jzy.game.engine.thread.ThreadPoolExecutorConfig;
import com.jzy.game.engine.util.FileUtil;
import com.jzy.game.engine.util.SysUtil;


/**
 * 
                   _oo0oo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  0\  =  /0
                ___/`---'\___
              .' --|     |-- '.
             / --|||  :  |||-- \
            / _||||| -:- |||||- \
           |   | \--  -  --/ |   |
           | \_|  ''\---/''  |_/ |
           \  .-\__  '-'  ___/-. /
         ___'. .'  /--.--\  `. .'___
      ."" '<  `.___\_<|>_/___.' >' "".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `_.   \_ __\ /__ _/   .-` /  /
 =====`-.____`.___ \_____/___.-`___.-'=====
                   `=---=`

 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

                         佛祖保佑     永无BUG
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 
 * 
 */

/**
 * 启动类
 * <p>
 * 服务器注册管理中心
 * <br>是否可用zookeeper替换
 * </p>
 *
 * @author JiangZhiYong
 * @date 2017-03-31 QQ:359135103
 */
public class AppCluster {
	private static final Logger log = LoggerFactory.getLogger(AppCluster.class);

	private static ClusterServer clusterServer;
	public static String path = "";

	public static void main(String[] args) {
		File file = new File(System.getProperty("user.dir"));
		if ("target".equals(file.getName())) {
			path = file.getPath() + File.separatorChar + "config";
		} else {
			path = file.getPath() + File.separatorChar + "target" + File.separatorChar + "config";
		}
		log.info("配置路径为：" + path);
		JedisClusterConfig jedisClusterConfig = FileUtil.getConfigXML(path, "jedisclusterConfig.xml",
				JedisClusterConfig.class);
		if (jedisClusterConfig == null) {
			SysUtil.exit(AppCluster.class, null, "jedisclusterConfig");
		}
		ThreadPoolExecutorConfig threadExcutorConfig_http = FileUtil.getConfigXML(path, "threadExcutorConfig_http.xml",
				ThreadPoolExecutorConfig.class);
		if (threadExcutorConfig_http == null) {
			SysUtil.exit(AppCluster.class, null, "threadExcutorConfig_http");
		}
		ThreadPoolExecutorConfig threadExcutorConfig_tcp = FileUtil.getConfigXML(path, "threadExcutorConfig_tcp.xml",
				ThreadPoolExecutorConfig.class);
		if (threadExcutorConfig_tcp == null) {
			SysUtil.exit(AppCluster.class, null, "threadExcutorConfig_tcp");
		}
		MinaServerConfig minaServerConfig_http = FileUtil.getConfigXML(path, "minaServerConfig_http.xml",
				MinaServerConfig.class);
		if (minaServerConfig_http == null) {
			SysUtil.exit(AppCluster.class, null, "minaServerConfig_http");
		}
		MinaServerConfig minaServerConfig_tcp = FileUtil.getConfigXML(path, "minaServerConfig_tcp.xml",
				MinaServerConfig.class);
		if (minaServerConfig_tcp == null) {
			SysUtil.exit(AppCluster.class, null, "minaServerConfig_tcp");
		}
		//RedisManager redisManager = new RedisManager(jedisClusterConfig);

		clusterServer = new ClusterServer(threadExcutorConfig_http, minaServerConfig_http, threadExcutorConfig_tcp,
				minaServerConfig_tcp);
		new Thread(clusterServer).start();
	}

	public static ClusterServer getClusterServer() {
		return clusterServer;
	}
	
}
