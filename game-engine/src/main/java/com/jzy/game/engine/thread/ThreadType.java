package com.jzy.game.engine.thread;

/**
 * 线程类型
 *
 * @author JiangZhiYong
 * @date 2017-03-30
 * QQ:359135103
 */
public enum ThreadType {
	/**耗时的线程池*/
	IO,
	/**全局同步线程*/
	SYNC,
	/**房间*/
	ROOM
}
