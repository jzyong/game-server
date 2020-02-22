package com.jzy.game.engine.mina.handler;

import org.apache.mina.core.session.IoSession;

import com.jzy.game.engine.mina.config.MinaClientConfig;
import com.jzy.game.engine.mina.service.MinaClientService;
import com.jzy.game.engine.server.Service;
import com.jzy.game.engine.util.IntUtil;

/**
 * 默认内部客户端消息处理器
 *
 * @author JiangZhiYong
 * @date 2017-04-09 QQ:359135103
 * @version $Id: $Id
 */
public class DefaultClientProtocolHandler extends DefaultProtocolHandler {

	private final MinaClientService service;

	/**
	 * <p>Constructor for DefaultClientProtocolHandler.</p>
	 *
	 * @param service a {@link com.jzy.game.engine.mina.service.MinaClientService} object.
	 */
	public DefaultClientProtocolHandler(MinaClientService service) {
		super(4);
		this.service = service;
	}
	
	
	

	/**
	 * <p>Constructor for DefaultClientProtocolHandler.</p>
	 *
	 * @param messageHeaderLength a int.
	 * @param service a {@link com.jzy.game.engine.mina.service.MinaClientService} object.
	 */
	public DefaultClientProtocolHandler(int messageHeaderLength, MinaClientService service) {
		super(messageHeaderLength);
		this.service = service;
	}




	/** {@inheritDoc} */
	@Override
	public void sessionOpened(IoSession session) {
		super.sessionOpened(session);
		getService().onIoSessionConnect(session);
	}

	/** {@inheritDoc} */
	@Override
	protected void forward(IoSession session, int msgID, byte[] bytes) {
		log.warn("无法找到消息处理器：msgID{},bytes{}", msgID, IntUtil.BytesToStr(bytes));
	}

	/** {@inheritDoc} */
	@Override
	public MinaClientService getService() {
		return service;
	}

	/** {@inheritDoc} */
	@Override
	public void sessionClosed(IoSession session) {
		super.sessionClosed(session);
		getService().onIoSessionClosed(session);
	}

}
