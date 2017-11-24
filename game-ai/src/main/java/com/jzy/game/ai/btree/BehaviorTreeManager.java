package com.jzy.game.ai.btree;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.struct.Person;

/**
 * 行为树
 * TODO 行为树克隆，缓存，行为树文件解析
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年11月24日 下午2:12:40
 */
public class BehaviorTreeManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(BehaviorTreeManager.class);
	private static volatile BehaviorTreeManager behaviorTreeManager;

	/**行为树对象缓存*/
	private Map<String, BehaviorTree<? extends Person>> behaviorTrees = new HashMap<>();

	private BehaviorTreeManager() {

	}

	public BehaviorTreeManager getInstance() {
		if (behaviorTreeManager == null) {
			synchronized (BehaviorTreeManager.class) {
				if (behaviorTreeManager == null) {
					behaviorTreeManager = new BehaviorTreeManager();
				}
			}
		}
		return behaviorTreeManager;
	}
}
