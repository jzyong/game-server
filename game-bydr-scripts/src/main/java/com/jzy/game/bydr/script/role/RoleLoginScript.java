package com.jzy.game.bydr.script.role;

import java.util.Map;
import java.util.function.Consumer;

import org.redisson.api.RMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.bydr.manager.RoleManager;
import com.jzy.game.bydr.script.IRoleScript;
import com.jzy.game.bydr.server.BydrServer;
import com.jzy.game.bydr.struct.role.Role;
import com.jzy.game.engine.redis.jedis.JedisManager;
import com.jzy.game.engine.redis.redisson.FastJsonCodec;
import com.jzy.game.engine.redis.redisson.RedissonManager;
import com.jzy.game.engine.util.JsonUtil;
import com.jzy.game.model.constant.Config;
import com.jzy.game.model.constant.Reason;
import com.jzy.game.model.redis.key.HallKey;

/**
 * 登录
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年8月4日 下午2:14:53
 */
public class RoleLoginScript implements IRoleScript {
	private static final Logger LOGGER = LoggerFactory.getLogger(RoleLoginScript.class);

	@Override
	public void login(long roleId, Reason reason, Consumer<Role> roleConsumer) {
		Role role = RoleManager.getInstance().loadRoleData(roleId);
		if (role == null) {
			role = new Role();
			role.setId(roleId);
		}
		role.setGameId(Config.SERVER_ID);
		if (roleConsumer != null) {
			roleConsumer.accept(role);
		}
		RoleManager.getInstance().getOnlineRoles().put(roleId, role);

		// 同步大厅角色数据，昵称、头像等
		syncHallData(role);
		tempInit(role);
	}

	/**
	 * 同步大厅数据
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月26日 下午2:44:18
	 * @param role
	 */
	private void syncHallData(Role role) {
		//同步角色
		String key = HallKey.Role_Map_Info.getKey(role.getId());
		Map<String, String> hgetAll = JedisManager.getJedisCluster().hgetAll(key);
		if (hgetAll == null) {
			LOGGER.warn("{}为找到角色数据", key);
			return;
		}
		com.jzy.game.model.struct.Role hallRole = new com.jzy.game.model.struct.Role();
		JsonUtil.map2Object(hgetAll, hallRole);
		role.setNick(hallRole.getNick());
		role.setGold(hallRole.getGold());
		role.setLevel(hallRole.getLevel());
		RoleManager.getInstance().saveRoleData(role);

		
//		//加载大厅数据
//		// 道具
//		RMap<Long, Item> items = RedissonManager.getRedissonClient()
//				.getMap(HallKey.Role_Map_Packet.getKey(role.getId()),new FastJsonCodec(Long.class,Item.class));
//		role.setItems(items);

	}

	/**
	 * 临时初始化
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月25日 下午5:31:37
	 * @param role
	 */
	private void tempInit(Role role) {
		// 赠送金币
		if (role.getGold() < 100) {
			role.changeGold(100000, Reason.RoleFire);
		}
	}

}
