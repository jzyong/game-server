/**工具生成，请遵循规范<br>
 @auther JiangZhiYong
 */
package com.jzy.game.hall.tcp.guild;

import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.message.Mid.MID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.message.hall.HallGuildMessage.ApplyGuildRequest;

@HandlerEntity(mid = MID.ApplyGuildReq_VALUE, msg = ApplyGuildRequest.class)
public class ApplyGuildHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplyGuildHandler.class);

	@Override
	public void run() {
		ApplyGuildRequest request = getMsg();
	}
}