package com.jzy.game.engine.handler;

import org.apache.mina.core.session.IoSession;

/**
 * 抽象handler
 *
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public abstract class AbsHandler implements IHandler {

	protected IoSession session;
	protected long createTime;

	@Override
	public IoSession getSession() {
		return this.session;
	}

	@Override
	public void setSession(IoSession session) {
		this.session = session;
	}

	@Override
	public void setCreateTime(long time) {
		this.createTime=time;
	}

	@Override
	public long getCreateTime() {
		return this.createTime;
	}

	@Override
	public Object getParameter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParameter(Object parameter) {
		// TODO Auto-generated method stub

	}
}
