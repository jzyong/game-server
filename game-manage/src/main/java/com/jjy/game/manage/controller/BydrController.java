package com.jjy.game.manage.controller;

import com.jzy.game.engine.util.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 捕鱼达人
 * @author JiangZhiYong
 * @QQ 359135103
 * @Date 2017/9/20 0020
 */
@Controller
@RequestMapping("/bydr")
public class BydrController {

    @Value("${cluster.url}")
    private String clusterUrl;

    @Value("${gate.websocket.port}")
    private int gateWebSocketPort;

    /**
     * 客户端页面
     * @return
     */
    @RequestMapping(value = "/client")
    public ModelAndView client(){
        //http://127.0.0.1:8001/server/gate/ip
        String url = clusterUrl + "/server/gate/ip";
        String urlGet = HttpUtil.URLGet(url);
        String [] strs= urlGet.split(":");
        //ws://127.0.0.1:8005/
        String wsUrl="ws://"+strs[0]+":"+gateWebSocketPort;
        //TODO 获取网关连接地址
        return new ModelAndView("bydr-client","wsUrl",wsUrl);
    }
}
