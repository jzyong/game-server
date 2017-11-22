/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jjy.game.dbtool.mongo;

import com.jjy.game.tool.util.MongoUtil;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author JiangZhiYong
 * @QQ 359135103
 */
public class MongoTest {

    @Ignore
    @Test
    public void testInsert() {
        MongoClientURI connectionString = new MongoClientURI("mongodb://127.0.0.1");
        MongoClient mongoClient = new MongoClient(connectionString);

        MongoDatabase database = mongoClient.getDatabase("lztb_att");
        MongoCollection<Document> collection = database.getCollection("test");
        Document doc = new Document("name", "MongoDB")
                .append("type", "database")
                .append("count", 1)
                .append("info", new Document("x", 203).append("y", 102));
        new Document().append("1", 1);
        collection.insertOne(doc);
        mongoClient.close();
    }

    @Test
    public void testInsetData() throws Exception{
//        MongoUtil.insertConfigData("E:\\arts\\策划文档\\翻牌机\\翻牌机数值\\ConfigRoom.xlsx", "config_room", "lztb_att");
//        MongoUtil.insertConfigData("E:\\arts\\策划文档\\翻牌机\\翻牌机数值\\ConfigHandsel.xlsx", "config_handsel", "lztb_att");
    }

}
