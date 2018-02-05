/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.jzy.game.ai.btree.dog;

import com.jzy.game.ai.btree.BehaviorTree;
import com.jzy.game.engine.math.MathUtil;

/**狗角色对象
 *  @author implicit-invocation
 * @author davebaol */
public class Dog {

	public String name;
	public String brainLog;	//思考日志
	private BehaviorTree<Dog> behaviorTree;	//行为树

	public Dog (String name) {
		this(name, null);
	}

	public Dog (String name, BehaviorTree<Dog> btree) {
		this.name = name;
		this.brainLog = name + " brain";
		this.behaviorTree = btree;
		if (btree != null) btree.setObject(this);
	}

	public BehaviorTree<Dog> getBehaviorTree () {
		return behaviorTree;
	}

	public void setBehaviorTree (BehaviorTree<Dog> behaviorTree) {
		this.behaviorTree = behaviorTree;
	}

	/**
	 * 犬叫
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年11月24日 下午4:37:33
	 */
	public void bark () {
		if (MathUtil.randomBoolean())
			log("Arf arf");
		else
			log("Woof");
	}

	/**
	 * 开始走路
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年11月24日 下午5:01:08
	 */
	public void startWalking () {
		log("Let's find a nice tree");
	}

	/**
	 * 走路
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年11月24日 下午5:01:45
	 */
	public void randomlyWalk () {
		log("SNIFF SNIFF - Dog walks randomly around!");
	}

	/**
	 * 停止走路
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年11月24日 下午5:11:08
	 */
	public void stopWalking () {
		log("This tree smells good :)");
	}

	/**
	 * 撒尿标记
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年11月24日 下午4:45:04
	 * @param i
	 * @return
	 */
	public Boolean markATree (int i) {
		if (i == 0) {
			log("Swoosh....");
			return null;
		}
		if (MathUtil.randomBoolean()) {
			log("MUMBLE MUMBLE - Still leaking out");
			return Boolean.FALSE;
		}
		log("I'm ok now :)");
		return Boolean.TRUE;
	}

//	private boolean urgent = false;
//
//	public boolean isUrgent () {
//		return urgent;
//	}
//
//	public void setUrgent (boolean urgent) {
//		this.urgent = urgent;
//	}

	public void log (String msg) {
		System.out.println(String.format("%s:%s", name,msg));
	}

	public void brainLog (String msg) {
		System.err.println(String.format("%s:%s", brainLog,msg));
	}

}
