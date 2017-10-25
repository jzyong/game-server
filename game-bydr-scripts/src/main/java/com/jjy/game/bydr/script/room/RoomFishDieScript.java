package com.jjy.game.bydr.script.room;

import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.bydr.script.IRoomScript;
import com.jjy.game.bydr.struct.room.Room;

/**
 * 定时清理房间死亡的鱼
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月14日 上午10:08:12
 */
public class RoomFishDieScript implements IRoomScript {
	private static final Logger LOGGER = LoggerFactory.getLogger(RoomFishDieScript.class);

	@Override
	public void secondHandler(Room room, LocalTime localTime) {
		if (localTime.getSecond() % 3 != 0) {	//每隔三秒清理一次
			return;
		}
		if (room.getFishMap().size() > 1000) {
			room.getFishMap().clear();
		}
	}


}
