package com.jzy.game.gate.tcp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.message.Mid.MID;
import com.jzy.game.message.ServerMessage.ServerEventRequest;
import com.jzy.game.model.constant.Reason;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.gate.manager.UserSessionManager;
import com.jzy.game.gate.script.IUserScript;
import com.jzy.game.gate.struct.UserSession;

/**
 * 事件消息
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年10月17日 下午5:05:36
 */
@HandlerEntity(mid=MID.ServerEventReq_VALUE,msg=ServerEventRequest.class)
public class ServerEventHandler extends TcpHandler {
	private static final Logger LOGGER=LoggerFactory.getLogger(ServerEventHandler.class);
	@Override
	public void run() {
		ServerEventRequest request=getMsg();
		switch (request.getType()) {
		case 1:	//gm踢玩家下线
			tickRole(request);
			break;

		default:
			break;
		}
		LOGGER.info("处理事件{}",request.toString());
	}
	
	/**
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年10月17日 下午5:08:15
	 * @param request
	 */
	private void tickRole(ServerEventRequest request) {
		UserSession userSession = UserSessionManager.getInstance().getUserSessionbyRoleId(request.getId());
		if(userSession==null) {
			return;
		}
		ScriptManager.getInstance().getBaseScriptEntry().executeScripts(IUserScript.class, script->script.quit(userSession.getClientSession(), Reason.GmTickRole));
	}

}
