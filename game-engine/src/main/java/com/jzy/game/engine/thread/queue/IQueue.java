package com.jzy.game.engine.thread.queue;

import java.util.Queue;

/**
 * 队列接口
 * @author laofan
 *
 * @param <T>
 */
public interface IQueue<T> {
	/**
	 * 清空队列
	 */
	public void clear();
	
	/**
	 * 获取队列
	 * @return
	 */
	public Queue<T> getQueue();
	
	/**
	 * 入队
	 * @param cmd
	 */
	public void enqueue(T cmd);
	
	/**
	 * 出队
	 * @param cmd
	 */
	public void dequeue(T cmd);
	
}
