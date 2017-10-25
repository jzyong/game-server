package com.jjy.game.model.handler.http.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.model.constant.Config;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.HttpHandler;
import com.jzy.game.engine.mail.MailConfig;
import com.jzy.game.engine.mail.MailManager;
import com.jzy.game.engine.util.MsgUtil;

/**
 * 关闭服务器
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月24日 下午1:49:30
 */
@HandlerEntity(path = "/server/exit")
public class ExitServerHandler extends HttpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExitServerHandler.class);

	@Override
	public void run() {
		String auth = getString("auth");
		if (!Config.SERVER_AUTH.equals(auth)) {
			sendMsg("验证失败");
			return;
		}
		String info = String.format("%s关闭服务器", MsgUtil.getIp(getSession()));
		LOGGER.info(info);
		sendMsg(info);
		MailConfig mailConfig = MailManager.getInstance().getMailConfig();
		String[] recives = mailConfig.getReciveUser().toArray(new String[mailConfig.getReciveUser().size()]);
		MailManager.getInstance().sendTextMail("服务器关闭", Config.SERVER_NAME + info, recives);
		System.exit(1);

	}

}
