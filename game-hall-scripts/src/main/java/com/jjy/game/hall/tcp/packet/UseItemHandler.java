package com.jjy.game.hall.tcp.packet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.hall.manager.PacketManager;
import com.jjy.game.hall.manager.RoleManager;
import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.hall.HallPacketMessage.UseItemRequest;
import com.jjy.game.model.constant.Reason;
import com.jjy.game.model.struct.Role;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;

/**
 * 使用物品
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年9月18日 下午3:28:53
 */
@HandlerEntity(mid=MID.UseItemReq_VALUE,msg=UseItemRequest.class)
public class UseItemHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(UseItemHandler.class);

	@Override
	public void run() {
		UseItemRequest req=getMsg();
		Role role = RoleManager.getInstance().getRole(rid);
		if(role==null) {
			return;
		}
		PacketManager.getInstance().useItem(role, req.getItemId(), 1, Reason.RoleUseItem, null);
	}

}
