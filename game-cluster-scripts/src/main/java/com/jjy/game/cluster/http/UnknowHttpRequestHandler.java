package com.jjy.game.cluster.http;

import org.apache.mina.http.api.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.HttpHandler;

/**
 * 未知请求 404
 *
 * @author JiangZhiYong
 * @date 2017-03-31 QQ:359135103
 * http://127.0.0.1:8680/
 */
@HandlerEntity(path = "")
public class UnknowHttpRequestHandler extends HttpHandler {
	private static final Logger LOGGER=LoggerFactory.getLogger(UnknowHttpRequestHandler.class);

	@Override
	public void run() {
		LOGGER.info("{}请求页面错误",getSession().getRemoteAddress().toString());
		getParameter().setStatus(HttpStatus.CLIENT_ERROR_NOT_FOUND);
		responseWithStatus();
		
	}

}
