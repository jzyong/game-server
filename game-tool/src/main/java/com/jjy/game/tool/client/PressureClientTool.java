package com.jjy.game.tool.client;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.tool.client.ssl.ClientSslContextFactory;
import com.jzy.game.engine.math.MathUtil;
import com.jzy.game.engine.mina.MinaUdpClient;
import com.jzy.game.engine.mina.code.ClientProtocolCodecFactory;
import com.jzy.game.engine.mina.config.MinaClientConfig;
import com.jzy.game.engine.mina.config.MinaClientConfig.MinaClienConnToConfig;
import com.jzy.game.engine.mina.service.SingleMinaTcpClientService;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.engine.util.HttpUtil;

import javax.swing.JTextArea;

/**
 * 压力测试客户端
 *
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月10日 下午4:48:24
 */
public class PressureClientTool {

    private static final Logger LOGGER = LoggerFactory.getLogger(PressureClientTool.class);

    public static Map<String, Player> players = new ConcurrentHashMap<>();
    public static String configPath;

    /**
     * 压力测试客户端个数
     */
    private int clientNum = 1;

    private String userNamePrefix = "jzy"; // 用户名前缀
    private static AtomicInteger userNameNo = new AtomicInteger(1); // 用户名编号
    private String clusterIp = "192.168.0.17";    //集群IP

    public PressureClientTool(int clientNum, String userNamePrefix, String password, String clusterIp,JTextArea logTextArea) {
        this.clientNum = clientNum;
        this.clusterIp = clusterIp;
        this.userNamePrefix = userNamePrefix;
        initConfigPath();
        ScriptManager.getInstance().init(null);

        //循环初始化客户端
        try {
            for (int i = 0; i < clientNum; i++) {
                PressureClientHandler pressureClientHandler = new PressureClientHandler();
                MinaClientConfig minaClientConfig = getMinaClientConfig();
                String userName = userNamePrefix + userNameNo.incrementAndGet();

                // TCP
                // 添加ssl
                Map<String, IoFilter> filters = new HashMap<>();
                SslFilter sslFilter = new SslFilter(ClientSslContextFactory.getInstance(false));
                sslFilter.setUseClientMode(true);
//		filters.put("ssl", sslFilter);
                SingleMinaTcpClientService service = new SingleMinaTcpClientService(minaClientConfig,
                        new ClientProtocolCodecFactory(), pressureClientHandler, filters);
                pressureClientHandler.setService(service);
                new Thread(service).start();

                // UDP
                MinaClientConfig minaClientConfig2 = new MinaClientConfig();
                MinaClienConnToConfig connTo = new MinaClienConnToConfig();
                connTo.setHost(minaClientConfig.getConnTo().getHost());
                connTo.setPort(8004);
                minaClientConfig2.setConnTo(connTo);
                MinaUdpClient udpClient = new MinaUdpClient(minaClientConfig2, pressureClientHandler,
                        new ClientProtocolCodecFactory());
                new Thread(udpClient).start();

                while (udpClient.getSession() == null) {
                    Thread.sleep(MathUtil.random(500, 3000));
                }
                Player player = new Player();
                player.setUserName(userName);
                player.setPassword(password);
                player.setUdpSession(udpClient.getSession());
                player.setTcpSession(service.getMostIdleIoSession());
                player.setLogTextArea(logTextArea);
                if(player.getTcpSession()==null||player.getUdpSession()==null){
                    LOGGER.warn("用户{}连接服务器失败",userName);
                    logTextArea.append(String.format("用户%s连接服务器失败\n",userName));
                    continue;
                }
                player.loginInit();
                players.put(userName, player);

                new PressureServiceThread(player).start();

            }
        } catch (Exception e) {
            LOGGER.error("PressureClientTool", e);
        }

    }

    public static void main(String[] args) {
        initConfigPath();
        ScriptManager.getInstance().init(null);
        PressureClientTool pressureClientTool = new PressureClientTool(1, "jzy", "111", "192.168.0.17",null);
    }

    /**
     * 获取客户端Tcp连接配置
     *
     * @return
     */
    public MinaClientConfig getMinaClientConfig() {
        MinaClientConfig minaClientConfig = new MinaClientConfig();
        try {
            minaClientConfig.setOrderedThreadPoolExecutorSize(1);
            MinaClienConnToConfig connTo = new MinaClienConnToConfig();
            String url="http://"+this.clusterIp+":8001/server/gate/ip";
            String get = HttpUtil.URLGet(url);
            LOGGER.info("hall host:{}", get);
            if (get.contains(":")) {
                String[] split = get.split(":");
                connTo.setHost(split[0]);
                connTo.setPort(Integer.parseInt(split[1]));
            } else {
                LOGGER.warn("大厅IP获取失败");
            }
            minaClientConfig.setConnTo(connTo);
        } catch (Exception e) {
            LOGGER.warn("获取大厅端口", e);
        }

        return minaClientConfig;
    }

    private static void initConfigPath() {
        File file = new File(System.getProperty("user.dir"));
        if ("target".equals(file.getName())) {
            configPath = file.getPath() + File.separatorChar + "config";
        } else {
            configPath = file.getPath() + File.separatorChar + "target" + File.separatorChar + "config";
        }
        LOGGER.info("配置路径为：" + configPath);
    }

}
