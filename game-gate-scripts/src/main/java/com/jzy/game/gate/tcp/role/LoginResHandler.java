package com.jzy.game.gate.tcp.role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.message.Mid.MID;
import com.jzy.game.message.hall.HallLoginMessage.LoginResponse;
import com.jzy.game.message.system.SystemMessage.SystemErroCode;
import com.jzy.game.model.redis.key.HallKey;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.engine.redis.jedis.JedisManager;
import com.jzy.game.engine.thread.ThreadType;
import com.jzy.game.gate.manager.ServerManager;
import com.jzy.game.gate.manager.UserSessionManager;
import com.jzy.game.gate.struct.UserSession;

/**
 * 登陆返回
 *<p>处理用户的连接session,设置UserSession用户ID，角色ID</p>
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
@HandlerEntity(mid = MID.LoginRes_VALUE, desc = "登陆", thread = ThreadType.IO, msg = LoginResponse.class)
public class LoginResHandler extends TcpHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginResHandler.class);

	@Override
	public void run() {
		LoginResponse response = getMsg();
		UserSession userSession = UserSessionManager.getInstance().getUserSessionBySessionId(response.getSessionId());
		if (userSession == null) {
			session.write(
					ServerManager.getInstance().buildSystemErrorResponse(SystemErroCode.ConectReset, "连接会话已失效，请重连"));
			LOGGER.warn("连接会话已失效，请重连");
			return;
		}
		String key = HallKey.Role_Map_Info.getKey(userSession.getRoleId());
		JedisManager.getJedisCluster().hset(key, "hallId", String.valueOf(userSession.getHallServerId()));
		UserSessionManager.getInstance().loginHallSuccess(userSession, response.getUid(), response.getRid());
		userSession.sendToClient(response);
	}

	

}
