package com.jzy.game.engine.thread.queue.action;

import com.jzy.game.engine.thread.queue.IActionQueue;

/**
 * 无限循环
 * @author HW-fanjaiwei
 *
 */
public abstract class InfiniteLoopAction extends DelayAction {


	public InfiniteLoopAction(IActionQueue<Action, DelayAction> queue, int delay) {
		super(queue, delay);
	}

	@Override
	public void execute() {
		try{			
			loopExecute();
		}catch (Exception e) {
			throw e;
		}finally {			
			this.execTime = System.currentTimeMillis() + this.delay;
			getActionQueue().enDelayQueue(this);
		}
	}

	/**
	 * 循环执行接口
	 */
	public abstract void loopExecute();
	
}
