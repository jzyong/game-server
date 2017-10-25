package com.jjy.game.hall.script;

import java.util.function.Consumer;

import com.jjy.game.model.struct.User;
import com.jzy.game.engine.script.IScript;

/**
 * 用户接口
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月7日 下午4:16:57
 */
public interface IUserScript extends IScript {

	/**
	 * 创建用户
	 * 
	 * @param userConsumer
	 * @return
	 */
	default public User createUser(Consumer<User> userConsumer) {
		return null;
	}
}
