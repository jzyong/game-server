package com.jzy.game.bydr.tcp.fight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.message.Mid.MID;
import com.jzy.game.message.bydr.BydrFightMessage.UseSkillRequest;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;

/**
 * 使用技能，道具
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年9月18日 下午2:44:50
 */
@HandlerEntity(mid=MID.UseSkillReq_VALUE,msg=UseSkillRequest.class)
public class UseSkillHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplyAthleticsHandler.class);

	@Override
	public void run() {
		
	}

}
