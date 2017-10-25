package com.jjy.game.bydr.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.model.mongo.bydr.dao.CFishDao;
import com.jzy.game.engine.mongo.AbsMongoManager;

/**
 * mongodb
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年6月28日 下午3:33:14
 */
public class MongoManager extends AbsMongoManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoManager.class);
	private static final MongoManager INSTANCE_MANAGER = new MongoManager();

	public static final MongoManager getInstance() {
		return INSTANCE_MANAGER;
	}

	@Override
	protected void initDao() {
		CFishDao.getDB(INSTANCE_MANAGER);

	}

}
