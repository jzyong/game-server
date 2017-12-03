package com.jzy.game.hall.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.model.mongo.bydr.entity.CFish;

/**
 * 配置表
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年10月18日 下午2:31:04
 */
public class ConfigManager {
	private static final Logger LOGGER=LoggerFactory.getLogger(ConfigManager.class);
	public static volatile ConfigManager configManager;
	
	/**鱼配置信息*/
	private Map<Integer, CFish> fishMap=new ConcurrentHashMap<>();
	
	private ConfigManager() {
		
	}
	
	public static ConfigManager getInstance() {
		if(configManager==null) {
			synchronized (ConfigManager.class) {
				if(configManager==null) {
					configManager=new ConfigManager();
				}
			}
		}
		return configManager;
	}

	public Map<Integer, CFish> getFishMap() {
		return fishMap;
	}

	public void setFishMap(Map<Integer, CFish> fishMap) {
		this.fishMap = fishMap;
	}
	
	/**
	 * 鱼配置信息
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年10月18日 下午3:18:43
	 * @param configId
	 * @return
	 */
	public CFish getFish(int configId) {
		if(fishMap.containsKey(configId)) {
			return fishMap.get(configId);
		}
		LOGGER.warn("CFish配置错误:{}未配置",configId);
		return null;
	}
	
}
