package com.jzy.game.gate.tcp.role;

import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.message.Mid.MID;
import com.jzy.game.message.hall.HallLoginMessage.QuitResponse;
import com.jzy.game.model.constant.Config;
import com.jzy.game.model.constant.Reason;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.gate.manager.UserSessionManager;
import com.jzy.game.gate.struct.UserSession;

/**
 * 退出游戏返回<br>
 * 
 * @note 在请求消息时已经移除了角色的连接会话信息,所有返回消息会话是游戏内部会话，不能从session中获取属性,不能关闭
 * @author JiangZhiYong
 * @QQ 359135103 2017年10月20日 下午2:18:08
 */
@HandlerEntity(mid = MID.QuitRes_VALUE, msg = QuitResponse.class)
public class QuitResHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(QuitResHandler.class);

	@Override
	public void run() {
		QuitResponse res = getMsg();
		UserSession userSession=UserSessionManager.getInstance().getUserSessionbyRoleId(rid);
		if(userSession==null) {
			LOGGER.warn("角色{}会话已遗失",rid);
			return ;
		}

		if (userSession.getClientSession() != null) {
			userSession.sendToClient(res);
			userSession.getClientSession().closeOnFlush();
			LOGGER.info("{}退出游戏", userSession.getRoleId());
		}
		
		//返回结果再移除，防止一些消息得不到转发失败
		UserSessionManager.getInstance().quit(userSession, Reason.UserQuit);

	}

}
