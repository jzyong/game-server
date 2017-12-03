package com.jzy.game.bydr.script.room;

import java.util.Iterator;
import com.jzy.game.bydr.script.IRoomScript;
import com.jzy.game.bydr.struct.role.Role;
import com.jzy.game.bydr.struct.room.Room;

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
