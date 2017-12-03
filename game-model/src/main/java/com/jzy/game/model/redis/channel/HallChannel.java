package com.jzy.game.model.redis.channel;

/**
 * 大厅订阅发布 通道
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月10日 下午2:36:31
 */
public enum HallChannel {
	/** 登录大厅 */
	LoginHall,
	/**大厅金币更新*/
	HallGoldChange,
	
	;
	public static String[] getChannels() {
		HallChannel[] values = HallChannel.values();
		String[] channels = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			channels[i] = values[i].name();
		}
		return channels;
	}
}
