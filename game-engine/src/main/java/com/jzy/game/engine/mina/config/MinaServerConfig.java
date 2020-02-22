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
 * @version $Id: $Id
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

    /**
     * <p>Getter for the field <code>port</code>.</p>
     *
     * @return a int.
     */
    public int getPort() {
        return port;
    }

    /**
     * <p>Setter for the field <code>port</code>.</p>
     *
     * @param port a int.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * <p>Getter for the field <code>url</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getUrl() {
        return url;
    }

    /**
     * <p>Setter for the field <code>url</code>.</p>
     *
     * @param url a {@link java.lang.String} object.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * <p>Getter for the field <code>orderedThreadPoolExecutorSize</code>.</p>
     *
     * @return a int.
     */
    public int getOrderedThreadPoolExecutorSize() {
        return orderedThreadPoolExecutorSize;
    }

    /**
     * <p>Setter for the field <code>orderedThreadPoolExecutorSize</code>.</p>
     *
     * @param orderedThreadPoolExecutorSize a int.
     */
    public void setOrderedThreadPoolExecutorSize(int orderedThreadPoolExecutorSize) {
        this.orderedThreadPoolExecutorSize = orderedThreadPoolExecutorSize;
    }

    /**
     * <p>isReuseAddress.</p>
     *
     * @return a boolean.
     */
    public boolean isReuseAddress() {
        return reuseAddress;
    }

    /**
     * <p>Setter for the field <code>reuseAddress</code>.</p>
     *
     * @param reuseAddress a boolean.
     */
    public void setReuseAddress(boolean reuseAddress) {
        this.reuseAddress = reuseAddress;
    }

    /**
     * <p>isTcpNoDelay.</p>
     *
     * @return a boolean.
     */
    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    /**
     * <p>Setter for the field <code>tcpNoDelay</code>.</p>
     *
     * @param tcpNoDelay a boolean.
     */
    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    /**
     * <p>Getter for the field <code>readerIdleTime</code>.</p>
     *
     * @return a int.
     */
    public int getReaderIdleTime() {
        return readerIdleTime;
    }

    /**
     * <p>Setter for the field <code>readerIdleTime</code>.</p>
     *
     * @param readerIdleTime a int.
     */
    public void setReaderIdleTime(int readerIdleTime) {
        this.readerIdleTime = readerIdleTime;
    }

    /**
     * <p>Getter for the field <code>writerIdleTime</code>.</p>
     *
     * @return a int.
     */
    public int getWriterIdleTime() {
        return writerIdleTime;
    }

    /**
     * <p>Setter for the field <code>writerIdleTime</code>.</p>
     *
     * @param writerIdleTime a int.
     */
    public void setWriterIdleTime(int writerIdleTime) {
        this.writerIdleTime = writerIdleTime;
    }

    /**
     * <p>Getter for the field <code>soLinger</code>.</p>
     *
     * @return a int.
     */
    public int getSoLinger() {
        return soLinger;
    }

    /**
     * <p>Setter for the field <code>soLinger</code>.</p>
     *
     * @param soLinger a int.
     */
    public void setSoLinger(int soLinger) {
        this.soLinger = soLinger;
    }

    /**
     * <p>Getter for the field <code>ip</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getIp() {
        return ip;
    }

    /**
     * <p>Setter for the field <code>ip</code>.</p>
     *
     * @param ip a {@link java.lang.String} object.
     */
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    /**
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link com.jzy.game.engine.server.ServerType} object.
     */
    public ServerType getType() {
        return type;
    }

    /**
     * <p>Setter for the field <code>type</code>.</p>
     *
     * @param type a {@link com.jzy.game.engine.server.ServerType} object.
     */
    public void setType(ServerType type) {
        this.type = type;
    }
    
    
    /**
     * <p>Getter for the field <code>httpPort</code>.</p>
     *
     * @return a int.
     */
    public int getHttpPort() {
        return httpPort;
    }

    /**
     * <p>Setter for the field <code>httpPort</code>.</p>
     *
     * @param httpPort a int.
     */
    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    /**
     * <p>Getter for the field <code>netSpeed</code>.</p>
     *
     * @return a int.
     */
    public int getNetSpeed() {
        return netSpeed;
    }

    /**
     * <p>Setter for the field <code>netSpeed</code>.</p>
     *
     * @param netSpeed a int.
     */
    public void setNetSpeed(int netSpeed) {
        this.netSpeed = netSpeed<128?128:netSpeed;
    }

    /**
     * <p>Getter for the field <code>maxCountPerSecond</code>.</p>
     *
     * @return a int.
     */
    public int getMaxCountPerSecond() {
        return maxCountPerSecond;
    }

    /**
     * <p>Setter for the field <code>maxCountPerSecond</code>.</p>
     *
     * @param maxCountPerSecond a int.
     */
    public void setMaxCountPerSecond(int maxCountPerSecond) {
        this.maxCountPerSecond = maxCountPerSecond<10?10:maxCountPerSecond;
    }
}
