package com.jzy.game.model.mongo.bydr.dao;

import java.util.List;
import org.mongodb.morphia.dao.BasicDAO;
import com.jzy.game.engine.mongo.AbsMongoManager;
import com.jzy.game.model.mongo.bydr.entity.CFish;

/**
 * 角色
 *
 * @author JiangZhiYong
 * @date 2017-02-27 QQ:359135103
 */
public class CFishDao extends BasicDAO<CFish, Integer> {

	private static volatile CFishDao cFishDao;

	public CFishDao(AbsMongoManager mongoManager) {
		super(CFish.class, mongoManager.getMongoClient(), mongoManager.getMorphia(),
				mongoManager.getMongoConfig().getDbName());
	}

	public static CFishDao getDB(AbsMongoManager mongoManager) {
		if (cFishDao == null) {
			synchronized (CFishDao.class) {
				if (cFishDao == null) {
					cFishDao = new CFishDao(mongoManager);
					// cFishDao.getDs().ensureIndexes(true);
				}
			}
		}
		return cFishDao;
	}

	public static List<CFish> getAll() {
		return cFishDao.createQuery().asList();
	}

}
