package com.jzy.game.engine.thread.queue.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.thread.queue.IActionQueue;

/**
 * action抽象类
 * @author laofan
 *
 */
public abstract class Action implements Runnable {
	private static final Logger LOGGER=LoggerFactory.getLogger(Action.class);

	/**
	 * 队列
	 */
	private final IActionQueue<Action, DelayAction> queue;
	/**
	 * 创建时间
	 */
	protected long createTime;

	public Action(IActionQueue<Action, DelayAction> queue) {
		this.queue = queue;
		createTime = System.currentTimeMillis();
	}

	public IActionQueue<Action, DelayAction> getActionQueue() {
		return queue;
	}

	public void run() {
		if (queue != null) {
			long start = System.currentTimeMillis();
			try {
				execute();
				long end = System.currentTimeMillis();
				long interval = end - start;
				long leftTime = end - createTime;
				if (interval >= 1000) {
					LOGGER.warn("execute action : " + toString() + ", interval : " + interval + ", leftTime : " + leftTime + ", size : " + queue.getActionQueue().size());
				}
			} catch (Exception e) {
				LOGGER.error("run action execute exception. action : " + toString(), e);
			} finally {
				queue.dequeue(this);
			}
		}
	}

	/**
	 * 执行体
	 */
	public abstract void execute();
}
