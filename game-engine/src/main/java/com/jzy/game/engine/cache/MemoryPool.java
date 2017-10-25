package com.jzy.game.engine.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 缓存对象
 *
 * @author JiangZhiYong
 * @date 2017-04-26
 * QQ:359135103
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

    public void put(T value) {
        synchronized (this.cache) {
            if ((!this.cache.contains(value)) && (this.cache.size() < this.MAX_SIZE)) {
                value.release();
                this.cache.add(value);
            }
        }
    }

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

    public int getMAX_SIZE() {
        return MAX_SIZE;
    }

    public void setMAX_SIZE(int MAX_SIZE) {
        this.MAX_SIZE = MAX_SIZE;
    }
    
}
