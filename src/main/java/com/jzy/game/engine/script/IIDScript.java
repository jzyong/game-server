package com.jzy.game.engine.script;

/**
 * 有ID的脚本
 *
 * @author JiangZhiYong
 * @date 2017-03-30
 * QQ:359135103
 */
public interface IIDScript extends IScript{
	
	/**
	 * 
	 * @return 脚本ID，一般用于处理特殊的逻辑，策划配置的ID
	 */
    int getModelID();
}
