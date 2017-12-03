package com.jzy.game.bydr.script.role;

import com.jzy.game.bydr.manager.RoleManager;
import com.jzy.game.bydr.script.IRoleScript;
import com.jzy.game.bydr.struct.role.Role;
import com.jzy.game.model.constant.Reason;

/**
 * 退出
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年8月4日 下午2:14:32
 */
public class RoleQuitScript implements IRoleScript {

	@Override
	public void quit(Role role, Reason reason) {
		RoleManager roleManager = RoleManager.getInstance();
		
		role.setGameId(0);
		roleManager.getOnlineRoles().remove(role.getId());
		role.syncGold(Reason.UserQuit);
		roleManager.saveRoleData(role);
	}

	
}
