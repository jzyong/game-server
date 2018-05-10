package com.jzy.game.engine.mongo;

import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.util.FileUtil;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * mongodb 管理类
 *
 * @author JiangZhiYong
 * @date 2017-04-14 QQ:359135103
 */
public abstract class AbsMongoManager {

    static Logger log = LoggerFactory.getLogger(AbsMongoManager.class);

    private MongoClient mongoClient = null;
    private Morphia morphia = null;
    private MongoClientConfig mongoConfig;

    /**
     * 创建mongodb连接
     *
     * @param configPath
     */
    public void createConnect(String configPath) {
        mongoConfig = FileUtil.getConfigXML(configPath, "mongoClientConfig.xml", MongoClientConfig.class);
        if (mongoConfig == null) {
            throw new RuntimeException(String.format("mongodb 配置文件 %s/MongoClientConfig.xml 未找到", configPath));
        }
        MongoClientURI uri = new MongoClientURI(mongoConfig.getUrl());
        mongoClient = new MongoClient(uri);
        morphia = new Morphia();
        morphia.mapPackage("");

        initDao();
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public Morphia getMorphia() {
        return morphia;
    }

    public MongoClientConfig getMongoConfig() {
        return mongoConfig;
    }

    /**
     * 初始化dao
     */
    protected abstract void initDao();

}
