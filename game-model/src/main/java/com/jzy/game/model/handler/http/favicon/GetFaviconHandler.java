package com.jzy.game.model.handler.http.favicon;

import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.HttpHandler;

/**
 * 获取 favicon.ico 图标	
 * <p>谷歌浏览器地址栏请求会额外发送次消息，IE不发送</p>
 * <p>
 * http://127.0.0.1:9002/favicon.ico
 * </p>
 * 
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年10月13日 上午10:07:42
 */
@HandlerEntity(path="/favicon.ico")
public class GetFaviconHandler extends HttpHandler {

	@Override
	public void run() {
		sendMsg("favicon.ico");
//		System.err.println("获取图标");
	}

}
