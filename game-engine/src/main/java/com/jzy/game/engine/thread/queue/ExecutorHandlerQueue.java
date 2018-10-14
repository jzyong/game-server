package com.jzy.game.engine.thread.queue;

import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.thread.queue.executor.IExecutor;





/**
 * 
 * 带执行器的队列
 * 
 * @author laofan
 *
 * @param <T>
 */
public class ExecutorHandlerQueue<T extends Runnable> implements IQueue<T> {
	private static final Logger LOGGER=LoggerFactory.getLogger(ExecutorHandlerQueue.class);

	/**
	 * 任务队列实体
	 */
	private final Queue<T> queue;
	/**
	 * 执行器
	 */
	protected final IExecutor<T> executor;
	/**
	 * 队列名称
	 */
	protected final String queueName;
	
	/**
	 * 清空队列标记
	 */
	protected volatile boolean clearQueueFlag;

	/**
	 * 
	 * @param executor :执行器
	 * @param queueName：队列前缀名称
	 */
	public ExecutorHandlerQueue(IExecutor<T> executor,String queueName) {
		this.executor = executor;
		queue = new LinkedList<T>();
		this.queueName = queueName;
	}
	
	
	public void enqueue(T cmd) {
		if(clearQueueFlag == true) {
            return;
        }
		boolean canExec = false;
		synchronized (queue) {
			queue.add(cmd);
			if (queue.size() == 1) {
				canExec = true;
			} else if (queue.size() > 10) {
				LOGGER.error(queueName + " queue size : " + queue.size());
			}
		}
		if (canExec) {
			executor.execute(cmd);
		}
	}


	public void dequeue(T cmdTask) {
		T nextCmdTask = null;
		synchronized (queue) {
			if (queue.size() == 0) {
				LOGGER.error(queueName+"queue.size() is 0.");
			}
			T temp = queue.remove();
			if (temp != cmdTask) {
				LOGGER.error(queueName + "queue error. temp " + temp + ", cmd : " + cmdTask);
			}
			if (queue.size() != 0) {
				nextCmdTask = queue.peek();
			}
		}
		
		if(clearQueueFlag){
			queue.clear();
			return;
		}
		if (nextCmdTask != null) {
			executor.execute(nextCmdTask);
		}
	}

	public Queue<T> getQueue() {
		return queue;
	}
	

	public void clear() {
		synchronized (queue) {			
			clearQueueFlag = true;
		}
	}

}
