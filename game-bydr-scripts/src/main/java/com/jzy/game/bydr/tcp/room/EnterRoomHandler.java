package com.jzy.game.bydr.tcp.room;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.message.Mid.MID;
import com.jzy.game.message.bydr.BydrRoomMessage.EnterRoomRequest;
import com.jzy.game.message.bydr.BydrRoomMessage.EnterRoomResponse;
import com.jzy.game.bydr.manager.RoleManager;
import com.jzy.game.bydr.manager.RoomManager;
import com.jzy.game.bydr.struct.role.Role;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;

/**
 * 进入房间
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月5日 下午5:35:36
 */
@HandlerEntity(mid = MID.EnterRoomReq_VALUE, msg = EnterRoomRequest.class)
public class EnterRoomHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(EnterRoomHandler.class);
//	private static AtomicInteger num = new AtomicInteger(0);

	@Override
	public void run() {
//		LOGGER.warn("{}", getMessage().toString());
		EnterRoomRequest req=getMsg();
		Role role = RoleManager.getInstance().getRole(rid);
		if(role==null) {
			LOGGER.warn("角色{}未登陆",rid);
			return;
		}
		RoomManager.getInstance().enterRoom(role, req.getType(), req.getRank());
//		EnterRoomResponse.Builder builder = EnterRoomResponse.newBuilder();
//		builder.setResult(num.getAndIncrement());
//		sendIdMsg(builder.build());

	}

}
