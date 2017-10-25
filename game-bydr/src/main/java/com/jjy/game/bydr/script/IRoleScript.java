package com.jjy.game.bydr.script;

import java.util.function.Consumer;

import com.jjy.game.bydr.struct.role.Role;
import com.jjy.game.model.constant.Reason;
import com.jzy.game.engine.script.IScript;

/**
 * 角色
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年8月4日 下午2:10:47
 */
public interface IRoleScript extends IScript{

	/**
	 * 登录
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年8月4日 下午2:12:33
	 * @param roleId
	 * @param reason
	 * @param roleConsumer
	 */
	default void login(long roleId,Reason reason,Consumer<Role> roleConsumer){
		
	}
	
	/**
	 * 退出
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年8月4日 下午2:12:40
	 * @param role
	 * @param reason
	 */
	default void quit(Role role,Reason reason){
		
	}
	
	/**
	 * 修改金币
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年9月25日 下午5:20:33
	 * @param role
	 * @param gold
	 * @param reason
	 */
	default void changeGold(Role role,int gold,Reason reason) {
		
	}
	
	/**
	 * 奖金币同步大大厅
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年9月26日 上午10:40:51
	 * @param role
	 * @param reason
	 */
	default void syncGold(Role role,Reason reason) {
		
	}
}
