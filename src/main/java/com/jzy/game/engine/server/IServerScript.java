package com.jzy.game.engine.server;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;

import com.jzy.game.engine.script.IScript;

/**
 * 服务器脚本
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年9月4日 上午10:45:15
 */
public interface IServerScript extends IScript{

	/**
	 * mina 添加过滤器
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年9月4日 上午10:46:33
	 * @param chain
	 */
	default void addFilter(DefaultIoFilterChainBuilder chain){
		
	}
}
