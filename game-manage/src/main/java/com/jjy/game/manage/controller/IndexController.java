package com.jjy.game.manage.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.jjy.game.manage.constant.SessionKey;

/**
 * 首页管理
 *
 * @author JiangZhiYong
 * @date 2017-05-23 QQ:359135103
 */
@Controller
public class IndexController {
    @RequestMapping("/")
    public String index(HttpSession session, HttpServletRequest request) {
        request.getCookies();
        return "/login";
    }

    @RequestMapping("/test")
    public String test() {
        System.out.println("test");
        return "/test";
    }

    @RequestMapping("/login")
    public String signin() {
        return "/login";
    }

    @RequestMapping("/register")
    public String register() {
        return "/register";
    }

    @RequestMapping("/home")
    public ModelAndView home(HttpSession session) {
        ModelAndView view = new ModelAndView("/home");
        String userName = (String) session.getAttribute(SessionKey.USER_NAME);
        if (userName != null) {
            view.addObject("userName", userName);
        }
        return view;
    }

    /**
     * 空内容
     * @return
     */
    @RequestMapping("/empty")
    public String empty() {
        return "/empty";
    }
}
