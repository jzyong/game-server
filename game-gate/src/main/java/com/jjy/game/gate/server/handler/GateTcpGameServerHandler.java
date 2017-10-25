package com.jjy.game.gate.server.handler;

import java.util.Arrays;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.gate.manager.UserSessionManager;
import com.jjy.game.gate.script.IGateServerScript;
import com.jjy.game.gate.server.GateTcpGameServer;
import com.jjy.game.gate.struct.UserSession;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.engine.mina.handler.DefaultProtocolHandler;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.engine.server.BaseServerConfig;
import com.jzy.game.engine.server.Service;
import com.jzy.game.engine.util.MsgUtil;

/**
 * 游戏服、大厅服等内部共用的服务器
 * @author JiangZhiYong
 * @QQ 359135103 2017年6月30日 下午2:05:46
 */
public class GateTcpGameServerHandler extends DefaultProtocolHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(GateTcpGameServer.class);

	private Service<MinaServerConfig> service;

	public GateTcpGameServerHandler(Service<MinaServerConfig> service) {
		super(12);
		this.service = service;
	}

	/**
	 * 奖消息转发给游戏客户端
	 */
	@Override
	protected void forward(IoSession session, int msgID, byte[] bytes) {
		long rid = MsgUtil.getMessageRID(bytes, 0);
		if (rid > 0) {
			UserSession userSession = UserSessionManager.getInstance().getUserSessionbyRoleId(rid);
			if (userSession != null) {
//				LOGGER.debug("{} bytes:{}", bytes.length, bytes);
				//udp转发
				if(userSession.getClientUdpSession()!=null){
					if(ScriptManager.getInstance().getBaseScriptEntry().predicateScripts(IGateServerScript.class, (IGateServerScript script)->script.isUdpMsg(userSession.getServerType(),msgID))){
						userSession.sendToClientUdp(Arrays.copyOfRange(bytes, 8, bytes.length));
						return;
					}
				}
				
				//tcp返回
				userSession.sendToClient(Arrays.copyOfRange(bytes, 8, bytes.length)); // 前8字节为角色ID
				return;
			}
		}

		LOGGER.warn("消息[{}]未找到角色{}", msgID, rid);
	}

	@Override
	public Service<? extends BaseServerConfig> getService() {
		return this.service;
	}

	@Override
	public void sessionOpened(IoSession session) {
		super.sessionOpened(session);
	}

	
}
