package com.jjy.game.model.mongo;

import java.util.Map;

/**
 * 配置实体检测接口
 * <br>
 * 配置文件之间存在依赖关系，在加载检测策划配置数据是否正确
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年10月18日 下午4:25:11
 */
public interface IConfigChecker {
	default <V> boolean check(Map<Integer, V> source) throws Exception {
		return true;
	}

	default <V, W> boolean check(Map<Integer, V> source, Map<Integer, W> source2) throws Exception {
		return true;
	}

	default <V, W, X> boolean check(Map<String, V> source, Map<Integer, W> source2, Map<Integer, X> source3)
			throws Exception {
		return true;
	}

	/**
	 * 数据检测
	 */
	default boolean check() throws Exception {
		return true;
	}
}
