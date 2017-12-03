package com.jzy.game.hall.tcp.login;

import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.hall.manager.RoleManager;
import com.jzy.game.message.Mid.MID;
import com.jzy.game.message.hall.HallLoginMessage.QuitRequest;
import com.jzy.game.message.hall.HallLoginMessage.QuitResponse;
import com.jzy.game.model.constant.Reason;

/**
 * 退出游戏
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年7月27日 上午9:49:28
 */
@HandlerEntity(mid=MID.QuitReq_VALUE,msg=QuitRequest.class)
public class QuitHandler extends TcpHandler {

	@Override
	public void run() {
		QuitRequest req=getMsg();
		RoleManager.getInstance().quit(rid, Reason.UserQuit);
		
		QuitResponse.Builder builder=QuitResponse.newBuilder();
		builder.setResult(0);
		sendIdMsg(builder.build());
	}

}
