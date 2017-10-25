package com.jjy.game.manage.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.jjy.game.manage.constant.SessionKey;

/**
 * 登录状态检查，临时处理，以后调整
 *
 * @author JiangZhiYong
 * QQ:359135103
 */
public class UserInterceptor extends HandlerInterceptorAdapter {

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object obj, ModelAndView mav)
			throws Exception {
		String userName = (String) (request.getSession()).getAttribute(SessionKey.USER_INFO);
		if (userName == null) {
			mav.setViewName("redirect:/login");
		}
	}

	/*@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String url = request.getRequestURI();
		if(url.indexOf("login")>=0){
			return true;
		}
		Cookie[] cookies = request.getCookies();
		if(cookies!=null){
			for(Cookie cookie:cookies){
				if(cookie.getName().equals(SessionKey.USER_NAME)||cookie.getName().equals(SessionKey.HTTP_SESSION)){
					return true;
				}
			}
		}
		response.sendRedirect("/game/login");
		return false;
	}*/

	
}
