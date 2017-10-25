package com.jjy.game.bydr.script;

import com.jjy.game.bydr.struct.room.Room;
import com.jzy.game.engine.script.IScript;

/**
 * 鱼脚本
 *
 * @author JiangZhiYong
 * @date 2017-04-24 QQ:359135103
 */
public interface IFishScript extends IScript {

	/**
	 * 刷新鱼
	 * 
	 * @param room
	 */
	default void fishRefresh(Room room) {

	}

	/**
	 * 鱼死亡
	 * 
	 * @param room
	 */
	default void fishDie(Room room) {

	}


}
