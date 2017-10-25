package com.jjy.game.hall.script.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mongodb.morphia.annotations.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.hall.manager.ConfigManager;
import com.jjy.game.model.mongo.bydr.dao.CFishDao;
import com.jjy.game.model.mongo.bydr.entity.CFish;
import com.jjy.game.model.script.IConfigScript;

/**
 * 加载配置脚本
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年10月18日 下午3:21:41
 */
public class LoadConfigScript implements IConfigScript {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoadConfigScript.class);

	@Override
	public String reloadConfig(List<String> tables) {
		StringBuffer sb = new StringBuffer();
		synchronized (this) {
			try {
				// 鱼配置
				if (containTable(tables, CFish.class)) {
					Map<Integer, CFish> fishMap = new ConcurrentHashMap<>();
					CFishDao.getAll().forEach(fish -> {
						fishMap.put(fish.getId(), fish);
					});
					ConfigManager.getInstance().setFishMap(fishMap);
					sb.append("CFish:").append(fishMap.size());
				}
				
				//TODO 其他配置
				
			} catch (Exception e) {
				LOGGER.error("加载配置", e);
			}

		}

		return sb.toString();
	}



}
