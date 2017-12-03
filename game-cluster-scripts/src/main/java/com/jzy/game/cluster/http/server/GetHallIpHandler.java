package com.jzy.game.cluster.http.server;

import java.util.Map;
import java.util.Optional;
import com.jzy.game.cluster.manager.ServerManager;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.HttpHandler;
import com.jzy.game.engine.server.ServerInfo;
import com.jzy.game.engine.server.ServerState;
import com.jzy.game.engine.server.ServerType;

/**
 * 获取大厅服务器
 *
 * <p>
 * http://127.0.0.1:8001/server/gate/ip
 * </p>
 * 
 * @author JiangZhiYong
 * @date 2017-03-31 QQ:359135103
 * 
 */
@HandlerEntity(path = "/server/hall/ip")
public class GetHallIpHandler extends HttpHandler {

	@Override
	public void run() {
		try {
			
			Map<Integer, ServerInfo> servers = ServerManager.getInstance().getServers(ServerType.HALL);
			if (servers == null||servers.size()<1) {
				getParameter().appendBody("无可用大厅服");
			} else {
				Optional<ServerInfo> findFirst = servers.values().stream()
						.filter(server -> server.getState() == ServerState.NORMAL.ordinal() && server.getSession() != null
								&& server.getSession().isConnected())
						.sorted((s1, s2) -> s1.getOnline() - s2.getOnline()).findFirst();
				if (findFirst.isPresent()) {
					getParameter().appendBody(findFirst.get().getIp() + ":" + findFirst.get().getHttpPort());
				}else {
					getParameter().appendBody("无可用大厅服");
				}
			}
		} finally {
			response();
		}
	}

}
