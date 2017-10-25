package com.jjy.game.model.handler.http.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.model.constant.Config;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.HttpHandler;
import com.jzy.game.engine.util.SysUtil;

/**
 * 获取jvm信息
 * <p>http://192.168.0.17:9002/server/jvm/info?auth=daa0cf5b-e72d-422c-b278-450b28a702c6</p>
 * @author JiangZhiYong
 * @QQ 359135103 2017年10月12日 下午2:38:44
 */
@HandlerEntity(path = "/server/jvm/info")
public class JvmInfoHandler extends HttpHandler {
	private static final Logger LOGGER=LoggerFactory.getLogger(JvmInfoHandler.class);

	@Override
	public void run() {
		String auth = getString("auth");
		if (!Config.SERVER_AUTH.equals(auth)) {
			sendMsg("验证失败");
			return;
		}
		String info = SysUtil.jvmInfo("<br>");
//		LOGGER.debug(info);
		LOGGER.info("请求完成");
		sendMsg(info);
	}

}
