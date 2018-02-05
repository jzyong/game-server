package com.jzy.game.ai.btree;

import org.junit.Ignore;
import org.junit.Test;

import com.jzy.game.ai.btree.branch.Parallel;
import com.jzy.game.ai.btree.branch.Selector;
import com.jzy.game.ai.btree.branch.Sequence;
import com.jzy.game.ai.btree.decorator.AlwaysFail;
import com.jzy.game.ai.btree.dog.BarkTask;
import com.jzy.game.ai.btree.dog.CareTask;
import com.jzy.game.ai.btree.dog.Dog;
import com.jzy.game.ai.btree.dog.MarkTask;
import com.jzy.game.ai.btree.dog.RestTask;
import com.jzy.game.ai.btree.dog.WalkTask;
import com.jzy.game.engine.math.MathUtil;

/**
 * 行为树测试
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年11月22日 下午6:03:17
 */
@Ignore
public class BehaviorTreeTest {

	/**
	 * Selector
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年11月24日 下午5:20:06
	 * @throws Exception
	 */
	@Test
	public void testDogBehavior() throws Exception{
		Selector<Dog> selector = new Selector<Dog>();

		Parallel<Dog> parallel = new Parallel<Dog>();
		selector.addChild(parallel);

		CareTask care = new CareTask();
		care.urgentProb = 0.8f;
		parallel.addChild(care);
		parallel.addChild(new AlwaysFail<Dog>(new RestTask()));

		Sequence<Dog> sequence = new Sequence<Dog>();
		selector.addChild(sequence);

		BarkTask bark1 = new BarkTask();
		bark1.times = MathUtil.random(1, 5);
		sequence.addChild(bark1);
		sequence.addChild(new WalkTask());
		sequence.addChild(new BarkTask());
		sequence.addChild(new MarkTask());
		BehaviorTree<Dog> tree=new BehaviorTree<Dog>(selector, new Dog("Buddy"));
		int setp=0;
		while(true) {
			System.out.println("步骤："+(++setp));
			tree.step();
			Thread.sleep(1000);
		}
	
	}
}
