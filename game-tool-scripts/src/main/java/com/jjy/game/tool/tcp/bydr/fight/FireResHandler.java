package com.jjy.game.tool.tcp.bydr.fight;

import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.bydr.BydrFightMessage.FireResponse;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;

/**
 * 开炮
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年10月20日 上午11:26:26
 */
@HandlerEntity(mid=MID.FireRes_VALUE,msg=FireResponse.class)
public class FireResHandler extends TcpHandler {

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
