package com.jjy.game.hall.script;

import com.jjy.game.model.struct.Role;
import com.jzy.game.engine.script.IScript;

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
