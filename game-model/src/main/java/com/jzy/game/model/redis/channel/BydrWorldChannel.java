package com.jzy.game.model.redis.channel;

/**
 * 捕鱼达人世界服监听通道
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年7月10日 下午2:40:49
 */
public enum BydrWorldChannel {
	/**报名参加竞技赛*/
	ApplyAthleticsReq;
	
	public static String[] getChannels() {
		BydrWorldChannel[] values = BydrWorldChannel.values();
		String[] channels = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			channels[i] = values[i].name();
		}
		return channels;
	}
}
