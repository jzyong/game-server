package com.jzy.game.bydr.script;

import java.util.function.Consumer;
import com.jzy.game.bydr.struct.role.Role;
import com.jzy.game.engine.script.IScript;

/**
 * 机器人脚本
 *
 * @author JiangZhiYong
 * @date 2017-04-27
 * QQ:359135103
 */
public interface IRobotScript extends IScript{

	/**
	 * 创建机器人
	 * @param roleConsumer
	 */
	default Role createRobot(Consumer<Role> roleConsumer){
		return null;
	}
	
	/**
	 * 检查机器人金币，并修正
	 * @param robot
	 */
	default void checkGold(Role robot){
		
	}
}
