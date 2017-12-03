package com.jzy.game.gate.tcp.role;

import com.jzy.game.message.Mid.MID;
import com.jzy.game.message.hall.HallLoginMessage.QuitRequest;
import com.jzy.game.model.constant.Reason;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.gate.script.IUserScript;

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
