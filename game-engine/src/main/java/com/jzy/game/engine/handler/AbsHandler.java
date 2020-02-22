package com.jzy.game.engine.handler;

import org.apache.mina.core.session.IoSession;

import io.netty.channel.Channel;

/**
 * 抽象handler
 *
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 * @version $Id: $Id
 */
public abstract class AbsHandler implements IHandler {

	protected IoSession session;
	protected long createTime;
	protected Channel channel;

	/** {@inheritDoc} */
	@Override
	public IoSession getSession() {
		return session;
	}

	/** {@inheritDoc} */
	@Override
	public void setSession(IoSession session) {
		this.session = session;
	}
	

	/**
	 * <p>Getter for the field <code>channel</code>.</p>
	 *
	 * @return a {@link io.netty.channel.Channel} object.
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * <p>Setter for the field <code>channel</code>.</p>
	 *
	 * @param channel a {@link io.netty.channel.Channel} object.
	 */
	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	/** {@inheritDoc} */
	@Override
	public void setCreateTime(long time) {
		createTime =time;
	}

	/** {@inheritDoc} */
	@Override
	public long getCreateTime() {
		return createTime;
	}

	/** {@inheritDoc} */
	@Override
	public Object getParameter() {
		// TODO Auto-generated method stub
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public void setParameter(Object parameter) {
		// TODO Auto-generated method stub

	}
}
