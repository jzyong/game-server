package com.jjy.game.bydr.tcp.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.bydr.manager.RoleManager;
import com.jjy.game.bydr.manager.RoomManager;
import com.jjy.game.bydr.struct.role.Role;
import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.hall.HallLoginMessage.QuitSubGameRequest;
import com.jjy.game.message.hall.HallLoginMessage.QuitSubGameResponse;
import com.jjy.game.model.constant.Reason;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;

/**
 * 退出子游戏 TODO 数据持久化等处理
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月26日 下午5:34:06
 */
@HandlerEntity(mid = MID.QuitSubGameReq_VALUE, msg = QuitSubGameRequest.class)
public class QuitSubGameHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(QuitSubGameHandler.class);

	@Override
	public void run() {
		QuitSubGameRequest req = getMsg();
		LOGGER.info("{}退出捕鱼", getRid());
		Role role = RoleManager.getInstance().getRole(getRid());
		if (role == null) {
			return;
		}
		// 退出房间
		if (role.getRoomId() > 0) {
			RoomManager.getInstance().quitRoom(role, role.getRoomId());
		}

		RoleManager.getInstance().quit(role, Reason.UserQuit);

		QuitSubGameResponse.Builder builder = QuitSubGameResponse.newBuilder();
		builder.setResult(0);
		sendIdMsg(builder.build());
	}

}
