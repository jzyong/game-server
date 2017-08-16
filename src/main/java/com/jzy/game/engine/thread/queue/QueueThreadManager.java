package com.jzy.game.engine.thread.queue;

import com.jzy.game.engine.thread.ServerThread;

/**
 * 队列线程处理管理
 * <p>
 * 两类线程模型:<br>
 * 1.为逻辑或接受到的消息预先分配一个线程，所有逻辑放在线程队列中依次执行；{@link ServerThread}，此方式可能会创建较多的线程<br>
 * 2.为逻辑或消息分配一个队列，再由队列分配线程，依次执行
 * {@link QueueThreadManager}，此方式有几个队列就相当于有几个并发任务，防止任务阻塞
 * 
 * </p>
 * 
 * 可选择模型
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年8月15日 上午10:16:28
 */
public class QueueThreadManager {
	private static volatile QueueThreadManager queueThreadManager;

	public static final QueueThreadManager getInstance() {
		if (queueThreadManager == null) {
			synchronized (QueueThreadManager.class) {
				if (queueThreadManager == null) {
					queueThreadManager = new QueueThreadManager();
				}
			}
		}
		return queueThreadManager;
	}

	private QueueThreadManager() {
		initQueue();
	}

	private void initQueue(){
		
	}
	
}
