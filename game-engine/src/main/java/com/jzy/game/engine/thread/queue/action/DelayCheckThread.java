package com.jzy.game.engine.thread.queue.action;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 1:定时检测队列 线程
 * 2:队列中时间到的action从新加入执行队列
 * 
 * @author laofan
 *
 */
public class DelayCheckThread extends Thread {
	private static final Logger LOGGER=LoggerFactory.getLogger(DelayCheckThread.class);

	/**
	 * 检测相隔时间 （单位：毫秒）
	 */
	private static final int FRAME_PER_SECOND = 120;
	
	/**
	 * 线程锁
	 */
	private Object lock = new Object(); // 线程锁

	private List<DelayAction> delayQqueue;	//延迟队列

	private List<DelayAction> execQueue;	//执行队列,临时存储

	private volatile boolean isRunning;

	public DelayCheckThread(String prefix) {
		super(prefix + "DelayCheckThread");
		delayQqueue = new ArrayList<DelayAction>();
		execQueue = new ArrayList<DelayAction>();
		isRunning = true;
		setPriority(Thread.MAX_PRIORITY); // 给予高优先级
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void stopping() {
		if (isRunning) {
			isRunning = false;
		}
	}

	@Override
	public void run() {
		long balance = 0;
		while (isRunning) {
			try {
				int execute = 0;
				// 读取待执行的队列,如果没有可以执行的动作则等待
				poll();
				if (execQueue.size() == 0) {
					continue;
				}

				long start = System.currentTimeMillis();
				execute = execActions();
				execQueue.clear();
				long end = System.currentTimeMillis();
				long interval = end - start;
				balance += FRAME_PER_SECOND - interval;
				if (interval > FRAME_PER_SECOND) {
					LOGGER.warn("DelayCheckThread is spent too much time: " + interval + "ms, execute = " + execute);
				}
				if (balance > 0) {
					Thread.sleep((int) balance);
					balance = 0;
				} else {
					if (balance < -1000) {
						balance += 1000;
					}
				}
			} catch (Exception e) {
				LOGGER.error("DelayCheckThread error. ", e);
			}
		}
	}

	
	/**
	 * 监测队列是否能执行，返回执行成功的Action数量
	 **/
	private int execActions() {
		int executeCount = 0;
		for (DelayAction delayAction : execQueue) {
			try {
				long begin = System.currentTimeMillis();
				if (delayAction == null) {
					LOGGER.error("error");
				}
				if (!delayAction.canExec(begin)) {	
					addDelayAction(delayAction);	//返回给等待队列
				}else{
					delayAction.getActionQueue().enqueue(delayAction);	//添加到执行队列
				}
				executeCount++;
				long end = System.currentTimeMillis();
				if (end - begin > FRAME_PER_SECOND) {
					LOGGER.warn(delayAction.toString() + " spent too much time. time :" + (end - begin));
				}
			} catch (Exception e) {
				LOGGER.error("执行action异常" + delayAction.toString(), e);
			}
		}
		return executeCount;
	}

	
	/**
	 * 添加Action到等待队列
	 * 
	 * @param delayAction
	 */
	public void addDelayAction(DelayAction delayAction) {
		synchronized (lock) {
			delayQqueue.add(delayAction);
			lock.notifyAll();
		}
	}

	/**
	 * 以阻塞的方式读取队列,如果队列为空则阻塞
	 * 
	 * @throws InterruptedException
	 **/
	private void poll() throws InterruptedException {
		synchronized (lock) {
			if (delayQqueue.size() == 0) {				
				/**
				 * 千万注意：
					当在对象上调用wait()方法时，执行该代码的线程立即放弃它在对象上的锁。然而调用notify()时，
					并不意味着这时线程会放弃其锁。如果线程荣然在完成同步代码，则线程在移出之前不会放弃锁。
					因此，只要调用notify()并不意味着这时该锁变得可用。
				 */
				lock.wait();
			} else {
				execQueue.addAll(delayQqueue);
				delayQqueue.clear();
				lock.notifyAll();
			}
		}
	}
}