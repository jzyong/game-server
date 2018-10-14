package com.jzy.game.bydr;

import java.io.File;
import java.util.Arrays;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RFuture;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.bydr.manager.MongoManager;
import com.jzy.game.bydr.server.BydrServer;
import com.jzy.game.bydr.struct.role.Role;
import com.jzy.game.engine.redis.jedis.JedisManager;
import com.jzy.game.engine.redis.redisson.RedissonManager;
import com.jzy.game.engine.script.ScriptManager;

/**
 * 捕鱼达人启动类
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年6月28日 上午11:30:49
 */
public final class AppBydr {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppBydr.class);
	private static String configPath;
	protected static JedisManager redisManager;
	private static BydrServer bydrServer;

	private AppBydr() {
	}

	public static void main(String[] args) {
		initConfigPath();
		// redis
		redisManager = new JedisManager(configPath);
		redisManager.initScript(configPath);
		JedisManager.setRedisManager(redisManager);
//		String result = RedisManager.getInstance().executeScript("Test", Arrays.asList("foo"), Arrays.asList("jzy"));
//		LOGGER.debug("redis 脚本测试:" + result);
//		RedissonManager.connectRedis(configPath);

		
		// 创建mongodb连接
		MongoManager.getInstance().createConnect(configPath);

		// 加载脚本
		ScriptManager.getInstance().init(str -> System.exit(0));

		// 启动通信连接
		bydrServer = new BydrServer(configPath);
		new Thread(bydrServer).start();
	}

	private static void initConfigPath() {
		File file = new File(System.getProperty("user.dir"));
		if ("target".equals(file.getName())) {
			configPath = file.getPath() + File.separatorChar + "config";
		} else {
			configPath = file.getPath() + File.separatorChar + "target" + File.separatorChar + "config";
		}
		LOGGER.info("配置路径为：" + configPath);
	}

	public static BydrServer getBydrServer() {
		return bydrServer;
	}

	public static String getConfigPath() {
		return configPath;
	}

}
