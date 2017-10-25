package com.jjy.game.manage.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.jzy.game.engine.server.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.jjy.game.manage.constant.Constant;
import com.jjy.game.manage.core.vo.ResultVO;
import com.jjy.game.manage.service.ServerService;
import com.jzy.game.engine.server.ServerInfo;
import com.jzy.game.engine.util.HttpUtil;
import com.jzy.game.engine.util.StringUtil;

/**
 * 服务器
 *
 * @author JiangZhiYong QQ:359135103
 */
@Controller
@RequestMapping("/server")
public class ServerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerController.class);

    @Autowired
    private ServerService serverService;

    @Value("${cluster.url}")
    private String clusterUrl;

    @Value("${server.auth}")
    private String serverAuth;

    /**
     * 服务器列表
     *
     * @param session
     * @param response
     * @return
     */
    @RequestMapping(value = "/list")
    public ModelAndView serverList(HttpSession session, HttpServletResponse response) {
        try {
            String url = clusterUrl + "/server/list";
            String urlGet = HttpUtil.URLGet(url);
            List<ServerInfo> servers = null;
            if (urlGet != null) {
                servers = JSON.parseArray(urlGet, ServerInfo.class);
            }

            return new ModelAndView("/server", "servers", servers);
        } catch (Exception e) {
            LOGGER.error("服务器", e);
            return new ModelAndView("redirect:/login");
        }
    }


    /**
     * 获取服务器线形图统计数据
     *
     * @return
     * @author JiangZhiYong
     * @QQ 359135103 2017年7月21日 下午6:03:48
     */
    @ResponseBody
    @RequestMapping(value = "/statistics")
    public ResultVO<List<String>> statistics(int id) {
        String url = clusterUrl + "/server/list";
        String urlGet = HttpUtil.URLGet(url);
        List<ServerInfo> servers = null;
        if (urlGet != null) {
            servers = JSON.parseArray(urlGet, ServerInfo.class);
        }
        if (servers == null || servers.size() < 1) {
            return new ResultVO<>(Constant.CODE_ERROR, "服务器数据为空", null);
        }
        //计数
        Map<Integer, Integer> countMap = new HashMap<>();
        List<String> serverNameList = new ArrayList<>();  //服务器名称
        List<Integer> serverOnlineList = new ArrayList<>(); //服务器在线人数
        List<Integer> serverFreeMemoryList = new ArrayList<>(); //服务器空闲内存
        List<Integer> serverTotalMemoryList = new ArrayList<>(); //服务器可获得内存
        List<Integer> serverMaxMemoryList = new ArrayList<>(); //服务器最大内存
        servers.forEach(server -> {
            if (!countMap.containsKey(server.getType())) {
                countMap.put(server.getType(), 1);
            } else {
                countMap.put(server.getType(), countMap.get(server.getType()) + 1);
            }
            serverNameList.add(server.getName());
            serverOnlineList.add(server.getOnline());
            serverFreeMemoryList.add(server.getFreeMemory());
            serverTotalMemoryList.add(server.getTotalMemory());
        });
        //样式
        List<String> serverTypeList = new ArrayList<>();
        List<Integer> serverCountList = new ArrayList<>();

        countMap.forEach((key, value) -> {
            serverTypeList.add(ServerType.valueof(key).toString());
            serverCountList.add(value);
        });
        List<String> result = new ArrayList<>();
        //服务器个数统计
        result.add(JSON.toJSONString(serverTypeList));
        result.add(JSON.toJSONString(serverCountList));
        //服务器人数
        result.add(JSON.toJSONString(serverNameList));
        result.add(JSON.toJSONString(serverOnlineList));
        result.add(JSON.toJSONString(serverFreeMemoryList));
        result.add(JSON.toJSONString(serverTotalMemoryList));
        result.add(JSON.toJSONString(serverMaxMemoryList));


//        LOGGER.info("服务器统计：{}", result);

        return new ResultVO<>(Constant.CODE_SUCCESS, urlGet, result);
    }

    /**
     * 加载脚本
     *
     * @return
     * @author JiangZhiYong
     * @QQ 359135103 2017年7月21日 下午6:03:48
     */
    @ResponseBody
    @RequestMapping(value = "/reloadScript")
    public ResultVO<String> reloadScript(HttpSession session, HttpServletResponse response, String ip, int port,
                                         String scriptPath) {
        StringBuilder url = new StringBuilder();
        url.append("http://").append(ip).append(":").append(port).append("/server/reloadScript?auth=")
                .append(serverAuth);
        if (!StringUtil.isNullOrEmpty(scriptPath)) {
            url.append("&scriptPath=").append(scriptPath);
        }
        LOGGER.info("加载脚本：{}", url);
        String urlGet = HttpUtil.URLGet(url.toString());
        LOGGER.info("加载返回：{}", urlGet);
        return new ResultVO<String>(Constant.CODE_SUCCESS, urlGet, urlGet);
    }

    /**
     * 关闭服务器
     *
     * @return
     * @author JiangZhiYong
     * @QQ 359135103 2017年7月21日 下午6:03:48
     */
    @ResponseBody
    @RequestMapping(value = "/exitServer")
    public ResultVO<String> exitServer(HttpSession session, HttpServletResponse response, String ip, int port) {
        StringBuilder url = new StringBuilder();
        url.append("http://").append(ip).append(":").append(port).append("/server/exit?auth=").append(serverAuth);
        LOGGER.info("关服URL：{}", url);
        String urlGet = HttpUtil.URLGet(url.toString());
        LOGGER.info("关服返回：{}", urlGet);
        return new ResultVO<String>(Constant.CODE_SUCCESS, urlGet, urlGet);
    }

    /**
     * 设置服务器状态
     *
     * @return
     * @author JiangZhiYong
     * @QQ 359135103 2017年7月21日 下午6:03:48
     */
    @ResponseBody
    @RequestMapping(value = "/state")
    public ResultVO<String> setServerState(HttpSession session, HttpServletResponse response, int id, int type,
                                           int state) {
        StringBuilder url = new StringBuilder();
        url.append(clusterUrl).append("/server/state?auth=").append(serverAuth).append("&serverType=").append(type)
                .append("&serverId=").append(id).append("&serverState=").append(state);
        LOGGER.info("状态URL：{}", url);
        String urlGet = HttpUtil.URLGet(url.toString());
        LOGGER.info("状态返回：{}", urlGet);
        return new ResultVO<String>(Constant.CODE_SUCCESS, urlGet, urlGet);
    }


    /**
     * 加载配置
     *
     * @return
     * @author JiangZhiYong
     * @QQ 359135103 2017年7月21日 下午6:03:48
     */
    @ResponseBody
    @RequestMapping(value = "/reloadConfig")
    public ResultVO<String> reloadConfig(String ip, int port, String tableName) {
        StringBuilder url = new StringBuilder();
        url.append("http://").append(ip).append(":").append(port).append("/server/reloadConfig?auth=")
                .append(serverAuth);
        if(!StringUtil.isNullOrEmpty(tableName)){
            url.append("&table=").append(tableName);
        }
        LOGGER.info("加载配置：{}", url);
        String urlGet = HttpUtil.URLGet(url.toString());
        LOGGER.info("加载返回：{}", urlGet);
        return new ResultVO<>(Constant.CODE_SUCCESS, urlGet, urlGet);
    }

    /**
     * 获取jvm信息
     * @param ip
     * @param port
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/jvm/info")
    public ResultVO<Map<String,String>> jvmInfo(String ip, int port) {
        StringBuilder url = new StringBuilder();
        url.append("http://").append(ip).append(":").append(port).append("/server/jvm/info?auth=")
                .append(serverAuth);
        LOGGER.info("jvm请求：{}", url);
        String urlGet = HttpUtil.URLGet(url.toString());
        LOGGER.info("jvm返回：{}", urlGet);
        return new ResultVO<>(Constant.CODE_SUCCESS, urlGet, serverService.getHTMLProPerites(urlGet));
    }

    /**
     * 获取线程信息
     * @param ip
     * @param port
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/thread/info")
    public ResultVO<Map<String,String>> threadInfo(String ip, int port) {
        StringBuilder url = new StringBuilder();
        url.append("http://").append(ip).append(":").append(port).append("/server/thread/info?auth=")
                .append(serverAuth);
        LOGGER.info("线程请求：{}", url);
        String urlGet = HttpUtil.URLGet(url.toString());
        LOGGER.info("线程返回：{}", urlGet);
        return new ResultVO<>(Constant.CODE_SUCCESS, urlGet, serverService.getHTMLProPerites(urlGet));
    }

}
