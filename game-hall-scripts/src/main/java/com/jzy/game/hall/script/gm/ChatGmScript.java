package com.jzy.game.hall.script.gm;

import org.apache.mina.http.HttpRequestImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.engine.handler.IHandler;
import com.jzy.game.engine.mina.config.MinaClientConfig;
import com.jzy.game.engine.mina.config.MinaServerConfig;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.hall.http.gm.GmHandler;
import com.jzy.game.hall.script.IGmScript;
import com.jzy.game.hall.server.HallServer;

/**
 * 执行聊天GM命令
  <br>
  <table>
	  <tr><th>GM</th><th>命令示例</th><th>http请求路径</th><th>备注</th></tr>
	  <tbody>
		  <tr><td>添加金币</td><td>&addGold 500</td><td>http://192.168.0.17:9102/gm?cmd=addGold&roleId=1&gold=10000&auth=123456</td><td>正数添加，负数减少</td></tr>
		  <tr><td>设置等级</td><td>&setLevel 5</td><td>http://192.168.0.17:9102/gm?cmd=setLevel&roleId=1&level=3&auth=123456</td><td></td></tr>
		  <tr><td>踢玩家下线</td><td>&offLineRole</td><td>http://192.168.0.17:9102/gm?cmd=offLineRole&roleId=1&auth=123456</td><td></td></tr>
	  </tbody>
  </table>
 
  @author JiangZhiYong
  @QQ 359135103 2017年10月16日 下午6:09:48
 */
public class ChatGmScript implements IGmScript {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatGmScript.class);

	@Override
	public String executeGm(long roleId, String gmCmd) {
		String cmd = gmCmd.replace("&", "");
		String[] split = cmd.split(" ");
		String queryString = null;

		// 调用http逻辑，需要组装参数
		switch (split[0]) {
		case "addGold": 	// 添加金币
			queryString = "cmd=" + split[0] + "&roleId=" + roleId + "&gold=" + split[1];
			break;
		case "setLevel":	// 设置等级
			queryString="cmd=" + split[0] + "&roleId=" + roleId + "&level=" + split[1];
			break;
		case "offLineRole":	// 设置等级
			queryString="cmd=" + split[0] + "&roleId=" + roleId;
			break;	

		default:
			break;
		}

		if (queryString != null) {
			Class<? extends IHandler> handlerClass = ScriptManager.getInstance().getHttpHandler("/gm");
			try {
				GmHandler handler = (GmHandler) handlerClass.newInstance();
				handler.setMessage(new HttpRequestImpl(null, null, null, queryString, null));
				return handler.execute();
			} catch (Exception e) {
				LOGGER.error("聊天GM", e);
			}
			return String.format("命令%s执行失败，服务器内存错误", gmCmd);
		} else {
			return String.format("命令%s执行失败，请检查格式", gmCmd);
		}

	}

	@Override
	public boolean isGMCmd(String gmCmd) {
		// 服务器地址验证
		MinaServerConfig minaServerConfig = HallServer.getInstance().getHallHttpServer().getMinaServerConfig();
		return IGmScript.super.isGMCmd(gmCmd)&&(minaServerConfig.getIp().startsWith("192")||minaServerConfig.getIp().startsWith("127"));
	}

}
