package com.jzy.game.gate.tcp.role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.message.Mid.MID;
import com.jzy.game.message.hall.HallLoginMessage.QuitSubGameResponse;
import com.jzy.game.model.constant.Config;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.gate.manager.UserSessionManager;
import com.jzy.game.gate.struct.UserSession;

/**
 * 退出子游戏返回
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年10月20日 下午2:06:00
 */
@HandlerEntity(mid = MID.QuitSubGameRes_VALUE, msg = QuitSubGameResponse.class)
public class QuitSubGameResHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(QuitSubGameResHandler.class);

	@Override
	public void run() {
		QuitSubGameResponse res = getMsg();
		UserSession userSession=UserSessionManager.getInstance().getUserSessionbyRoleId(rid);
		if(userSession==null) {
//			LOGGER.warn("角色{}会话已遗失",rid);
			return ;
		}
		
//		LOGGER.info("角色{}退出：{}", userSession.getRoleId(), userSession.getServerType().toString());
		if (userSession.getClientSession() != null) {
			userSession.sendToClient(res);
			userSession.removeGame();
		}

	}

}
