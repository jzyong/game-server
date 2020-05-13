/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jzy.game.tool.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

/**
 * 配置
 *
 * @author JiangZhiYong
 * @QQ 359135103
 */
public class Config {

    @ElementList(required = false)
    private HashSet<DBConfig> dbs = new HashSet<>();
    @Element(required = false)
    private String excelDir = "./"; //Excel目录

    public HashSet<DBConfig> getDbs() {
        return dbs;
    }

    public void setDbs(HashSet<DBConfig> dbs) {
        this.dbs = dbs;
    }


    public String getExcelDir() {
        return excelDir;
    }

    public void setExcelDir(String excelDir) {
        this.excelDir = excelDir;
    }
    
    
    

    /**
     * 数据库配置
     */
    public static class DBConfig {

        @Element(required = true)
        private String name;

        @Element(required = true)
        private String url;
        
        @Element(required = false)
        private String dbName = "lztb_att"; //数据库名称
        
//        @Element(required = false)
//        private String loadConfigUrl;   //更新服务器配置文件请求地址
        //更新服务器配置文件请求地址
        @ElementList(required = false)
        private List<String> loadConfigUrls =new ArrayList<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }

//        public String getLoadConfigUrl() {
//            return loadConfigUrl;
//        }
//
//        public void setLoadConfigUrl(String loadConfigUrl) {
//            this.loadConfigUrl = loadConfigUrl;
//        }


        public List<String> getLoadConfigUrls() {
            return loadConfigUrls;
        }

        public void setLoadConfigUrls(List<String> loadConfigUrls) {
            this.loadConfigUrls = loadConfigUrls;
        }
    }
}
