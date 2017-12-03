package com.jzy.game.gate.tcp.role;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.message.Mid.MID;
import com.jzy.game.message.hall.HallLoginMessage.LoginSubGameRequest;
import com.jzy.game.message.system.SystemMessage.SystemErroCode;
import com.jzy.game.message.system.SystemMessage.SystemErrorResponse;
import com.jzy.game.model.redis.key.HallKey;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.engine.redis.jedis.JedisManager;
import com.jzy.game.engine.server.ServerInfo;
import com.jzy.game.engine.server.ServerType;
import com.jzy.game.gate.manager.ServerManager;
import com.jzy.game.gate.manager.UserSessionManager;
import com.jzy.game.gate.struct.UserSession;

/**
 * 登录子游戏
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年6月30日 下午5:03:26
 */
@HandlerEntity(mid = MID.LoginSubGameReq_VALUE, msg = LoginSubGameRequest.class)
public class LoginSubGameHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginSubGameHandler.class);

	@Override
	public void run() {
		LoginSubGameRequest request = getMsg();
		long uid = request.getRid();
		ServerType serverType = ServerType.valueof(request.getGameType());
		LOGGER.info("[{}]登录游戏{}", uid, serverType);

		UserSession userSession = UserSessionManager.getInstance().getUserSessionByUserId(uid);
		userSession.setServerType(serverType);
		ServerInfo serverInfo = null;

		// 重连登录到之前保留的游戏服
		String key = HallKey.Role_Map_Info.getKey(userSession.getRoleId());
		String gameType = JedisManager.getJedisCluster().hget(key, "gameType");
		if (gameType != null && gameType.equals(serverType.toString())) {
			String gameId = JedisManager.getJedisCluster().hget(key, "gameId");
			if (gameId != null) {
				serverInfo = ServerManager.getInstance().getGameServerInfo(serverType, Integer.parseInt(gameId));
			}
		}

		// 随机选择一个空闲的服务器
		if (serverInfo == null) {
			serverInfo = ServerManager.getInstance().getIdleGameServer(serverType,userSession);
		}

		if (serverInfo == null) {
			SystemErrorResponse response = ServerManager.getInstance()
					.buildSystemErrorResponse(SystemErroCode.GameNotFind, "游戏服务器维护中");
			userSession.getClientSession().write(response);
			LOGGER.warn("{} 没有可用服务器", serverType.toString());
			return;
		}
		userSession.setServerType(serverType);
		userSession.setServerId(serverInfo.getId());

		//设置玩家登录游戏服属性
		Map<String, String> redisMap=new HashMap<>(2);
		redisMap.put("gameId", String.valueOf(serverInfo.getId()));
		redisMap.put("gameType", String.valueOf(serverType.getType()));
		JedisManager.getJedisCluster().hmset(key, redisMap);
		
		userSession.sendToGame(request);

	}

}
