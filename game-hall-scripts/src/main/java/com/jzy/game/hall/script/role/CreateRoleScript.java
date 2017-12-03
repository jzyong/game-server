package com.jzy.game.hall.script.role;

import java.util.function.Consumer;
import com.jzy.game.model.mongo.hall.dao.HallInfoDao;
import com.jzy.game.model.struct.Role;
import com.jzy.game.hall.script.IRoleScript;


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
