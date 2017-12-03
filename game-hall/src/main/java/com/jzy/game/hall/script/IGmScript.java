package com.jzy.game.hall.script;

import com.jzy.game.engine.script.IScript;
import com.jzy.game.model.struct.Role;

/**
 * gm脚本
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年10月16日 下午6:04:35
 */
public interface IGmScript extends IScript {
	
	default String executeGm(long roleId,String gmCmd) {
		return String.format("GM {}未执行", gmCmd);
	}
	
	/**
	 * 是否为gm命令
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年10月16日 下午6:07:32
	 * @param gmCmd
	 */
	default boolean isGMCmd(String gmCmd) {
		return gmCmd!=null&&gmCmd.startsWith("&");
	}
}
