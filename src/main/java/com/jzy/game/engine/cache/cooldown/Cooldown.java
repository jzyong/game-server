package com.jzy.game.engine.cache.cooldown;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.jzy.game.engine.cache.IMemoryObject;
import com.jzy.game.engine.util.TimeUtil;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * 冷却信息类
 *
 * @author JiangZhiYong
 * @date 2017-05-03 QQ:359135103
 */
public class Cooldown implements IMemoryObject, Serializable {

	private static final long serialVersionUID = 2294363482787659688L;

	// 冷却类型
	private String type;
	// 关键字
	private String key;
	// 开始时间
	private long start;
	// 持续时间
	private long delay;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * 获取结束时间
	 *
	 * @return
	 */
	@JSONField(serialize = false)
	public long getEndTime() {
		return start + delay;
	}

	/**
	 * 冷却完成处理
	 *
	 * @param T
	 *            待处理的冷却对象
	 * @param t
	 */
	@JSONField(serialize = false)
	public <T extends Object> void coolingFinsh(Consumer<T> consumer, T t) {
		if (getRemainTime() <= 0) {
			if (consumer != null) {
				consumer.accept(t);
			}
		}
	}

	/**
	 * 获取剩余时间
	 *
	 * @return
	 */
	@JSONField(serialize = false)
	public long getRemainTime() {
		return getEndTime() - TimeUtil.currentTimeMillis();
	}

	@JSONField(serialize = false)
	@Override
	public void release() {
		this.setKey(null);
		this.setType(null);
		this.setStart(0);
		this.setDelay(0);
	}

	@JSONField(serialize = false)
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
