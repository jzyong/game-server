package com.jzy.game.engine.handler;

import org.apache.mina.core.session.IoSession;

/**
 * 处理器接口
 *
 * @author jiangzhiyong
 * @version $Id: $Id
 */
public interface IHandler extends Runnable {


	/**
	 * 会话
	 *
	 * @return a {@link org.apache.mina.core.session.IoSession} object.
	 */
	IoSession getSession();

	/**
	 * 会话
	 *
	 * @param session a {@link org.apache.mina.core.session.IoSession} object.
	 */
	void setSession(IoSession session);

	/**
	 * 请求消息
	 *
	 * @return a {@link java.lang.Object} object.
	 */
	Object getMessage();

	/**
	 * 消息
	 *
	 * @param message a {@link java.lang.Object} object.
	 */
	void setMessage(Object message);

	/**
	 * 创建时间
	 *
	 * @param time a long.
	 */
	void setCreateTime(long time);
	
	/**
	 * 创建时间
	 *
	 * @return a long.
	 */
	long getCreateTime();
	
	/**
	 * http 参数
	 *
	 * @return a {@link java.lang.Object} object.
	 */
	Object getParameter();

    /**
     * http 参数
     *
     * @param parameter a {@link java.lang.Object} object.
     */
    void setParameter(Object parameter);
}
