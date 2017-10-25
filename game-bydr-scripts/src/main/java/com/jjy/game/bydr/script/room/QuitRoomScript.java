package com.jjy.game.bydr.script.room;

import java.util.Iterator;

import com.jjy.game.bydr.script.IRoomScript;
import com.jjy.game.bydr.struct.role.Role;
import com.jjy.game.bydr.struct.room.Room;

/**
 * 退出房间
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月14日 上午10:10:12
 */
public class QuitRoomScript implements IRoomScript {

	@Override
	public void quitRoom(Role role, Room room) {
		Iterator<Role> iterator = room.getRoles().values().iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getId() == role.getId()) {
				iterator.remove();
			}
		}
		//TODO 
	}

}
