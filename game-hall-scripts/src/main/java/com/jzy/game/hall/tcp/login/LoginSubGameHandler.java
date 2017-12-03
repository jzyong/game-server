/**工具生成，请遵循规范<br>
 @auther JiangZhiYong
 */
package com.jzy.game.hall.tcp.login;

import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.message.Mid.MID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.message.hall.HallLoginMessage.LoginSubGameRequest;


@HandlerEntity(mid=MID.LoginSubGameReq_VALUE,msg=LoginSubGameRequest.class)
public class LoginSubGameHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginSubGameHandler.class);

	@Override
	public void run() {
		LoginSubGameRequest request = getMsg();
	}
}