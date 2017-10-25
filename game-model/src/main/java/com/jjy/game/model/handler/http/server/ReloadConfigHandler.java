package com.jjy.game.model.handler.http.server;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jjy.game.model.constant.Config;
import com.jjy.game.model.script.IConfigScript;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.HttpHandler;
import com.jzy.game.engine.mail.MailConfig;
import com.jzy.game.engine.mail.MailManager;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.engine.util.MsgUtil;
import com.jzy.game.engine.util.SymbolUtil;

/**
 * 加载配置
 * <p>
 * http://192.168.0.17:9002/server/reloadConfig?table=c_fish,c_room&auth=daa0cf5b-e72d-422c-b278-450b28a702c6
 * </p>
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年10月12日 下午2:43:20
 */
@HandlerEntity(path = "/server/reloadConfig")
public class ReloadConfigHandler extends HttpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReloadConfigHandler.class);

	@Override
	public void run() {
		String auth = getString("auth");
		if (!Config.SERVER_AUTH.equals(auth)) {
			sendMsg("验证失败");
			return;
		}
		String tableStr = getString("table");
		String result = "";
		if (tableStr != null) {
			result = ScriptManager.getInstance().getBaseScriptEntry().functionScripts(IConfigScript.class,
					(IConfigScript script) -> script
							.reloadConfig(Arrays.asList(tableStr.split(SymbolUtil.DOUHAO_REG))));
		} else {
			result = ScriptManager.getInstance().getBaseScriptEntry().functionScripts(IConfigScript.class,
					(IConfigScript script) -> script.reloadConfig(null));
		}

		String info = String.format("%s加载配置：%s", MsgUtil.getIp(getSession()), result);
		LOGGER.info(info);
		MailConfig mailConfig = MailManager.getInstance().getMailConfig();
		String[] recives = mailConfig.getReciveUser().toArray(new String[mailConfig.getReciveUser().size()]);
		MailManager.getInstance().sendTextMail("加载配置", Config.SERVER_NAME + "\r\n" + info, recives);
		sendMsg(info);
	}

}
