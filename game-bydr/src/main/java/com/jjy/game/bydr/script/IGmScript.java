package com.jjy.game.bydr.script;

import com.jzy.game.engine.handler.HttpHandler;
import com.jzy.game.engine.script.IScript;

/**
 * gm
 *
 * @author JiangZhiYong
 * @date 2017-04-17 QQ:359135103
 */
public interface IGmScript extends IScript{

	/**
	 * 验证http请求sid
	 * @param handler
	 * @return
	 */
	default boolean authHttpSid(HttpHandler handler) {
		return false;
	}
}
