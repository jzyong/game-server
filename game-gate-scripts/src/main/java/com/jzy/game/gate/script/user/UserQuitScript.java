package com.jzy.game.gate.script.user;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.message.hall.HallLoginMessage.QuitRequest;
import com.jzy.game.message.hall.HallLoginMessage.QuitSubGameRequest;
import com.jzy.game.model.constant.Config;
import com.jzy.game.model.constant.Reason;
import com.jzy.game.gate.manager.ServerManager;
import com.jzy.game.gate.manager.UserSessionManager;
import com.jzy.game.gate.script.IUserScript;
import com.jzy.game.gate.server.handler.GateTcpUserServerHandler;
import com.jzy.game.gate.struct.UserSession;

/**
 * 角色退出
 * <p>TODO 需要向大厅服、游戏服广播,断线重连和玩家真实退出的不同处理</p>
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年7月26日 下午4:51:31
 */
public class UserQuitScript implements IUserScript {
	private static final Logger LOGGER=LoggerFactory.getLogger(UserQuitScript.class);

	@Override
	public void quit(IoSession session, Reason reason) {
		Object attribute = session.getAttribute(Config.USER_SESSION);
		if(attribute==null){
			LOGGER.warn("session 为空 ：{}",reason.toString());
			return;
		}
		
		UserSession userSession=(UserSession)attribute;
		
		
		//是否连接子游戏
		if(userSession.getGameSession()!=null){
			QuitSubGameRequest.Builder builder=QuitSubGameRequest.newBuilder();
			builder.setRid(userSession.getRoleId());
			userSession.sendToGame(builder.build());
			userSession.removeGame();
		}
		
		//是否连接大厅服
		if(userSession.getHallSession()!=null){
			QuitRequest.Builder builder=QuitRequest.newBuilder();
			builder.setRid(userSession.getRoleId());
			userSession.sendToHall(builder.build());
			userSession.removeHall();
		}
		if(Reason.SessionIdle==reason||Reason.GmTickRole==reason||Reason.ServerClose==reason){
			session.closeOnFlush();
			UserSessionManager.getInstance().quit(userSession, reason);
		}
		
	}

	
}
