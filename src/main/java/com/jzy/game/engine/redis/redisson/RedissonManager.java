package com.jzy.game.engine.redis.redisson;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.connection.balancer.LoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.util.FileUtil;

/**
 * Redisson 工具
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月15日 下午3:25:55
 */
public class RedissonManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedissonManager.class);


	private static RedissonClusterConfig redissonClusterConfig;

	private static RedissonClient redisson;

	private RedissonManager() {

	}

	/**
	 * 连接服务器
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月15日 下午3:36:06
	 * @param configPath
	 */
	public static void connectRedis(String configPath) {
		if(redisson!=null) {
			LOGGER.warn("Redisson客户端已经连接");
		}
		redissonClusterConfig = FileUtil.getConfigXML(configPath, "redissonClusterConfig.xml",
				RedissonClusterConfig.class);
		if (redissonClusterConfig == null) {
			LOGGER.warn("{}/redissonClusterConfig.xml文件不存在", configPath);
			System.exit(0);
		}
		Config config = new Config();
		config.setCodec(new FastJsonCodec());
		ClusterServersConfig clusterServersConfig = config.useClusterServers();
		clusterServersConfig.setScanInterval(redissonClusterConfig.getScanInterval()); // 集群状态扫描间隔时间，单位是毫秒
		// 可以用"rediss://"来启用SSL连接
		redissonClusterConfig.getNodes().forEach(url -> clusterServersConfig.addNodeAddress(url));
		clusterServersConfig.setReadMode(redissonClusterConfig.getReadMode());
		clusterServersConfig.setSubscriptionMode(redissonClusterConfig.getSubscriptionMode());
		redisson = Redisson.create(config);
	}
	
	public static RedissonClient getRedissonClient() {
		return redisson;
	}
}
