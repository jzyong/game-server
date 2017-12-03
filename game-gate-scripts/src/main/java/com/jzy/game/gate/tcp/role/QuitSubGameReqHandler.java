package com.jzy.game.gate.tcp.role;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.message.Mid.MID;
import com.jzy.game.message.hall.HallLoginMessage.QuitSubGameRequest;
import com.jzy.game.model.constant.Config;
import com.jzy.game.model.redis.key.HallKey;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.engine.redis.jedis.JedisManager;
import com.jzy.game.engine.server.ServerType;
import com.jzy.game.engine.util.MsgUtil;
import com.jzy.game.gate.struct.UserSession;

/**
 * 退出子游戏
 * // TODO 处理用户session 请求处理，还是返回处理，根据客户端需求
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年7月26日 下午5:34:06
 */
@HandlerEntity(mid=MID.QuitSubGameReq_VALUE,msg=QuitSubGameRequest.class)
public class QuitSubGameReqHandler extends TcpHandler {
	private static final Logger LOGGER=LoggerFactory.getLogger(QuitSubGameReqHandler.class);

	@Override
	public void run() {
		QuitSubGameRequest req=getMsg();
		Object attribute = getSession().getAttribute(Config.USER_SESSION);
		if(attribute==null){
			LOGGER.warn("{} 无用户会话",MsgUtil.getIp(getSession()));
			return;
		}
		if(req==null){
			req=QuitSubGameRequest.newBuilder().build();
		}
		
		UserSession userSession=(UserSession)attribute;
		userSession.sendToGame(req);
		userSession.removeGame();
		
		//清空角色服务器ID，服务类型，网关统一处理，避免游戏服重复处理
		String key = HallKey.Role_Map_Info.getKey(userSession.getRoleId());
		Map<String, String> redisMap=new HashMap<String, String>();
		redisMap.put("gameType", ServerType.NONE.toString());
		redisMap.put("gameId", String.valueOf(0));
		JedisManager.getJedisCluster().hmset(key, redisMap);
	}

}
