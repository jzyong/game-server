package com.jjy.game.gate.tcp.role;

import com.jjy.game.gate.script.IUserScript;
import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.hall.HallLoginMessage.QuitRequest;
import com.jjy.game.model.constant.Reason;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.engine.script.ScriptManager;

/**
 * 退出游戏
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年7月26日 下午5:27:23
 */
@HandlerEntity(mid=MID.QuitReq_VALUE,msg=QuitRequest.class)
public class QuitReqHandler extends TcpHandler {

	@Override
	public void run() {
		ScriptManager.getInstance().getBaseScriptEntry().executeScripts(IUserScript.class, script->script.quit(getSession(), Reason.UserQuit));

	}

}
