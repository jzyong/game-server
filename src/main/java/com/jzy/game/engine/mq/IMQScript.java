package com.jzy.game.engine.mq;

import com.jzy.game.engine.script.IScript;

/**
 * MQ 消息处理脚本
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年7月28日 上午10:39:14
 */
public interface IMQScript extends IScript {

	/**
	 * ＭＱ消息接收处理
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年7月28日 上午10:39:59
	 * @param msg
	 */
	default void onMessage(String msg){
		
	}
}
