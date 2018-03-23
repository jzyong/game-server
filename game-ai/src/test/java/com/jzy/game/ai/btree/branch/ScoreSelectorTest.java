package com.jzy.game.ai.btree.branch;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.jzy.game.ai.btree.BehaviorTree;
import com.jzy.game.ai.btree.Task;
import com.jzy.game.ai.btree.decorator.AlwaysFail;
import com.jzy.game.ai.btree.dog.BarkTask;
import com.jzy.game.ai.btree.dog.CareTask;
import com.jzy.game.ai.btree.dog.Dog;
import com.jzy.game.ai.btree.dog.MarkTask;
import com.jzy.game.ai.btree.dog.RestTask;
import com.jzy.game.ai.btree.dog.WalkTask;
import com.jzy.game.engine.math.MathUtil;

/**
 * 分数选择器
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class ScoreSelectorTest {

	/**
	 * 测试分数选择
	 */
	@Test
	public void testScoreSelector() throws Exception{
		ScoreSelector<Dog> selector = new DogRandomSocreSelector();




		BarkTask bark1 = new BarkTask();
		bark1.times=10;
		bark1.setName("bark");
		selector.addChild(bark1);
		WalkTask walkTask = new WalkTask();
		walkTask.setName("walk");
		selector.addChild(walkTask);
		BehaviorTree<Dog> tree=new BehaviorTree<Dog>(selector, new Dog("Buddy"));
		int setp=0;
		while(true) {
			System.out.println("步骤："+(++setp));
			tree.step();
			Thread.sleep(1000);
		}
	}
	
	public class DogRandomSocreSelector extends ScoreSelector<Dog>{

		@Override
		protected void calculateScore() {
			getScoreChildren().forEach(task->{
				if(task instanceof BarkTask) {
					BarkTask barkTask=(BarkTask)task;
					if(barkTask.times>5) {
						barkTask.setScore(100);
					}else {
						barkTask.setScore(10);
					}
				}else {
					task.setScore(MathUtil.random(99,101));
				}
			});
			getScoreChildren().sort((t1,t2)->t2.getScore()-t1.getScore());
		}
		
	}
}
