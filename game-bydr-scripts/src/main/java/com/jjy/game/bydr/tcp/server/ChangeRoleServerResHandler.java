package com.jjy.game.bydr.tcp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.bydr.manager.RoleManager;
import com.jjy.game.bydr.struct.role.Role;
import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.ServerMessage.ChangeRoleServerResponse;
import com.jjy.game.model.constant.Reason;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;

/**
 * 改变服务器结果
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年8月3日 下午4:11:03
 */
@HandlerEntity(mid = MID.ChangeRoleServerRes_VALUE, msg = ChangeRoleServerResponse.class)
public class ChangeRoleServerResHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChangeRoleServerResHandler.class);

	@Override
	public void run() {
		ChangeRoleServerResponse res = getMsg();
		Role role = RoleManager.getInstance().getRole(rid);
		if (res.getResult() == 0 && role != null) {
			RoleManager.getInstance().quit(role, Reason.CrossServer);
		}

		LOGGER.info("角色{}跨服退出{}", rid, res.getResult());

	}

}
