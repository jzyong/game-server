package com.jjy.game.manage.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.jjy.game.manage.constant.SessionKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.jjy.game.manage.service.UserService;
import com.jzy.game.engine.util.StringUtil;

/**
 * 用户
 *
 * @author JiangZhiYong
 * @date 2017-05-23 QQ:359135103
 */
@Controller
@RequestMapping("/user")
public class UserController {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private UserService userService;

	@Value("${userName}")
	private String userName;

	@Value("${password}")
	private String password;

	/**
	 * 登录
	 * 
	 * @param session
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/login")
	public ModelAndView login(HttpSession session, HttpServletResponse response, String userName, String password) {

		if (StringUtil.isNullOrEmpty(userName) || StringUtil.isNullOrEmpty(password)) {
			return new ModelAndView("redirect:/login");
		}

		if (userName.equalsIgnoreCase(this.userName) && password.equals(this.password)) {
			session.setAttribute(SessionKey.USER_INFO,userName);
			userService.saveCookie(session, userName, response);
			return new ModelAndView("redirect:/home");
		}

		return new ModelAndView("redirect:/login");
	}

}
