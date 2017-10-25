package com.jjy.game.hall.script.role;

import java.util.Map;

import com.jjy.game.hall.manager.RoleManager;
import com.jjy.game.hall.script.IRoleScript;
import com.jjy.game.model.constant.Reason;
import com.jjy.game.model.mongo.hall.dao.RoleDao;
import com.jjy.game.model.redis.key.HallKey;
import com.jjy.game.model.struct.Role;
import com.jzy.game.engine.redis.jedis.JedisManager;
import com.jzy.game.engine.util.JsonUtil;

import redis.clients.jedis.Jedis;

/**
 * 角色退出游戏
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月18日 下午6:01:57
 */
public class RoleQuitScript implements IRoleScript {

	@Override
	public void quit(Role role, Reason reason) {
		RoleManager.getInstance().getRoles().remove(role.getId());
		saveData(role);
	}

	/**
	 * 存储数据
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年9月18日 下午6:06:41
	 * @param role
	 */
	private void saveData(Role role) {
		// ----- mongodb -----
		RoleDao.saveRole(role);

		//----- redis-----
		
		//角色 数据不保存，需要实时存储
//		String key = HallKey.Role_Map_Info.getKey(role.getId());
//		Map<String, String> map = JsonUtil.object2Map(role);
//		JedisManager.getJedisCluster().hmset(key, map);
	}

}
