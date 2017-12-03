package com.jzy.game.gate.tcp.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.message.Mid.MID;
import com.jzy.game.message.hall.HallChatMessage.ChatResponse;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.gate.manager.UserSessionManager;
import com.jzy.game.gate.struct.UserSession;

/**
 * 聊天消息返回
 * 
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年9月13日 下午3:49:36
 */
@HandlerEntity(mid=MID.ChatRes_VALUE,msg=ChatResponse.class)
public class ChatResHandler extends TcpHandler {
	private static final Logger LOGGER=LoggerFactory.getLogger(ChatResHandler.class);
	@Override
	public void run() {
		ChatResponse res=getMsg();
		switch (res.getChatType()) {
		case PRIVATE:	//在当前网关则转发
			UserSession userSession = UserSessionManager.getInstance().getUserSessionbyRoleId(this.rid);
			if(userSession!=null) {
				userSession.sendToClient(res);
			}
			break;
		case WORLD:		//广播给所有玩家
		case PMD:
			UserSessionManager.getInstance().broadcast(res);
			break;
		default:
			break;
		}
	}

}
