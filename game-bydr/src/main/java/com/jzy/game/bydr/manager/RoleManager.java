package com.jzy.game.bydr.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.redisson.api.RMap;
import org.redisson.client.codec.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.bydr.script.IRoleScript;
import com.jzy.game.bydr.struct.role.Role;
import com.jzy.game.engine.redis.jedis.JedisManager;
import com.jzy.game.engine.redis.redisson.FastJsonCodec;
import com.jzy.game.engine.redis.redisson.RedissonManager;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.engine.util.JsonUtil;
import com.jzy.game.model.constant.Reason;
import com.jzy.game.model.redis.key.BydrKey;
import com.jzy.game.model.redis.key.HallKey;

/**
 * 角色管理
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月10日 下午4:01:42
 */
public class RoleManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(RoleManager.class);
	private static volatile RoleManager roleManager;

	private final Map<Long, Role> onlineRoles = new ConcurrentHashMap<>(); // 在线的角色

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

	public Map<Long, Role> getOnlineRoles() {
		return onlineRoles;
	}

	public Role getRole(long roleId) {
		return onlineRoles.get(roleId);
	}

	/**
	 * 登录
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年8月3日 下午3:49:37
	 * @param roleId
	 * @param reason
	 * @return 0 成功
	 */
	public void login(long roleId, Reason reason, Consumer<Role> roleConsumer) {
		ScriptManager.getInstance().getBaseScriptEntry().executeScripts(IRoleScript.class,
				script -> script.login(roleId, reason, roleConsumer));
	}

	/**
	 * 退出
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年8月3日 下午3:21:15
	 * @param role
	 * @param reason
	 *            原因
	 */
	public void quit(Role role, Reason reason) {
		ScriptManager.getInstance().getBaseScriptEntry().executeScripts(IRoleScript.class,
				script -> script.quit(role, reason));
	}

	/**
	 * 加载角色数据
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年8月3日 下午3:43:59
	 * @param roleId
	 */
	public Role loadRoleData(long roleId) {
		Map<String, String> roleMap = JedisManager.getJedisCluster().hgetAll(BydrKey.Role_Map.getKey(roleId));
		if (roleMap == null || roleMap.size() < 1) {
			return null;
		}
		Role role = new Role();
		JsonUtil.map2Object(roleMap, role);

		// TODO 其他角色数据
		
//		//大厅角色数据
//		RMap<String, Object> hallRole = RedissonManager.getRedissonClient().getMap(HallKey.Role_Map_Info.getKey(roleId), new StringCodec());
//		role.setHallRole(hallRole);

		return role;
	}

	/**
	 * 存储角色数据
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年8月3日 下午3:22:58
	 * @param role
	 * 
	 *            TODO 存储到mongodb
	 */
	public void saveRoleData(Role role) {
		String key = BydrKey.Role_Map.getKey(role.getId());
		LOGGER.debug("{}存储数据", key);
		JedisManager.getJedisCluster().hmset(key, JsonUtil.object2Map(role));
		//
	}
}
