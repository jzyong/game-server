package com.jzy.game.tool.client;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.ssl.SslFilter;

import com.jzy.game.engine.mina.handler.ClientProtocolHandler;
import com.jzy.game.engine.mina.service.SingleMinaTcpClientService;
import com.jzy.game.engine.server.BaseServerConfig;
import com.jzy.game.engine.server.Service;

/**
 * 压测消息处理handler
 * 
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年7月10日 下午5:48:39
 */
public class PressureClientHandler extends ClientProtocolHandler{
	
	private SingleMinaTcpClientService service;

	public PressureClientHandler() {
		super(8);
	}

	@Override
	public void sessionOpened(IoSession session) {
		super.sessionOpened(session);
		service.onIoSessionConnect(session);
	}

	public void setService(SingleMinaTcpClientService service) {
		this.service = service;
	}

	@Override
	public Service<? extends BaseServerConfig> getService() {
		return this.service;
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
//		IoFilter filter = session.getFilterChain().get(SslFilter.class);
//		if(filter!=null){
//			SslFilter sslFilter=(SslFilter)filter;
//			sslFilter.setUseClientMode(true);
//			sslFilter.startSsl(session);
//			sslFilter.initiateHandshake(session);
//		}
	}

	@Override
	public void messageReceived(IoSession session, Object obj) throws Exception {
		super.messageReceived(session, obj);
	}
	
	
}