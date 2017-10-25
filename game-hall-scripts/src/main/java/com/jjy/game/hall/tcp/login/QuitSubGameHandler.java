/**工具生成，请遵循规范<br>
 @auther JiangZhiYong
 */
package com.jjy.game.hall.tcp.login;

import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jjy.game.message.Mid.MID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jjy.game.message.hall.HallLoginMessage.QuitSubGameRequest;


@HandlerEntity(mid=MID.QuitSubGameReq_VALUE,msg=QuitSubGameRequest.class)
public class QuitSubGameHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(QuitSubGameHandler.class);

	@Override
	public void run() {
		QuitSubGameRequest request = getMsg();
	}
}