package com.jzy.game.engine.thread.queue.executor;

import com.jzy.game.engine.thread.queue.action.Action;
import com.jzy.game.engine.thread.queue.action.DelayAction;

/**
 * 带延迟执行的线程执行器接口
 * @author laofan
 *
 */
public interface IDelayExecutor extends IExecutor<Action> {
	
	/**
	 * 执行延迟/定时 action
	 * @param delayAction
	 */
    void executeDelayAction(DelayAction delayAction);
}
