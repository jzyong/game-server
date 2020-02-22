package com.jzy.game.engine.mina.service;

import java.util.concurrent.PriorityBlockingQueue;

import org.apache.mina.core.session.IoSession;

import com.jzy.game.engine.mina.config.MinaClientConfig;
import com.jzy.game.engine.server.ITcpClientService;
import com.jzy.game.engine.server.Service;
import com.jzy.game.engine.thread.ThreadPoolExecutorConfig;

/**
 * 内部客户端服务
 *
 * @author JiangZhiYong
 * @date 2017-04-01 QQ:359135103
 * @version $Id: $Id
 */
public abstract class MinaClientService extends Service<MinaClientConfig> implements ITcpClientService<MinaClientConfig>{

	private final MinaClientConfig minaClientConfig;

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
	 *
	 * @param minaClientConfig a {@link com.jzy.game.engine.mina.config.MinaClientConfig} object.
	 */
	public MinaClientService(MinaClientConfig minaClientConfig) {
		this(null, minaClientConfig);
	}

	/**
	 * <p>Constructor for MinaClientService.</p>
	 *
	 * @param threadPoolExecutorConfig a {@link com.jzy.game.engine.thread.ThreadPoolExecutorConfig} object.
	 * @param minaClientConfig a {@link com.jzy.game.engine.mina.config.MinaClientConfig} object.
	 */
	public MinaClientService(ThreadPoolExecutorConfig threadPoolExecutorConfig, MinaClientConfig minaClientConfig) {
		super(threadPoolExecutorConfig);
		this.minaClientConfig = minaClientConfig;
	}

	/**
	 * 连接建立
	 *
	 * @param session a {@link org.apache.mina.core.session.IoSession} object.
	 */
	public void onIoSessionConnect(IoSession session) {
		sessions.add(session);
	}

	/**
	 * 连接关闭移除
	 *
	 * @param session a {@link org.apache.mina.core.session.IoSession} object.
	 */
	public void onIoSessionClosed(IoSession session) {
		sessions.remove(session);
	}

	/**
	 * <p>isSessionEmpty.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isSessionEmpty() {
		return sessions.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 *
	 * 发送消息
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
	 * @return a {@link org.apache.mina.core.session.IoSession} object.
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

	/**
	 * <p>Getter for the field <code>minaClientConfig</code>.</p>
	 *
	 * @return a {@link com.jzy.game.engine.mina.config.MinaClientConfig} object.
	 */
	public MinaClientConfig getMinaClientConfig() {
		return minaClientConfig;
	}
	
}
