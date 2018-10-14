/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jzy.game.gate.manager;

import com.jzy.game.engine.mongo.AbsMongoManager;
import com.jzy.game.model.mongo.hall.dao.HallInfoDao;
import com.jzy.game.model.mongo.hall.dao.RoleDao;
import com.jzy.game.model.mongo.hall.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mongodb
 *
 * @author JiangZhiYong
 * @QQ 359135103
 */
public class MongoManager extends AbsMongoManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoManager.class);
    private static final MongoManager INSTANCE_MANAGER = new MongoManager();

    private MongoManager() {

    }

    public static MongoManager getInstance() {
        return INSTANCE_MANAGER;
    }

    @Override
    protected void initDao() {
        HallInfoDao.getDB(INSTANCE_MANAGER);
        UserDao.getDB(INSTANCE_MANAGER);
        RoleDao.getDB(INSTANCE_MANAGER);
    }

}
