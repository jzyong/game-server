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

import com.jzy.game.ai.btree.LeafTask;
import com.jzy.game.ai.btree.Task;
import com.jzy.game.ai.btree.annotation.TaskAttribute;

/**
 * 吠叫
 * 
 * @author implicit-invocation
 * @author davebaol
 */
public class BarkTask extends LeafTask<Dog> {

	@TaskAttribute
	public int times = 1;	//次数

	private int t;	//已执行次数

	@Override
	public void start() {
		super.start();
		t = times;
	}

	@Override
	public Status execute() {
		Dog dog = getObject();
		for (int i = 0; i < t; i++)
			dog.bark();
		return Status.SUCCEEDED;
	}

//	@Override
//	protected Task<Dog> copyTo(Task<Dog> task) {
//		BarkTask bark = (BarkTask) task;
//		bark.times = times;
//
//		return task;
//	}

	@Override
	public void reset() {
		times = 1;
		t = 0;
		super.reset();
	}

}
