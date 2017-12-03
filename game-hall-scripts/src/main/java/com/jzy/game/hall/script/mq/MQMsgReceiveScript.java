package com.jzy.game.hall.script.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.mq.IMQScript;

/**
 * MQ 消息接收
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年7月28日 下午3:01:03
 */
public class MQMsgReceiveScript implements IMQScript {
	private static final Logger LOGGER=LoggerFactory.getLogger(MQMsgReceiveScript.class);

	@Override
	public void onMessage(String msg) {
		LOGGER.info(msg);
	}

	
}
