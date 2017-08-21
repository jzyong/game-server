/**
 * <p>
 * 集群没有管道，管道能节约服务器的socket连接<br>
 * 脚本key为脚本文件名称<br>
 * 脚本支持事务操作<br>
 * 分布式锁，后期可以加入redisson进行实现
 * </p>
 * */

package com.jzy.game.engine.redis;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.redis.config.JedisClusterConfig;
import com.jzy.game.engine.util.FileUtil;

import org.slf4j.Logger;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

/**
 * redis集群管理类
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年8月18日 下午5:32:34
 */
public class RedisManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisManager.class);
	private static JedisCluster jedisCluster;
	private static RedisManager redisManager;

	private Map<String, String> keysShaMap; // key:脚本名称

	/**
	 * 
	 * @param configPath
	 *            redis配置文件路径
	 */
	public RedisManager(String configPath) {
		this(loadJedisClusterConfig(configPath));
	}

	public RedisManager(JedisClusterConfig config) {
		HashSet<HostAndPort> jedisClusterNodes = new HashSet<>();
		config.getNodes().forEach(node -> {
			if (node == null) {
				return;
			}
			try {
				if (node.getIp() != null && node.getIp().length() > 5) {
					jedisClusterNodes.add(new HostAndPort(node.getIp(), node.getPort()));
				}
			} catch (Exception e) {
				LOGGER.error(node.toString(), e);
			}
		});
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		poolConfig.setMaxTotal(config.getPoolMaxTotal());
		poolConfig.setMaxIdle(config.getPoolMaxIdle());
		poolConfig.setMaxWaitMillis(config.getMaxWaitMillis());
		poolConfig.setTimeBetweenEvictionRunsMillis(config.getTimeBetweenEvictionRunsMillis());
		poolConfig.setMinEvictableIdleTimeMillis(config.getMinEvictableIdleTimeMillis());
		poolConfig.setSoftMinEvictableIdleTimeMillis(config.getSoftMinEvictableIdleTimeMillis());
		poolConfig.setTestOnBorrow(config.isTestOnBorrow());
		poolConfig.setTestWhileIdle(config.isTestWhileIdle());
		poolConfig.setTestOnReturn(config.isTestOnReturn());
		jedisCluster = new JedisCluster(jedisClusterNodes, config.getConnectionTimeout(), config.getSoTimeout(),
				config.getMaxRedirections(), poolConfig);
	}

	private static JedisClusterConfig loadJedisClusterConfig(String configPath) {
		JedisClusterConfig jedisClusterConfig = FileUtil.getConfigXML(configPath, "jedisClusterConfig.xml",
				JedisClusterConfig.class);
		if (jedisClusterConfig == null) {
			LOGGER.error("redis配置{}未找到", configPath);
			System.exit(1);
		}
		return jedisClusterConfig;
	}

	public static JedisCluster getJedisCluster() {
		return jedisCluster;
	}

	public static RedisManager getInstance() {
		return redisManager;
	}

	public static void setRedisManager(RedisManager redisManager) {
		RedisManager.redisManager = redisManager;
	}

	/**
	 * 初始化脚本
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年8月7日 下午6:18:37
	 * @param path
	 *            脚本路径
	 */
	public void initScript(String configPath) {
		try {
			String path = configPath + File.separator + "lua"; // lua脚本路径
			List<File> sources = new ArrayList<>();
			FileUtil.getFiles(path, sources, ".lua", null);
			if (sources.size() < 1) {
				LOGGER.warn("{}目录无任何lua脚本");
				return;
			}
			for (File file : sources) {
				String fileName = file.getName().substring(0, file.getName().indexOf("."));
				scriptFlush(fileName);
				loadScript(path, fileName);
			}

		} catch (Exception e) {
			LOGGER.error("redis 脚本", e);
		}

	}

	/**
	 * 清除脚本缓存
	 */
	public void scriptFlush(String fileName) {
		RedisManager.getJedisCluster().scriptFlush(fileName.getBytes());
	}

	/**
	 * 初始化脚本
	 * 
	 * @param path
	 *            脚本所在路径
	 * @param jedis
	 * @param fileName
	 *            脚本文件名称
	 * @throws Exception
	 */
	public void loadScript(String path, String fileName) throws Exception {
		String script = FileUtil.readTxtFile(path + File.separator, fileName + ".lua");
		if (script == null || script.length() < 1) {
			throw new Exception(path + "/" + fileName + ".lua 加载出错");
		}
		String hash = RedisManager.getJedisCluster().scriptLoad(script, fileName);
		if (hash == null || hash.length() < 1) {
			throw new Exception(fileName + ".lua 脚本注入出错");
		}
		if (keysShaMap == null) {
			keysShaMap = new HashMap<>();
		}
		LOGGER.debug("Redis脚本：{}-->{}", fileName, hash);
		this.keysShaMap.put(fileName, hash);
	}

	/**
	 * 获取脚本 sha
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年8月7日 下午6:05:24
	 * @param fileName
	 *            脚本名称
	 * @return
	 */
	private String getSha(String fileName) {
		if (keysShaMap.containsKey(fileName)) {
			return keysShaMap.get(fileName);
		}
		LOGGER.warn(String.format("脚本 %s没初始化", fileName));
		return null;
	}

	/**
	 * 执行脚本
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年8月7日 下午6:10:31
	 * @param scriptName
	 *            脚本文件名称
	 * @param keys
	 *            redis key列表
	 * @param args
	 *            参数集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T executeScript(String scriptName, List<String> keys, List<String> args) {
		String sha = getSha(scriptName);
		if (sha == null) {
			return null;
		}
		Object object = RedisManager.getJedisCluster().evalsha(sha, keys, args);
		if (object == null) {
			return null;
		}
		return (T) object;
	}
}
