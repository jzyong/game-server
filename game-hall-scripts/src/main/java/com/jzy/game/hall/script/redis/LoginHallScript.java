package com.jzy.game.hall.script.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.redis.IPubSubScript;
import com.jzy.game.engine.redis.jedis.JedisPubSubMessage;
import com.jzy.game.model.redis.channel.HallChannel;

/**
 * 登录大厅脚本
 * <p>
 * 监听玩家登录其他大厅服务器，移除在本服务器的相关信息
 * </p>
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月10日 下午2:19:13
 */
public class LoginHallScript implements IPubSubScript {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginHallScript.class);

	@Override
	public void onMessage(String channel, JedisPubSubMessage message) {
		if (!HallChannel.LoginHall.name().equals(channel)) {
			return;
		}
		LOGGER.debug(message.toString());
	}

}
