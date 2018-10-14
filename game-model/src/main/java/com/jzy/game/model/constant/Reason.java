package com.jzy.game.model.constant;

/**
 * 全局原因、类型区别
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年7月26日 下午4:43:39
 */
public enum Reason {
	SessionIdle("会话空闲"),
	SessionClosed("会话关闭"),
	
	UserQuit("用户退出"),
	UserLogin("用户登录"),
	UserReconnect("用户登录"),
	
	CrossServer("跨服"),
	
	RoleUseItem("使用物品"),
	RoleFire("开炮"),
	RoleSync("角色同步"),
	
	GmTickRole("gm踢角色下线"),
	
	HallGoldChange("大厅金币改变"),
	
	ServerClose("服务器关闭"),
	;


	private final String reason;

	Reason(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}
	
}
