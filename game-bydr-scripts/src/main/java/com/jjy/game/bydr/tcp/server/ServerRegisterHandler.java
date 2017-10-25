package com.jjy.game.bydr.tcp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.ServerMessage.ServerRegisterResponse;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.engine.server.ServerState;
import com.jzy.game.engine.thread.ThreadType;

/**
 * 服务器注册消息返回
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年6月29日 上午11:15:57
 */
@HandlerEntity(mid = MID.ServerRegisterRes_VALUE, msg = ServerRegisterResponse.class, thread = ThreadType.SYNC)
public class ServerRegisterHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerRegisterHandler.class);

	@Override
	public void run() {
		ServerRegisterResponse res = getMsg();
		if (res != null && res.getServerInfo() != null) {
			if (ServerState.MAINTAIN.getState() == res.getServerInfo().getState()) {
				LOGGER.warn("服务器状态变为维护");
			}
			// TODO 更新服务器状态信息，处理维护状态下的逻辑
		}
		// LOGGER.debug("更新服务器信息返回");
	}

}
