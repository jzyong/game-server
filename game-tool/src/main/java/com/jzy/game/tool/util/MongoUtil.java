/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jzy.game.tool.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bson.BsonArray;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mongodb
 *
 * @author JiangZhiYong
 * @QQ 359135103
 */
public class MongoUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoUtil.class);


    /**
     * 获取数据库
     *
     * @param client
     * @param name
     * @return
     */
    public static MongoDatabase getMongoDatabase(MongoClient client, String name) {
        return client.getDatabase(name);
    }

    /**
     * 插入配置数据 先删除，再插入
     *
     * @param filePath
     * @param sheetName
     * @param dbName
     */
    public static String insertConfigData(MongoClient client, String filePath, String sheetName, String dbName) throws Exception {
        String retString = sheetName + "更新成功";
        Args.Five<List<String>, List<String>, List<String>, List<List<Object>>, List<String>> excel = ExcelUtil.readExcel(filePath, sheetName);
        if (excel == null) {
            LOGGER.warn("{}--{}未找到数据", filePath, sheetName);
            return "内部错误";
        }
        List<Document> documents = new ArrayList<>();

        MongoDatabase database = getMongoDatabase(client, dbName);
        if (database == null) {
            LOGGER.warn("{}数据库不存在", dbName);
            return "检查配置数据库不存在";
        }

        MongoCollection<Document> collection = database.getCollection(sheetName);
        if (collection == null) {
            LOGGER.warn("{}数据库集合{}不存在", dbName, collection);
            return "表不存在";
        }
        int row = excel.d().size();     //数据行数
        int column = excel.a().size();  //字段列数
        try {
            for (int i = 0; i < row; i++) {
                Document document = new Document();
                List<Object> datas = excel.d().get(i);
                for (int j = 0; j < column; j++) {
                    //排除客户端配置
                    if ("".equalsIgnoreCase(excel.e().get(j)) || "client".equalsIgnoreCase(excel.e().get(j).toLowerCase())) {
                        continue;
                    }
                    document.append(excel.a().get(j), datas.get(j));
                }
                documents.add(document);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            LOGGER.error("Excel表{}有空行，空列，请删除", sheetName);
            retString = sheetName + "表有空行，空列，请删除";
            return retString;
        }

        if (documents.size() < 1) {
            return sheetName + "数据为空";
        }
        collection.drop();
        collection.insertMany(documents);
        return retString;
    }

    /**
     * json转docment
     *
     * @param jsonStr
     * @return
     */
    public static Document getDocument(String jsonStr) {
        Document document = new Document();
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(jsonStr);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                document.append(entry.getKey(), getRealValue(entry.getValue().toString()));   //TODO 数据类型检查
            }
        }
        return document;
    }

    /**
     * json 数组转文档
     *
     * @param jsonStr
     * @return
     */
    public static List<Object> getDocuments(String jsonStr, String... type) {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(jsonStr);
        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            List<Object> list = new ArrayList<>();
            jsonArray.forEach(element -> {
                if (element.isJsonObject()) {
                    Document d = new Document();
                    for (Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                        d.append(entry.getKey(), getRealValue(entry.getValue().toString(), type));   //TODO 数据类型检查
                    }
                    list.add(d);
                } else if (element.isJsonPrimitive()) {
                    list.add(getBsonValue(getRealValue(element.getAsString(), type)));
                } else {
                    LOGGER.warn("{}数据复杂，不支持多重嵌套", jsonStr);
                }
            });
            return list;
        }
        LOGGER.warn("{}不是json数组", jsonStr);
        return null;
    }

    /**
     * 获取真实值
     *
     * @param valueStr
     * @return
     */
    public static Object getRealValue(String valueStr, String... type) {
        if (type != null && type.length > 0) {
            switch (type[0]) {
                case "long":
                    return Long.parseLong(valueStr);
                case "float":
                    return Float.parseFloat(valueStr);
            }

        }

        if (StringUtil.isInteger(valueStr)) {
            try {
                return Integer.parseInt(valueStr);
            }catch (NumberFormatException e){
                //数据过长，用long解析
                return Long.parseLong(valueStr);
            }

        } else if (StringUtil.isDouble(valueStr)) {
            return Double.parseDouble(valueStr);
        } else if ("false".equalsIgnoreCase(valueStr) || "true".equalsIgnoreCase(valueStr)) {
            return Boolean.parseBoolean(valueStr);
        }
        return valueStr;
    }

    public static BsonValue getBsonValue(Object obj) {
        if (obj instanceof Integer) {
            return new BsonInt32((Integer) obj);
        }

        if (obj instanceof String) {
            return new BsonString((String) obj);
        }

        if (obj instanceof Long) {
            return new BsonInt64((Long) obj);
        }

        if (obj instanceof Date) {
            return new BsonDateTime(((Date) obj).getTime());
        }
        if (obj instanceof Double || obj instanceof Float) {
            return new BsonDouble((Double) obj);
        }
        return new BsonNull();

    }

}
