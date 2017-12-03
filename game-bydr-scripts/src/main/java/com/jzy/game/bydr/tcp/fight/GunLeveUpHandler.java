/**工具生成，请遵循规范<br>
 @auther JiangZhiYong
 */
package com.jzy.game.bydr.tcp.fight;

import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jzy.game.message.Mid.MID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.message.bydr.BydrFightMessage.GunLeveUpRequest;


@HandlerEntity(mid=MID.GunLeveUpReq_VALUE,msg=GunLeveUpRequest.class)
public class GunLeveUpHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(GunLeveUpHandler.class);

	@Override
	public void run() {
		GunLeveUpRequest request = getMsg();
	}
}