package com.jjy.game.bydr.script.role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.bydr.script.IRoleScript;
import com.jjy.game.bydr.struct.role.Role;
import com.jjy.game.model.constant.Reason;
import com.jjy.game.model.redis.key.HallKey;
import com.jzy.game.engine.redis.jedis.JedisManager;

/**
 * 修改角色金币
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月25日 下午5:21:32
 */
public class RoleGoldChangeScript implements IRoleScript {
	private static final Logger LOGGER = LoggerFactory.getLogger(RoleGoldChangeScript.class);

	@Override
	public void changeGold(Role role, int add, Reason reason) {
		long gold = role.getGold() + add;
		if (gold < 0 || gold > Long.MAX_VALUE) {
			LOGGER.warn("玩家更新金币异常,{}+{}={}", role.getGold(), add, gold);
			role.setGold(0);
		}
		role.setGold(gold);
		if (reason == Reason.RoleFire) {
			role.setWinGold(role.getWinGold() + add);
		}
	}

	@Override
	public void syncGold(Role role, Reason reason) {
		if (role.getWinGold() != 0) {
			String key = HallKey.Role_Map_Info.getKey(role.getId());

			long addAndGet = JedisManager.getJedisCluster().hincrBy(key, "gold", role.getWinGold());
			role.setWinGold(0);
			LOGGER.debug("更新后金币为{}", addAndGet);
		}

	}

}
