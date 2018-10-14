package com.jzy.game.hall.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.engine.mongo.AbsMongoManager;
import com.jzy.game.model.mongo.hall.dao.HallInfoDao;
import com.jzy.game.model.mongo.hall.dao.MailDao;
import com.jzy.game.model.mongo.hall.dao.RoleDao;
import com.jzy.game.model.mongo.hall.dao.UserDao;

/**
 * mongodb
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年6月28日 下午3:33:14
 */
public class MongoManager extends AbsMongoManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoManager.class);
	private static final MongoManager INSTANCE_MANAGER = new MongoManager();

	public static MongoManager getInstance() {
		return INSTANCE_MANAGER;
	}

	@Override
	protected void initDao() {
		HallInfoDao.getDB(INSTANCE_MANAGER);
		UserDao.getDB(INSTANCE_MANAGER);
		RoleDao.getDB(INSTANCE_MANAGER);
		MailDao.getDB(INSTANCE_MANAGER);
	}

}
