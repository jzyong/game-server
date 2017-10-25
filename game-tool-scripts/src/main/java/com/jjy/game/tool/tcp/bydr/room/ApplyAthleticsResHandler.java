package com.jjy.game.tool.tcp.bydr.room;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.bydr.BydrRoomMessage.ApplyAthleticsResponse;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;

/**
 * 竞技场报名
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年8月3日 下午6:26:10
 */
@HandlerEntity(mid=MID.ApplyAthleticsRes_VALUE,msg=ApplyAthleticsResponse.class)
public class ApplyAthleticsResHandler extends TcpHandler {
	private static final Logger LOGGER=LoggerFactory.getLogger(ApplyAthleticsResHandler.class);
	
	@Override
	public void run() {
		ApplyAthleticsResponse res=getMsg();
		LOGGER.info("竞技赛报名：{}",res.toString());
	}

}
