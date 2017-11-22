/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jjy.game.gate.manager;

import com.jjy.game.model.mongo.hall.dao.HallInfoDao;
import com.jjy.game.model.mongo.hall.dao.RoleDao;
import com.jjy.game.model.mongo.hall.dao.UserDao;
import com.jzy.game.engine.mongo.AbsMongoManager;

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

    public static final MongoManager getInstance() {
        return INSTANCE_MANAGER;
    }

    @Override
    protected void initDao() {
        HallInfoDao.getDB(INSTANCE_MANAGER);
        UserDao.getDB(INSTANCE_MANAGER);
        RoleDao.getDB(INSTANCE_MANAGER);
    }

}
