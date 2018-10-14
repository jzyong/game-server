package com.jzy.game.engine.thread.queue.executor;

/**
 * 执行器接口定义
 * @author laofan
 *
 * @param <T>
 */
public interface IExecutor<T extends Runnable> {
	
	/**
	 * 执行任务
	 * @param cmdTask
	 */
    void execute(T cmdTask);
	
	/**
	 * 停止所有线程
	 */
    void stop();
	
}
