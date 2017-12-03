package com.jzy.game.hall.tcp.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.hall.server.HallServer;
import com.jzy.game.message.Mid.MID;
import com.jzy.game.message.ServerMessage.ServerInfo;
import com.jzy.game.message.ServerMessage.ServerListResponse;

/***
 * 返回服务器列表
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年6月29日 下午1:58:40
 */
@HandlerEntity(mid = MID.ServerListRes_VALUE, msg = ServerListResponse.class)
public class ServerListHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerListHandler.class);

	@Override
	public void run() {
		ServerListResponse res = getMsg();
		if (res == null) {
			return;
		}
		List<ServerInfo> list = res.getServerInfoList();
		if (list == null) {
			LOGGER.warn("没有可用网关服务器");
			return;
		}
		// 更新服务器信息
		Set<Integer> serverIds = new HashSet<>();
		list.forEach(info -> {
			HallServer.getInstance().getHall2GateClient().updateHallServerInfo(info);
			serverIds.add(info.getId());
		});
		Map<Integer, com.jzy.game.engine.server.ServerInfo> hallServers = HallServer.getInstance().getHall2GateClient()
				.getServers();

		if (hallServers.size() != list.size()) {
			List<Integer> ids = new ArrayList<>(hallServers.keySet());
			ids.removeAll(serverIds);
			ids.forEach(serverId -> {
				HallServer.getInstance().getHall2GateClient().removeTcpClient(serverId);
			});
		}
	}

}
