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
 * @version $Id: $Id
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

	/**
	 * <p>Getter for the field <code>type</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getType() {
		return type;
	}

	/**
	 * <p>Setter for the field <code>type</code>.</p>
	 *
	 * @param type a {@link java.lang.String} object.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * <p>Getter for the field <code>start</code>.</p>
	 *
	 * @return a long.
	 */
	public long getStart() {
		return start;
	}

	/**
	 * <p>Setter for the field <code>start</code>.</p>
	 *
	 * @param start a long.
	 */
	public void setStart(long start) {
		this.start = start;
	}

	/**
	 * <p>Getter for the field <code>delay</code>.</p>
	 *
	 * @return a long.
	 */
	public long getDelay() {
		return delay;
	}

	/**
	 * <p>Setter for the field <code>delay</code>.</p>
	 *
	 * @param delay a long.
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}

	/**
	 * <p>Getter for the field <code>key</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * <p>Setter for the field <code>key</code>.</p>
	 *
	 * @param key a {@link java.lang.String} object.
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * 获取结束时间
	 *
	 * @return a long.
	 */
	@JSONField(serialize = false)
	public long getEndTime() {
		return start + delay;
	}

	/**
	 * 冷却完成处理
	 *
	 * @param t a T object.
	 * @param consumer a {@link java.util.function.Consumer} object.
	 * @param <T> a T object.
	 */
	@JSONField(serialize = false)
	public <T> void coolingFinsh(Consumer<T> consumer, T t) {
		if (getRemainTime() <= 0) {
			if (consumer != null) {
				consumer.accept(t);
			}
		}
	}

	/**
	 * 获取剩余时间
	 *
	 * @return a long.
	 */
	@JSONField(serialize = false)
	public long getRemainTime() {
		return getEndTime() - TimeUtil.currentTimeMillis();
	}

	/** {@inheritDoc} */
	@JSONField(serialize = false)
	@Override
	public void reset() {
		setKey(null);
		setType(null);
		setStart(0);
		setDelay(0);
	}

	/** {@inheritDoc} */
	@JSONField(serialize = false)
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
