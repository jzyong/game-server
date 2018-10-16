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

package com.jzy.game.ai.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 非阻塞信号量仓库
 * 
 * @author davebaol
 */
public class NonBlockingSemaphoreRepository {

	private static final Map<String, NonBlockingSemaphore> REPO = new HashMap<>();

	private static NonBlockingSemaphore.Factory FACTORY = new SimpleNonBlockingSemaphore.Factory();

	public static void setFactory(NonBlockingSemaphore.Factory factory) {
		FACTORY = factory;
	}

	/**
	 * 添加任务信号控制
	 * @param name 信号量名称
	 * @param maxResources 最大信号量数
	 * @return
	 */
	public static NonBlockingSemaphore addSemaphore(String name, int maxResources) {
		NonBlockingSemaphore sem = FACTORY.createSemaphore(name, maxResources);
		REPO.put(name, sem);
		return sem;
	}

	public static NonBlockingSemaphore getSemaphore(String name) {
		return REPO.get(name);
	}

	public static NonBlockingSemaphore removeSemaphore(String name) {
		return REPO.remove(name);
	}

	public static void clear() {
		REPO.clear();
	}

}
