package com.jjy.game.hall.script.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.model.constant.Config;
import com.jjy.game.model.redis.channel.HallChannel;
import com.jjy.game.model.redis.key.HallKey;
import com.jzy.game.engine.redis.IPubSubScript;
import com.jzy.game.engine.redis.jedis.JedisManager;
import com.jzy.game.engine.redis.jedis.JedisPubSubMessage;

/**
 * 游戏服更新大厅金币
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年9月18日 上午11:19:56
 * @deprecated 子游戏直接操作redis
 */
public class GoldUpdateScript implements IPubSubScript {
	private static final Logger LOGGER=LoggerFactory.getLogger(GoldUpdateScript.class);

	@Override
	public void onMessage(String channel, JedisPubSubMessage message) {
//		if(!HallChannel.GoldUpdat.name().equals(channel)) {
//			return;
//		}
//		if(message.getServer()!=Config.SERVER_ID) {
//			return;
//		}
//		String key = HallKey.Role_Map_Info.getKey(message.getId());
//		
//		Long finalGold = JedisManager.getJedisCluster().hincrBy(key, "gold", message.getTarget());
//		LOGGER.debug("{}更新{}-->{}",key,message.getTarget(),finalGold);
	}

}
