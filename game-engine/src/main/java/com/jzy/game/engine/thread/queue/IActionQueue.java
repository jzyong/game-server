package com.jzy.game.engine.thread.queue;

import java.util.Queue;

import com.jzy.game.engine.thread.queue.action.Action;
import com.jzy.game.engine.thread.queue.action.DelayAction;

/**
 * action 队列接口
 * 
 * @author laofan
 *
 */
public interface IActionQueue<T extends Action, E extends DelayAction> {

	/**
	 * 添加延时执行任务
	 * 
	 * @param delayAction
	 */
    void enDelayQueue(E delayAction);

	/**
	 * 清空队列
	 */
    void clear();

	/**
	 * 获取队列
	 * 
	 * @return
	 */
    Queue<T> getActionQueue();

	/**
	 * 入队
	 * 
	 * @param action
	 */
    void enqueue(T action);

	/**
	 * 出队
	 * 
	 * @param cmd
	 */
    void dequeue(T action);

}
