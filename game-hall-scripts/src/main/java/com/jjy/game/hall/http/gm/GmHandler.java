package com.jjy.game.hall.http.gm;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.hall.manager.RoleManager;
import com.jjy.game.hall.server.HallServer;
import com.jjy.game.message.ServerMessage.ServerEventRequest;
import com.jjy.game.model.constant.Config;
import com.jjy.game.model.redis.key.HallKey;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.HttpHandler;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.engine.redis.jedis.JedisManager;
import com.jzy.game.engine.util.JsonUtil;
import com.jzy.game.engine.util.MsgUtil;

/**
 * GM处理器，所有gm在大厅处理，捕鱼游戏等用redis发布
 * <p>
 * 	http://192.168.0.17:9102/gm?cmd=addGold&roleId=1&gold=10000&auth=daa0cf5b-e72d-422c-b278-450b28a702c6
 * </p>
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年10月16日 下午5:07:12
 */
@HandlerEntity(path="/gm")
public class GmHandler extends HttpHandler {
	private static final Logger LOGGER=LoggerFactory.getLogger(GmHandler.class);
	private static final Map<String, Method> GM_METHOD=JsonUtil.getFieldMethodMap(GmHandler.class, null);

	@Override
	public void run() {
		String auth = getString("auth");
		MinaServerConfig minaServerConfig = HallServer.getInstance().getHallHttpServer().getMinaServerConfig();
		if (!Config.SERVER_AUTH.equals(auth)||!minaServerConfig.getIp().startsWith("192")||!minaServerConfig.getIp().startsWith("127")) {
			sendMsg("权限验证失败");
			return;
		}
		String result = execute();
		
		LOGGER.info("{}使用GM结果:{}",MsgUtil.getIp(getSession()),result);
		if(getSession()!=null) {
			sendMsg(result);
		}
	}
	
	/**
	 * 执行命令
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年10月16日 下午6:32:04
	 * @return
	 */
	public String execute() {
		String cmd = getString("cmd"); 	//命令，方法名称
		if(cmd.equalsIgnoreCase("execute")) {
			return "指令错误";
		}
		String result=String.format("GM %s 执行失败", getMessage().getQueryString());
		if(GM_METHOD.containsKey(cmd.trim())) {
			Method method = GM_METHOD.get(cmd);
			try {
				result=(String)method.invoke(this);
			} catch (Exception e) {
				LOGGER.error("GM",e);
			} 
		}
		return result;
	}
	
	
	/**
	 * 增减金币
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年10月16日 下午5:45:05
	 * @return
	 */
	protected String addGold() {
		long roleId = getLong("roleId");
		int gold=getInt("gold");
		String key=HallKey.Role_Map_Info.getKey(roleId);
		JedisManager.getJedisCluster().hincrBy(key, "gold", gold);
		String info=String.format("角色%d增加%d金币", roleId,gold);
		RoleManager.getInstance().publishGoldChange(roleId, gold);
		return info;
	}
	
	/**
	 * 设置角色等级
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年10月16日 下午5:57:26
	 * @return
	 */
	protected String setLevel() {
		long roleId = getLong("roleId");
		String level=getString("level");
		String key=HallKey.Role_Map_Info.getKey(roleId);
		JedisManager.getJedisCluster().hset(key, "level", level);
		String info=String.format("角色%d等级设为%d", roleId,level);
		//TODO 通知子游戏改变？
		return info;
		
	}
	
	/**
	 * 下线玩家
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年10月17日 下午4:59:54
	 * @return
	 */
	protected String offLineRole() {
		long roleId = getLong("roleId");
		ServerEventRequest.Builder builder=ServerEventRequest.newBuilder();
		builder.setType(1);
		builder.setId(roleId);
		HallServer.getInstance().getHall2GateClient().broadcastMsg(builder.build(), roleId);
		return String.format("角色%d将被踢下线", roleId);
	}

}
