package com.jjy.game.tool.tcp.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.system.SystemMessage.HeartResponse;
import com.jjy.game.tool.client.PressureServiceThread;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;

/**
 * 心跳返回
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年9月1日 下午5:08:56
 */
@HandlerEntity(mid=MID.HeartRes_VALUE,msg=HeartResponse.class)
public class HeartResHandler extends TcpHandler {
	private static final Logger LOGGER =LoggerFactory.getLogger(HeartResHandler.class);
 
	public void run() {
		HeartResponse res=getMsg();
		long sendTime = (Long) session.getAttribute(PressureServiceThread.SEND_TIME, Long.MAX_VALUE);
//			LOGGER.warn("结果：{} 耗时：{}ms", res.getServerTime(), (System.currentTimeMillis() - sendTime));
	}

}
