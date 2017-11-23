package com.jzy.game.ai.btree;

import com.jzy.game.engine.script.IScript;
import com.jzy.game.engine.struct.Person;

/**
 * 行为树脚本
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年11月23日 下午5:49:18
 */
public interface IBehaviorTreeScript extends IScript{
	
	/**
	 * 为对象添加行为树
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年11月23日 下午5:50:29
	 * @param person j
	 */
	default void addBehaviorTree(Person person) {
		
	}
}
