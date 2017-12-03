/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jzy.game.model.mongo.hall.dao;

import com.jzy.game.engine.mongo.AbsMongoManager;
import com.jzy.game.model.struct.HallInfo;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

/**
 * 大厅信息
 *
 * @author JiangZhiYong
 * @QQ 359135103
 */
public class HallInfoDao extends BasicDAO<HallInfo, Integer> {

	private static volatile HallInfoDao hallInfoDao;

	private HallInfoDao(AbsMongoManager mongoManager) {
		super(HallInfo.class, mongoManager.getMongoClient(), mongoManager.getMorphia(),
				mongoManager.getMongoConfig().getDbName());
	}

	public static HallInfoDao getDB(AbsMongoManager mongoManager) {
		if (hallInfoDao == null) {
			synchronized (HallInfoDao.class) {
				if (hallInfoDao == null) {
					hallInfoDao = new HallInfoDao(mongoManager);
				}
			}
		}
		return hallInfoDao;
	}

	/**
	 * 用户ID
	 * 
	 * @return
	 */
	public static long getUserId() {
		HallInfo hallInfo = incFieldNum(1, "userIdNum");
		return hallInfo.getUserIdNum();
	}

	/**
	 * 角色ID
	 * 
	 * @return
	 */
	public static long getRoleId() {
		HallInfo hallInfo = incFieldNum(1, "roleIdNum");
		return hallInfo.getRoleIdNum();
	}

	/**
	 * 增加属性的值
	 * 
	 * @param num
	 *            增加量
	 * @param fieldName
	 *            属性名称
	 * @return
	 */
	public static HallInfo incFieldNum(int num, String fieldName) {
		Query<HallInfo> query = hallInfoDao.createQuery();
		UpdateOperations<HallInfo> updateOperations = hallInfoDao.createUpdateOperations().inc(fieldName, num);
		HallInfo hallInfo = hallInfoDao.getDs().findAndModify(query, updateOperations);
		if (hallInfo == null) {
			hallInfo = new HallInfo();
			hallInfoDao.save(hallInfo);
			hallInfo = hallInfoDao.getDs().findAndModify(query, updateOperations);
		}
		return hallInfo;
	}

}
