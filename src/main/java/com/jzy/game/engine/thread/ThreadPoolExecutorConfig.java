package com.jzy.game.engine.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 线程池配置
 *
 * @author JiangZhiYong
 * @date 2017-03-30 QQ:359135103
 */
@Root
public class ThreadPoolExecutorConfig {

	// 线程池名称
	@Element(required = false)
	private String name;

	// 核心线程池值
	@Element(required = true)
	private int corePoolSize = 20;

	// 线程池最大值
	@Element(required = true)
	private int maxPoolSize = 200;

	// 线程池保持活跃时间(秒)
	@Element(required = true)
	private long keepAliveTime = 30L;

	// 心跳间隔（大于0开启定时监测线程）
	@Element(required = false)
	private int heart = 0;

	// 缓存命令数
	@Element(required = false)
	private int commandSize = 100000;

	public ThreadPoolExecutor newThreadPoolExecutor() throws RuntimeException {
		return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(commandSize), new IoThreadFactory());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	@Deprecated
	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	@Deprecated
	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public long getKeepAliveTime() {
		return keepAliveTime;
	}

	@Deprecated
	public void setKeepAliveTime(long keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

	public int getHeart() {
		return heart;
	}

	public void setHeart(int heart) {
		this.heart = heart;
	}

	public int getCommandSize() {
		return commandSize < 10000 ? 10000 : commandSize;
	}

	public void setCommandSize(int commandSize) {
		this.commandSize = commandSize;
	}

}
