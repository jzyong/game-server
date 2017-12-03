package com.jzy.game.model.script;

import com.jzy.game.engine.script.IScript;
import com.jzy.game.message.ServerMessage.ServerInfo;

/**
 *  游戏服务器状态监测脚本
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年7月10日 下午4:29:45
 */
public interface IGameServerCheckScript extends IScript{

	/**
	 * 构建服务器状态信息
	 * @param builder
	 */
	public default void buildServerInfo(ServerInfo.Builder builder){
	}
}
