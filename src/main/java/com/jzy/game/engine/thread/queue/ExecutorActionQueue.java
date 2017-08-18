package com.jzy.game.engine.thread.queue;

import java.util.Queue;

import com.jzy.game.engine.thread.queue.action.Action;
import com.jzy.game.engine.thread.queue.action.DelayAction;
import com.jzy.game.engine.thread.queue.executor.IDelayExecutor;




/**
 * 
 * 可执行的ACTION队列
 * @author laofan
 *
 */
public class ExecutorActionQueue extends ExecutorHandlerQueue<Action> implements IActionQueue<Action,DelayAction> {

	
	public ExecutorActionQueue(IDelayExecutor executor, String queueName) {
		super(executor, queueName);
		
	}


	public void enDelayQueue(DelayAction delayAction) {
		((IDelayExecutor)executor).executeDelayAction(delayAction);
	}


	@Override
	public Queue<Action> getActionQueue() {
		return getQueue();
	}


}
