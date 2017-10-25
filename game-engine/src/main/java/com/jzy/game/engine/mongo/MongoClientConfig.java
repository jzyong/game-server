package com.jzy.game.engine.mongo;

import org.simpleframework.xml.Element;

/**
 * Mongo配置文件
 *
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
public class MongoClientConfig {
    
    /**数据库名字*/
    @Element(required = false)
    private String dbName = "lztb_hall";
    /**数据库连接地址*/
    @Element(required = true)
    private String url="mongodb://127.0.0.1:27017/?replicaSet=rs_lztb";

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    
}
