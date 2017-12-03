package com.jzy.game.hall.tcp.packet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.message.Mid.MID;
import com.jzy.game.message.hall.HallPacketMessage.UseItemRequest;
import com.jzy.game.model.constant.Reason;
import com.jzy.game.model.struct.Role;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.hall.manager.PacketManager;
import com.jzy.game.hall.manager.RoleManager;

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
