package com.jzy.game.engine.thread;

import java.util.concurrent.Executor;

/**
 * 自定义线程池
 *
 * @author JiangZhiYong
 * @date 2017-04-21
 * QQ:359135103
 */
public interface ExecutorPool extends Executor {

	/**
	 * 关闭线程
	 */
	void stop() ;
	

}
