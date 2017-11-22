package com.jzy.game.engine.mina.config;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.jzy.game.engine.server.BaseServerConfig;
import com.jzy.game.engine.server.ServerType;

/**
 * 客户端配置
 *
 * @author JiangZhiYong
 * @date 2017-03-31
 * QQ:359135103
 */
@Root
public class MinaClientConfig extends BaseServerConfig {
    // 客户端线程池大小
    @Element(required = false)
    private int orderedThreadPoolExecutorSize = 150;
    
    @Element(required = false)
    private int soLinger = 0;
    
    /***
     * 客户端创建的最大连接数
     */
    @Element(required = false)
    private int maxConnectCount = 1;
    
    //连接配置
    @Element(required = false)
    private MinaClienConnToConfig connTo;
    
    // 当前服务器的类型,如当前服务器是gameserver.那么对应ServerType.GameServer = 10
    @Element(required = false)
    private ServerType type = ServerType.GATE;
    
    // 其他配置,如配置服务器允许开启的地图
    @Element(required = false)
    private String info;
    
    public int getOrderedThreadPoolExecutorSize() {
        return orderedThreadPoolExecutorSize;
    }

    public void setOrderedThreadPoolExecutorSize(int orderedThreadPoolExecutorSize) {
        this.orderedThreadPoolExecutorSize = orderedThreadPoolExecutorSize;
    }

    public int getSoLinger() {
        return soLinger;
    }

    public void setSoLinger(int soLinger) {
        this.soLinger = soLinger;
    }

    public ServerType getType() {
        return type;
    }

    public void setType(ServerType type) {
        this.type = type;
    }
    
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public MinaClienConnToConfig getConnTo() {
        return connTo;
    }

    public void setConnTo(MinaClienConnToConfig connTo) {
        this.connTo = connTo;
    }

    public int getMaxConnectCount() {
        return maxConnectCount;
    }

    public void setMaxConnectCount(int maxConnectCount) {
        this.maxConnectCount = maxConnectCount;
    }
    
    /**
     * 连接配置
     *
     * @author JiangZhiYong
     * @date 2017-03-31
     * QQ:359135103
     */
    @Root
    public static class MinaClienConnToConfig extends BaseServerConfig {
        @Element(required = true)
        private ServerType type=ServerType.GATE;
        
        // 链接到服务器的地址
        @Element(required = true)
        private String host="127.0.0.1";
        
        // 链接到服务器的端口
        @Element(required = true)
        private int port=8500;

        public ServerType getType() {
            return type;
        }

        @Override
        public String toString() {
            return new StringBuilder("目标类型：").append(type).append("连接ip：").append(host).append(":").append(port).toString();
        }

        public void setType(ServerType type) {
            this.type = type;
        }
        
        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 47 * hash + this.type.ordinal();
            hash = 47 * hash + this.id;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final MinaClienConnToConfig other = (MinaClienConnToConfig) obj;
            if (this.type != other.type) {
                return false;
            }
            if (this.id != other.id) {
                return false;
            }
            return true;
        }

    }
}

