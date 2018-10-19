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

package com.jzy.game.ai.fsm;

import org.junit.Before;
import org.junit.Test;

import com.jzy.game.ai.msg.MessageManager;
import com.jzy.game.engine.util.TimeUtil;

/**
 * A simple test to demonstrate state machines combined with message handling.
 * 
 * @author davebaol
 */
public class StateMachineTest {

	Bob bob;
	Elsa elsa;
	long elapsedTime;

	@Before
	public void create() {

		elapsedTime = TimeUtil.currentTimeMillis();

		// Create Bob and his wife
		bob = new Bob();
		elsa = new Elsa(bob);
		bob.setElsa(elsa);

	}

	/**
	 * 测试状态
	 */
	@Test
	public void testStateMachine() throws Exception {

		while (true) {
			elapsedTime=TimeUtil.currentTimeMillis();
			bob.update(elapsedTime);
			elsa.update(elapsedTime);

			// Dispatch any delayed messages
			MessageManager.getInstance().update();

			Thread.sleep(1000);
		}

	}
}
