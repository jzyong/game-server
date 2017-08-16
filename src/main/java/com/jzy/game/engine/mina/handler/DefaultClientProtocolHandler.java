package com.jzy.game.engine.mina.handler;

import org.apache.mina.core.session.IoSession;

import com.jzy.game.engine.mina.config.MinaClientConfig;
import com.jzy.game.engine.server.Service;
import com.jzy.game.engine.util.IntUtil;

/**
 * 默认内部客户端消息处理器
 *
 * @author JiangZhiYong
 * @date 2017-04-09 QQ:359135103
 */
public class DefaultClientProtocolHandler extends DefaultProtocolHandler {

	private Service<MinaClientConfig> service;

	public DefaultClientProtocolHandler(Service<MinaClientConfig> service) {
		super(4);
		this.service = service;
	}
	
	
	

	public DefaultClientProtocolHandler(int messageHeaderLenght, Service<MinaClientConfig> service) {
		super(messageHeaderLenght);
		this.service = service;
	}




	@Override
	public void sessionOpened(IoSession session) {
		super.sessionOpened(session);
		getService().onIoSessionConnect(session);
	}

	@Override
	protected void forward(IoSession session, int msgID, byte[] bytes) {
		log.warn("无法找到消息处理器：msgID{},bytes{}", msgID, IntUtil.BytesToStr(bytes));
	}

	@Override
	public Service<MinaClientConfig> getService() {
		return this.service;
	}

	@Override
	public void sessionClosed(IoSession session) {
		super.sessionClosed(session);
		getService().onIoSessionClosed(session);
	}

}