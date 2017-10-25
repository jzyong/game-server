package com.jjy.game.bydr.tcp.fight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.bydr.manager.RoomManager;
import com.jjy.game.bydr.struct.role.Role;
import com.jjy.game.bydr.struct.room.Room;
import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.bydr.BydrFightMessage.FireRequest;
import com.jjy.game.message.bydr.BydrFightMessage.FireResponse;
import com.jjy.game.model.constant.Reason;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.engine.thread.ThreadType;
import com.jzy.game.engine.util.TimeUtil;

/**
 * 开炮
 *
 * @author JiangZhiYong
 * @date 2017-04-21 QQ:359135103
 */
@HandlerEntity(mid = MID.FireReq_VALUE, msg = FireRequest.class, thread = ThreadType.ROOM)
public class FireHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(FireHandler.class);

	@Override
	public void run() {
		FireRequest req = getMsg();
		// LOGGER.info(req.toString());
		Role role = getPerson();

		Room room = RoomManager.getInstance().getRoom(role.getRoomId());
//		if (room == null) {
//			LOGGER.warn("{}所在房间{}未找到", role.getNick(), role.getRoomId());
//			sendServerMsg(ServerMsgId.login_state_lost);
//			return;
//		}
//		ConfigRoom cRoom = ConfigManager.getInstance().getConfigRoom(room.getType());
//
//		if (req.getGold() < cRoom.getMinGold() || req.getGold() > cRoom.getMaxGold()
//				|| req.getGold() % cRoom.getMinGold() != 0) {
//			//sendServerMsg("请求金币非法");
//			LOGGER.warn("房间{} {}_{}请求金币{}非法", room.getType(), role.getNick(), getRemoteAddr(), req.getGold());
//			return;
//		}

		if (role.getGold() < req.getGold()) {
//			sendServerMsg(ServerMsgId.not_enough_gold);
			return;
		}

		role.getFireGolds().add(req.getGold());
		role.changeGold(-req.getGold(), Reason.RoleFire);
		role.addBetGold(req.getGold());
		role.setFireTime(TimeUtil.currentTimeMillis());
		room.addAllFireCount();
		role.addFireCount();


		FireResponse.Builder builder = FireResponse.newBuilder();
		builder.setRid(role.getId());
		builder.setGold(req.getGold());
		builder.setAngleX(req.getAngleX());
		builder.setAngleY(req.getAngleY());
		builder.setTargetFishId(req.getTargetFishId());
		FireResponse response = builder.build();
		room.getRoles().values().forEach(roomRole -> {
			roomRole.sendMsg(response);
		});

	}

}
