package com.jzy.game.hall.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 公会管理
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月22日 上午9:56:19
 */
public class GuildManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(GuildManager.class);
	private static volatile GuildManager guildManager;

	private GuildManager() {

	}

	public static GuildManager getInstance() {
		if (guildManager == null) {
			synchronized (GuildManager.class) {
				if (guildManager == null) {
					guildManager = new GuildManager();
				}
			}
		}
		return guildManager;
	}

}
