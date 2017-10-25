package com.jjy.game.gate.tcp.role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.gate.manager.UserSessionManager;
import com.jjy.game.gate.struct.UserSession;
import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.system.SystemMessage.UdpConnectRequest;
import com.jjy.game.message.system.SystemMessage.UdpConnectResponse;
import com.jjy.game.model.constant.Config;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.engine.util.MsgUtil;

/**
 * 请求进行udp连接
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月1日 下午3:55:27
 */
@HandlerEntity(mid = MID.UdpConnectReq_VALUE, msg = UdpConnectRequest.class)
public class UdpConnectHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(UdpConnectHandler.class);

	@Override
	public void run() {
		UdpConnectRequest req = getMsg();
//		LOGGER.info("udp连接：{}", req.toString());
		UserSession userSession = UserSessionManager.getInstance().getUserSessionbyRoleId(req.getRid());
		UserSession userSession2 = UserSessionManager.getInstance().getUserSessionBySessionId(req.getSessionId());
		UdpConnectResponse.Builder builder = UdpConnectResponse.newBuilder();
		builder.setResult(0);
		if (userSession == null) {
			builder.setResult(1);
		} else if (!userSession.equals(userSession2)) {
			builder.setResult(2);
		}
		// 地址不正确
		if (!MsgUtil.getIp(session).equals(MsgUtil.getIp(userSession.getClientSession()))) {
			builder.setResult(3);
		}
		userSession.setClientUdpSession(getSession());
		session.setAttribute(Config.USER_SESSION, userSession);

		session.write(builder.build());
	}

}
