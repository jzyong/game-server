package com.jzy.game.engine.mina.config;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.jzy.game.engine.server.BaseServerConfig;
import com.jzy.game.engine.server.ServerType;

/**
 * 主要配置Mina服务器相关配置
 *
 * @author JiangZhiYong
 * @date 2017-03-30
 * QQ:359135103
 */
@Root
public class MinaServerConfig extends BaseServerConfig {

    // mina地址
    @Element(required = false)
    private String ip;

    // mina端口
    @Element(required = false)
    private int port = 8500;


    // http服务器地址
    @Element(required = false)
    private String url;

    // mina服务器线程池大小
    @Element(required = false)
    private int orderedThreadPoolExecutorSize = 300;    

    // 是否重用地址
    @Element(required = false)
    private boolean reuseAddress = true;

    // Tcp没有延迟
    @Element(required = false)
    private boolean tcpNoDelay = true;

    // 读取空闲时间检测
    @Element(required = false)
    private int readerIdleTime = 120;

    // 写入空闲时间检测
    @Element(required = false)
    private int writerIdleTime = 120;

    @Element(required = false)
    private int soLinger;

    // 服务器类型
    @Element(required = false)
    private ServerType type=ServerType.GATE;
    
    //http服务器端口
    @Element(required=false)
    private int httpPort;
    
    //网络带宽：负载均衡时做判断依据。以1M支撑64人并发计算
    @Element(required=false)
    private int netSpeed=64*5;
    
    //限制每秒会话可接受的消息数，超过则关闭
    @Element(required=false)
    private int maxCountPerSecond=30;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getOrderedThreadPoolExecutorSize() {
        return orderedThreadPoolExecutorSize;
    }

    public void setOrderedThreadPoolExecutorSize(int orderedThreadPoolExecutorSize) {
        this.orderedThreadPoolExecutorSize = orderedThreadPoolExecutorSize;
    }

    public boolean isReuseAddress() {
        return reuseAddress;
    }

    public void setReuseAddress(boolean reuseAddress) {
        this.reuseAddress = reuseAddress;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public int getReaderIdleTime() {
        return readerIdleTime;
    }

    public void setReaderIdleTime(int readerIdleTime) {
        this.readerIdleTime = readerIdleTime;
    }

    public int getWriterIdleTime() {
        return writerIdleTime;
    }

    public void setWriterIdleTime(int writerIdleTime) {
        this.writerIdleTime = writerIdleTime;
    }

    public int getSoLinger() {
        return soLinger;
    }

    public void setSoLinger(int soLinger) {
        this.soLinger = soLinger;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public ServerType getType() {
        return type;
    }

    public void setType(ServerType type) {
        this.type = type;
    }
    
    
    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public int getNetSpeed() {
        return netSpeed;
    }

    public void setNetSpeed(int netSpeed) {
        this.netSpeed = netSpeed<128?128:netSpeed;
    }

    public int getMaxCountPerSecond() {
        return maxCountPerSecond;
    }

    public void setMaxCountPerSecond(int maxCountPerSecond) {
        this.maxCountPerSecond = maxCountPerSecond<10?10:maxCountPerSecond;
    }
}
