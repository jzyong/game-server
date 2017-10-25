package com.jjy.game.bydr.script.role;

import com.jjy.game.bydr.manager.RoleManager;
import com.jjy.game.bydr.script.IRoleScript;
import com.jjy.game.bydr.struct.role.Role;
import com.jjy.game.model.constant.Reason;

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
