/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jjy.game.model.mongo.hall.dao;

import com.jjy.game.model.struct.User;
import com.jzy.game.engine.mongo.AbsMongoManager;

import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

/**
 * 用戶
 *
 * @author JiangZhiYong
 * @QQ 359135103
 */
public class UserDao extends BasicDAO<User, Long> {

    private static volatile UserDao userDao;

    private UserDao(AbsMongoManager mongoManager) {
        super(User.class, mongoManager.getMongoClient(), mongoManager.getMorphia(), mongoManager.getMongoConfig().getDbName());
    }

    public static UserDao getDB(AbsMongoManager mongoManager) {
        if (userDao == null) {
            synchronized (UserDao.class) {
                if (userDao == null) {
                    userDao = new UserDao(mongoManager);
                }
            }
        }
        return userDao;
    }

    public static User findByAccount(String accunt) {
        Query<User> query = userDao.createQuery().filter("accunt", accunt);
        return query.get();
    }
    
    public static void saveUser(User user){
        userDao.save(user);
    }

}
