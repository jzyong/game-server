package com.jzy.game.engine.thread.timer;

/**
 * 定时器,end时间大于0表示截至时间到即销毁；loop为-1标识永久循环
 *
 * @author JiangZhiYong
 * @date 2017-03-30 QQ:359135103
 */
public abstract class TimerEvent implements Runnable {

	// 定时器结束时间
	private long end;
	// 定时器循环次数
	private int loop = -1;

	public TimerEvent(long end, int loop) {
		this.end = end;
		this.loop = loop;
	}

	protected TimerEvent(long end) {
		this(end, -1);
	}

	public TimerEvent() {

	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public long remain() {
		if (end == 0 || loop < 0) {
			return 0;
		}
		return end - System.currentTimeMillis();
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public int getLoop() {
		return loop;
	}

	public void setLoop(int loop) {
		this.loop = loop;
	}
}
