package com.jjy.game.hall.script.role;

import java.util.function.Consumer;

import com.jjy.game.hall.script.IRoleScript;
import com.jjy.game.model.mongo.hall.dao.HallInfoDao;
import com.jjy.game.model.struct.Role;


/**
 * 创建角色
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年7月7日 下午4:34:02
 */
public class CreateRoleScript implements IRoleScript {

	@Override
	public Role createRole(long userId, Consumer<Role> roleConsumer) {
		Role role = new Role();
		role.setId(HallInfoDao.getRoleId());
		role.setUserId(userId);
		if (roleConsumer != null) {
			roleConsumer.accept(role);
		}
		return role;
	}

}
