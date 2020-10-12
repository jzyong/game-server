package com.jzy.game.ai.btree.branch;

import java.util.ArrayList;
import java.util.List;

import com.jzy.game.ai.btree.Task;

/**
 * 分数选择器，分数越高，优先执行,只会执行第一个子树
 * 
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 * @param <E>
 *            黑板对象
 */
public abstract class ScoreSelector<E> extends Selector<E> {
	/** 根据分数排序后的任务列表 */
	private List<Task<E>> scoreChildren = new ArrayList<>();

	@Override
	public void start() {
		super.start();
		calculateScore();
		if (scoreChildren.size() < 1) {
			scoreChildren.addAll(this.children);
		}
	}

	@Override
	public void run() {
		if (runningChild != null) {
			runningChild.run();
		} else {
			if (currentChildIndex < scoreChildren.size()) {
				runningChild = scoreChildren.get(currentChildIndex);
				runningChild.setControl(this);
				runningChild.start();
				if (!runningChild.checkGuard(this))
					runningChild.fail();
				else
					run();
			} else {
				// Should never happen; this case must be handled by subclasses in childXXX
				// methods
			}
		}
	}

	/**
	 * 计算子节点的分数 <br>
	 * 节点可根据名称获取
	 */
	protected abstract void calculateScore();

	@Override
	public void resetTask() {
		super.resetTask();
		this.scoreChildren.clear();
	}

	@Override
	public void reset() {
		super.reset();
		this.scoreChildren.clear();
	}

	public List<Task<E>> getScoreChildren() {
		return scoreChildren;
	}
	
}
