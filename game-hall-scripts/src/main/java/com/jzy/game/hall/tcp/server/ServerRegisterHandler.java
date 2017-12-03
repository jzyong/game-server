package com.jzy.game.hall.tcp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.engine.thread.ThreadType;
import com.jzy.game.message.Mid.MID;
import com.jzy.game.message.ServerMessage.ServerRegisterResponse;

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
		// TODO Auto-generated method stub
		// LOGGER.debug("更新服务器信息返回");
	}

}
