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
 * @version $Id: $Id
 */
public class MemoryPool<T extends IMemoryObject> implements Serializable {

	private static final long serialVersionUID = 943760723073862247L;

	private final List<T> cache = Collections.synchronizedList(new ArrayList<T>());

	private int MAX_SIZE = 500;

	/**
	 * <p>Constructor for MemoryPool.</p>
	 */
	public MemoryPool() {
	}

	/**
	 * <p>Constructor for MemoryPool.</p>
	 *
	 * @param max a int.
	 */
	public MemoryPool(int max) {
		MAX_SIZE = max;
	}

	/**
	 * 放回对象池并释放资源属性
	 *
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年11月8日 下午2:46:59
	 * @param value a T object.
	 */
	public void put(T value) {
		synchronized (cache) {
			if (!cache.contains(value) && cache.size() < MAX_SIZE) {
				value.reset();
				cache.add(value);
			}
		}
	}

	/**
	 * 批量放回
	 *
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年11月8日 下午2:53:22
	 * @param values a T object.
	 */
	@SuppressWarnings("unchecked")
	public void putAll(T... values) {
		synchronized (cache) {
			for (T value : values) {
				if (value == null) {
					continue;
				}
				if (!cache.contains(value) && cache.size() < MAX_SIZE) {
					cache.add(value);
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
	 * @param c a {@link java.lang.Class} object.
	 * @return a T object.
	 */
	public T get(Class<? extends T> c) {
		synchronized (cache) {
			if (!cache.isEmpty()) {
				return cache.remove(0);
			}
			try {
				return c.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * <p>size.</p>
	 *
	 * @return a int.
	 */
	public int size() {
		return cache.size();
	}

	/**
	 * <p>clear.</p>
	 */
	public void clear() {
		synchronized (cache) {
			cache.clear();
		}
	}

	/**
	 * <p>getMAX_SIZE.</p>
	 *
	 * @return a int.
	 */
	public int getMAX_SIZE() {
		return MAX_SIZE;
	}

	/**
	 * <p>setMAX_SIZE.</p>
	 *
	 * @param MAX_SIZE a int.
	 */
	public void setMAX_SIZE(int MAX_SIZE) {
		this.MAX_SIZE = MAX_SIZE;
	}

}
