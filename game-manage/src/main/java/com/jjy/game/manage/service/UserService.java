package com.jjy.game.manage.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.jjy.game.manage.constant.SessionKey;

/**
 * 用户
 *
 * @author JiangZhiYong QQ:359135103
 */
@Service
public class UserService {

	/**
	 * 存储 cookie
	 * 
	 * @param session
	 * @param muser
	 * @param response
	 */
	public void saveCookie(HttpSession session, String userName, HttpServletResponse response) {
		Cookie sessionCookie = new Cookie(SessionKey.HTTP_SESSION, session.getId());
		sessionCookie.setMaxAge(3600);
		sessionCookie.setPath("/");
		response.addCookie(sessionCookie);
		
//		session.setAttribute(SessionKey.USER_NAME, userName);
		Cookie loginidCookie = new Cookie(SessionKey.USER_NAME, userName);
		// 30*24*60*60
		loginidCookie.setMaxAge(2592000);
		loginidCookie.setPath("/");
		response.addCookie(loginidCookie);
	}

}
