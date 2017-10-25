package com.jjy.game.tool.tcp.bydr.room;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.bydr.BydrRoomMessage;
import com.jjy.game.message.bydr.BydrRoomMessage.EnterRoomResponse;
import com.jjy.game.tool.client.Player;
import com.jjy.game.tool.client.PressureServiceThread;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;

/**
 * 进入房间返回
 *
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月11日 上午11:12:39
 */
@HandlerEntity(mid = MID.EnterRoomRes_VALUE, msg = EnterRoomResponse.class)
public class EnterRoomResHandlaer extends TcpHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(EnterRoomResHandlaer.class);

	public void run() {
		EnterRoomResponse res = getMsg();
		long sendTime = (Long) session.getAttribute(PressureServiceThread.SEND_TIME, Long.MAX_VALUE);
		LOGGER.warn("{}进入房间：{} 耗时：{}ms", session.getClass().getSimpleName(), res.getResult(),
				(System.currentTimeMillis() - sendTime));
		Player player = (Player) session.getAttribute(Player.PLAYER);
		if (res.hasRoomInfo()) {
			BydrRoomMessage.RoomInfo roomInfo = res.getRoomInfo();
			player.showLog(String.format("%s 进入房间 %d-%d-%d", player.getUserName(), roomInfo.getId(), roomInfo.getType(),
					roomInfo.getRank()));
		} else {
			player.showLog(String.format("%s 进入房间失败", player.getUserName()));
		}
		// TODO 切服消息
		// ChangeRoleServerRequest.Builder builder=ChangeRoleServerRequest.newBuilder();
		// builder.setRoleId((long)session.getAttribute("roleId"));
		// builder.setServerType(ServerType.GAME_BYDR.getType());
		// session.write(builder.build());
	}

}
