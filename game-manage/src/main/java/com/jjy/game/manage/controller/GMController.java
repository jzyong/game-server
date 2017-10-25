package com.jjy.game.manage.controller;

import com.jjy.game.manage.constant.Constant;
import com.jjy.game.manage.core.vo.ResultVO;
import com.jzy.game.engine.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

/**
 * GM 管理
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年8月4日 下午5:05:36
 */
@RequestMapping("/gm")
@Controller
public class GMController {
	private static final Logger LOGGER= LoggerFactory.getLogger(GMController.class);

	@Value("${cluster.url}")
	private String clusterUrl;

	/**gm调用地址*/
	private String gmUrl;

	@Value("${server.auth}")
	private String serverAuth;

	/**
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年8月4日 下午5:07:25
	 * @return
	 */
	@RequestMapping("/list")
	public ModelAndView gmList(){
		//http://127.0.0.1:8001/server/hall/ip
		String url = clusterUrl + "/server/hall/ip";
		String urlGet = HttpUtil.URLGet(url);
		gmUrl="http://"+urlGet+"/gm?";
		return new ModelAndView("gm","gmUrl",gmUrl);
	}

	/**
	 *
	 * @param queryString
	 * @return
	 */
	@RequestMapping("/execute")
	@ResponseBody
	public ResultVO<String> executeGm(String queryString){
		String url=gmUrl+"auth="+serverAuth+queryString;
		String urlGet = HttpUtil.URLGet(url);
		LOGGER.info("gm：{}",url);
		return new ResultVO<>(Constant.CODE_SUCCESS, urlGet,urlGet);
	}
}
