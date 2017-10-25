package com.jjy.game.gate.timer.server;

import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.gate.server.GateServer;
import com.jjy.game.message.ServerMessage.ServerListRequest;
import com.jjy.game.message.ServerMessage.ServerRegisterRequest;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.engine.script.ITimerEventScript;
import com.jzy.game.engine.server.ServerType;

/**
 * 更新服务器信息脚本
 *
 * @author JiangZhiYong
 * @date 2017-04-09 QQ:359135103
 */
public class UpdateServerInfoScript implements ITimerEventScript {
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateServerInfoScript.class);

	@Override
	public void secondHandler(LocalTime localTime) {
		if (localTime.getSecond() % 5 == 0) { // 每5秒更新一次
			// 向服务器集群更新服务器信息
			MinaServerConfig minaServerConfig = GateServer.getInstance().getGateTcpUserServer().getMinaServerConfig();
			ServerRegisterRequest request = GateServer.getInstance().buildServerRegisterRequest(minaServerConfig);
			GateServer.getInstance().getClusterClient().sendMsg(request);
			// LOGGER.debug("更新服务器信息");
			// 重连服务器监测
			GateServer.getInstance().getClusterClient().getTcpClient().checkStatus();

			// 请求游戏服务器列表
			ServerListRequest.Builder builder = ServerListRequest.newBuilder();
			builder.setServerType(ServerType.GAME.getType());
			GateServer.getInstance().getClusterClient().sendMsg(builder.build());
		}
	}

}
