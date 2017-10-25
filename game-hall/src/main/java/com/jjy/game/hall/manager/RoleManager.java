package com.jjy.game.hall.manager;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.hall.script.IRoleScript;
import com.jjy.game.message.hall.HallPacketMessage.PacketItemsResponse;
import com.jjy.game.model.constant.Reason;
import com.jjy.game.model.mongo.hall.dao.RoleDao;
import com.jjy.game.model.redis.channel.HallChannel;
import com.jjy.game.model.redis.key.HallKey;
import com.jjy.game.model.struct.Item;
import com.jjy.game.model.struct.Role;
import com.jzy.game.engine.redis.jedis.JedisManager;
import com.jzy.game.engine.redis.jedis.JedisPubSubMessage;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.engine.util.JsonUtil;

/**
 * 角色管理
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月7日 下午4:00:37
 */
public class RoleManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(RoleManager.class);
	private static volatile RoleManager roleManager;

	/** role 数据需要实时存数据库 */
	private Map<Long, Role> roles = new ConcurrentHashMap<>();

	private RoleManager() {

	}

	public static RoleManager getInstance() {
		if (roleManager == null) {
			synchronized (RoleManager.class) {
				if (roleManager == null) {
					roleManager = new RoleManager();
				}
			}
		}
		return roleManager;
	}

	/**
	 * 创建角色
	 * 
	 * @param userConsumer
	 * @return
	 */
	public Role createUser(long userId, Consumer<Role> roleConsumer) {
		Collection<IRoleScript> evts = ScriptManager.getInstance().getBaseScriptEntry().getEvts(IRoleScript.class);
		Iterator<IRoleScript> iterator = evts.iterator();
		while (iterator.hasNext()) {
			IRoleScript userScript = iterator.next();
			Role role = userScript.createRole(userId, roleConsumer);
			if (role != null) {
				return role;
			}
		}
		return null;
	}

	public Map<Long, Role> getRoles() {
		return roles;
	}

	public Role getRole(long id) {
		Role role = roles.get(id);
		Map<String, String> hgetAll = JedisManager.getJedisCluster().hgetAll(role.getRoleRedisKey());
		// 从redis读取最新数据
		if (hgetAll != null && role != null) {
			JsonUtil.map2Object(hgetAll, role);
		}
		return role;
	}

	/**
	 * 登陆
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月18日 下午6:23:14
	 * @param role
	 */
	public void login(Role role, Reason reason) {
		ScriptManager.getInstance().getBaseScriptEntry().executeScripts(IRoleScript.class,
				script -> script.login(role, reason));
	}

	/**
	 * 退出
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月18日 下午6:28:51
	 * @param rid
	 * @param reason
	 */
	public void quit(long rid, Reason reason) {
		quit(getRole(rid), reason);
	}

	/**
	 * 退出游戏
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月18日 下午6:09:51
	 * @param role
	 * @param reason
	 */
	public void quit(Role role, Reason reason) {
		ScriptManager.getInstance().getBaseScriptEntry().executeScripts(IRoleScript.class,
				script -> script.quit(role, reason));
	}

	/**
	 * 广播金币改变
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年10月17日 上午10:11:59
	 * @param roleId
	 * @param gold
	 *            金币改变量
	 */
	public void publishGoldChange(long roleId, int gold) {
		String gameIdStr = JedisManager.getJedisCluster().hget(HallKey.Role_Map_Info.getKey(roleId), "gameId");
		if (gameIdStr != null && !gameIdStr.equals("0")) {
			JedisPubSubMessage message = new JedisPubSubMessage(roleId, Integer.parseInt(gameIdStr), gold);
			JedisManager.getJedisCluster().publish(HallChannel.HallGoldChange.name(), message.toString());
		}
	}

}
