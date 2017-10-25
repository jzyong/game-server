package com.jjy.game.cluster.tcp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.cluster.manager.ServerManager;
import com.jjy.game.cluster.server.ClusterTcpServer;
import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.ServerMessage.ServerInfo;
import com.jjy.game.message.ServerMessage.ServerRegisterRequest;
import com.jjy.game.message.ServerMessage.ServerRegisterResponse;
import com.jjy.game.model.timer.GameServerCheckTimer;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.engine.server.ServerType;

/**
 * 注册服务器信息
 *
 * @author JiangZhiYong
 * @date 2017-04-05 QQ:359135103
 */
@HandlerEntity(mid = MID.ServerRegisterReq_VALUE, msg = ServerRegisterRequest.class)
public class ServerRegisterHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerRegisterHandler.class);

	@Override
	public void run() {
		ServerRegisterRequest req = getMsg();
		ServerInfo serverInfo = req.getServerInfo();
		LOGGER.info("服务器{}_{}注册", ServerType.valueof(serverInfo.getType()).toString(), serverInfo.getId());
		
		
		com.jzy.game.engine.server.ServerInfo info = ServerManager.getInstance().registerServer(serverInfo,getSession());
		ServerRegisterResponse.Builder builder = ServerRegisterResponse.newBuilder();
		ServerInfo.Builder infoBuilder=ServerInfo.newBuilder();
		infoBuilder.mergeFrom(serverInfo);
		//反向更新
		infoBuilder.setState(info.getState());
		
		builder.setServerInfo(infoBuilder);
		getSession().write(builder.build());
	}

}
