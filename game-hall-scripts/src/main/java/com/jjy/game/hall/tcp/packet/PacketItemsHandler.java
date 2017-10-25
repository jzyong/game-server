package com.jjy.game.hall.tcp.packet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.hall.manager.PacketManager;
import com.jjy.game.hall.manager.RoleManager;
import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.hall.HallPacketMessage.PacketItemsRequest;
import com.jjy.game.message.hall.HallPacketMessage.PacketItemsResponse;
import com.jjy.game.model.struct.Role;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;

/**
 * 背包物品列表
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月18日 下午3:26:33
 */
@HandlerEntity(mid = MID.PacketItemsReq_VALUE, msg = PacketItemsRequest.class)
public class PacketItemsHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketItemsHandler.class);

	@Override
	public void run() {
		Role role = RoleManager.getInstance().getRole(rid);
		if (role == null) {
			LOGGER.warn("角色{}未登陆", rid);
			return;
		}
		PacketItemsResponse.Builder builder = PacketItemsResponse.newBuilder();
		role.getItems().forEach((key, value) -> {
			builder.addItems(PacketManager.getInstance().buildPacketItem(value));
		});
		sendIdMsg(builder.build());

	}

}
