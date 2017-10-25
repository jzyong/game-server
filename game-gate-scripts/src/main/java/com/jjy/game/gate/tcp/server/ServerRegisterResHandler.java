package com.jjy.game.gate.tcp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.ServerMessage.ServerRegisterResponse;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.engine.thread.ThreadType;

/**
 * 注册集群服返回
 *
 * @author JiangZhiYong
 * @date 2017-04-09 QQ:359135103
 */
@HandlerEntity(mid = MID.ServerRegisterRes_VALUE, msg = ServerRegisterResponse.class,thread=ThreadType.SYNC)
public class ServerRegisterResHandler extends TcpHandler {
	private static final Logger LOGGER=LoggerFactory.getLogger(ServerRegisterResHandler.class);

	@Override
	public void run() {
		
	
	}

}
