package com.jzy.game.hall.script;

import java.util.function.Consumer;
import com.jzy.game.engine.script.IScript;
import com.jzy.game.model.constant.Reason;
import com.jzy.game.model.struct.Role;


/**
 * 玩家脚本
 *
 * @author JiangZhiYong
 * @date 2017-03-30 QQ:359135103
 */
public interface IRoleScript extends IScript {

	/**
	 * 登录游戏
	 */
	public default void login(Role role,Reason reason) {

	}

	/**
	 * 创建角色
	 * 
	 * @param userId
	 * @return
	 */
	public default Role createRole(long userId, Consumer<Role> roleConsumer) {

		return null;
	}
	
	/**
	 * 角色退出游戏
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年9月18日 下午6:01:08
	 * @param role
	 */
	default void quit(Role role,Reason reason) {
		
	}
	
}
