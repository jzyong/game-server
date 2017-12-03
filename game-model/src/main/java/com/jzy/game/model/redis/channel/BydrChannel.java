package com.jzy.game.model.redis.channel;

/**
 * 捕鱼达人监听通道
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月10日 下午2:40:49
 */
public enum BydrChannel {
	/** 登录大厅 */
	LoginHall,
	/** 报名参加竞技赛结果 */
	ApplyAthleticsRes,
	/**大厅金币更新*/
	HallGoldChange,
	;

	public static String[] getChannels() {
		BydrChannel[] values = BydrChannel.values();
		String[] channels = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			channels[i] = values[i].name();
		}
		return channels;
	}
}
