package com.jjy.game.gate.tcp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.gate.manager.ServerManager;
import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.ServerMessage;
import com.jjy.game.message.ServerMessage.ServerRegisterRequest;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.engine.server.ServerInfo;
import com.jzy.game.engine.server.ServerType;
import com.jzy.game.engine.thread.ThreadType;

/**
 * 游戏服循环注册更新
 *
 * @author JiangZhiYong
 * @date 2017-04-09 QQ:359135103
 */
@HandlerEntity(mid = MID.ServerRegisterReq_VALUE, msg = ServerRegisterRequest.class, thread = ThreadType.SYNC)
public class ServerRegisterHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerRegisterHandler.class);

	@Override
	public void run() {
		ServerRegisterRequest req = getMsg();
		ServerMessage.ServerInfo serverInfo = req.getServerInfo();
		ServerInfo gameInfo = ServerManager.getInstance().getGameServerInfo(ServerType.valueof(serverInfo.getType()),
				serverInfo.getId());
		if (gameInfo != null) {
			gameInfo.onIoSessionConnect(session);
			LOGGER.info("服务器：{} 连接注册到网关服 {} ip:{}", gameInfo.getName(), getSession().getId(),
					getSession().getRemoteAddress().toString());
		} else {
			LOGGER.warn("网关服务没有{}服:{}",ServerType.valueof(serverInfo.getType()).toString(), serverInfo.getId());
		}

	}

}
