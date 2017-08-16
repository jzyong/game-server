package com.jzy.game.engine.server;

import java.util.concurrent.PriorityBlockingQueue;

import org.apache.mina.core.session.IoSession;

import com.jzy.game.engine.mina.config.MinaClientConfig;
import com.jzy.game.engine.thread.ThreadPoolExecutorConfig;

/**
 * 客户端服务
 *
 * @author JiangZhiYong
 * @date 2017-04-01 QQ:359135103
 */
public abstract class ClientService extends Service<MinaClientConfig> {

	private MinaClientConfig minaClientConfig;

	/** 连接会话 */
	private final PriorityBlockingQueue<IoSession> sessions = new PriorityBlockingQueue<>(128,
			(IoSession o1, IoSession o2) -> {
				int res = o1.getScheduledWriteMessages() - o2.getScheduledWriteMessages();
				if (res == 0) {
					res = (int) (o1.getWrittenBytes() - o2.getWrittenBytes());
				}
				return res;
			});

	/**
	 * 无线程池
	 * @param minaClientConfig
	 */
	public ClientService(MinaClientConfig minaClientConfig) {
		this(null, minaClientConfig);
	}

	public ClientService(ThreadPoolExecutorConfig threadPoolExecutorConfig, MinaClientConfig minaClientConfig) {
		super(threadPoolExecutorConfig);
		this.minaClientConfig = minaClientConfig;
	}

	/**
	 * 连接建立
	 */
	public void onIoSessionConnect(IoSession session) {
		sessions.add(session);
	}

	/**
	 * 连接关闭移除
	 */
	public void onIoSessionClosed(IoSession session) {
		sessions.remove(session);
	}

	public boolean isSessionEmpty() {
		return sessions.isEmpty();
	}

	/**
	 * 发送消息
	 * 
	 * @param obj
	 * @return
	 */
	public boolean sendMsg(Object obj) {
		IoSession session = getMostIdleIoSession();
		if (session != null) {
			session.write(obj);
			return true;
		}
		return false;
	}

	/**
	 * 获取连接列表中最空闲的有效的连接
	 * 
	 * @return
	 */
	public IoSession getMostIdleIoSession() {
		IoSession session = null;
		while (session == null && !sessions.isEmpty()) {
			session = sessions.peek();
			if (session != null && session.isConnected()) {
				break;
			} else {
				sessions.poll();
			}
		}
		return session;
	}

	public MinaClientConfig getMinaClientConfig() {
		return minaClientConfig;
	}
	
}
