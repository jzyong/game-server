package com.jzy.game.engine.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 缓存对象
 *
 * @author JiangZhiYong
 * @date 2017-04-26 QQ:359135103
 * @param <T>
 */
public class MemoryPool<T extends IMemoryObject> implements Serializable {

	private static final long serialVersionUID = 943760723073862247L;

	private final List<T> cache = Collections.synchronizedList(new ArrayList<T>());

	private int MAX_SIZE = 500;

	public MemoryPool() {
	}

	public MemoryPool(int max) {
		this.MAX_SIZE = max;
	}

	/**
	 * 放回对象池并释放资源属性
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年11月8日 下午2:46:59
	 * @param value
	 */
	public void put(T value) {
		synchronized (this.cache) {
			if ((!this.cache.contains(value)) && (this.cache.size() < this.MAX_SIZE)) {
				value.reset();
				this.cache.add(value);
			}
		}
	}

	/**
	 * 批量放回
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年11月8日 下午2:53:22
	 * @param values
	 */
	@SuppressWarnings("unchecked")
	public void putAll(T... values) {
		synchronized (this.cache) {
			for (T value : values) {
				if (value == null) {
					continue;
				}
				if ((!this.cache.contains(value)) && (this.cache.size() < this.MAX_SIZE)) {
					this.cache.add(value);
				}
				value.reset();
			}
		}
	}

	/**
	 * 获取缓存对象，如果没有，新建
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年11月8日 下午2:50:05
	 * @param c
	 * @return
	 */
	public T get(Class<? extends T> c) {
		synchronized (this.cache) {
			if (!this.cache.isEmpty()) {
				return this.cache.remove(0);
			}
			try {
				return c.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public int size() {
		return this.cache.size();
	}

	public void clear() {
		synchronized (this.cache) {
			this.cache.clear();
		}
	}

	public int getMAX_SIZE() {
		return MAX_SIZE;
	}

	public void setMAX_SIZE(int MAX_SIZE) {
		this.MAX_SIZE = MAX_SIZE;
	}

}
