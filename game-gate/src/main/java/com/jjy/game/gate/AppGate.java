package com.jjy.game.gate;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.gate.manager.MongoManager;
import com.jjy.game.gate.server.GateServer;
import com.jjy.game.model.redis.key.GateKey;
import com.jjy.game.model.redis.key.HallKey;
import com.jzy.game.engine.redis.jedis.JedisClusterConfig;
import com.jzy.game.engine.redis.jedis.JedisManager;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.engine.util.FileUtil;

/**
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
 * 启动类
 *
 */
public class AppGate {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppGate.class);

	private static String configPath;
	static JedisManager redisManager;
	private static GateServer gateServer;

	public static void main(String[] args) {
		initConfigPath();

		// redis
		JedisClusterConfig jedisClusterConfig = FileUtil.getConfigXML(configPath, "jedisClusterConfig.xml",
				JedisClusterConfig.class);
		if (jedisClusterConfig == null) {
			LOGGER.error("redis配置{}未找到", configPath);
			System.exit(1);
		}
		redisManager = new JedisManager(jedisClusterConfig);

		// 创建mongodb连接
		MongoManager.getInstance().createConnect(configPath);

		// 加载脚本
		ScriptManager.getInstance().init(null);

		// 通信服务
		gateServer = new GateServer();
		new Thread(gateServer).start();
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

	public static String getConfigPath() {
		return configPath;
	}

	public static GateServer getHallServer() {
		return gateServer;
	}

}
