package com.jzy.game.cluster.http.server;

import java.util.ArrayList;
import java.util.List;
import com.jzy.game.cluster.manager.ServerManager;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.HttpHandler;
import com.jzy.game.engine.server.ServerInfo;

/**
 * 服务器列表
 * <p>
 * http://127.0.0.1:8001/server/list
 * </p>
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月20日 上午11:04:06
 */
@HandlerEntity(path = "/server/list")
public class ServerListHandler extends HttpHandler {

	@Override
	public void run() {
		List<ServerInfo> servers = new ArrayList<>();
		ServerManager.getInstance().getServers().values().forEach(l -> l.forEach((id, serverInfo) -> {
			servers.add(serverInfo);
		}));
		sendMsg(servers);
	}

}
