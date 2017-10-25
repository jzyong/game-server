package com.jjy.game.cluster.http.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.cluster.manager.ServerManager;
import com.jjy.game.model.constant.Config;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.HttpHandler;
import com.jzy.game.engine.server.ServerInfo;
import com.jzy.game.engine.server.ServerState;
import com.jzy.game.engine.server.ServerType;
import com.jzy.game.engine.util.MsgUtil;

/**
 * 设置服务器状态
 * <p>
 * http://127.0.0.1:8001/server/state
 * </p>
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月13日 下午3:16:56
 */
@HandlerEntity(path = "/server/state")
public class SetServerStateHandler extends HttpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(SetServerStateHandler.class);

	@Override
	public void run() {
		String auth = getString("auth");
		if (!Config.SERVER_AUTH.equals(auth)) {
			sendMsg("验证失败");
			return;
		}

		int serverType = getInt("serverType");
		int serverId = getInt("serverId");
		int serverState = getInt("serverState");
		ServerInfo server = ServerManager.getInstance().getServer(ServerType.valueof(serverType), serverId);
		if (server == null) {
			sendMsg(String.format("服务器 %d %d 未启动", serverType, serverId));
			return;
		}
		server.setState(serverState);
		LOGGER.info("{}设置服务器{}_{} 状态：{}", MsgUtil.getIp(getSession()), serverType, serverId, serverState);
		sendMsg("服务器状态设置未：" + ServerState.valueOf(serverState));
	}
	
	

}
