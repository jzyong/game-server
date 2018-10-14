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
    void clear();
	
	/**
	 * 获取队列
	 * @return
	 */
    Queue<T> getQueue();
	
	/**
	 * 入队
	 * @param cmd
	 */
    void enqueue(T cmd);
	
	/**
	 * 出队
	 * @param cmd
	 */
    void dequeue(T cmd);
	
}
