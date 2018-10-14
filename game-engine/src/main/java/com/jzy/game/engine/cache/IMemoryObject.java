package com.jzy.game.engine.cache;

/**
 * 对象池对象
 *
 * @author JiangZhiYong
 * @date 2017-04-26 QQ:359135103
 */
public interface IMemoryObject {

	/**
	 * 对象释放并重置
	 */
    void reset();
}
