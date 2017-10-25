/**工具生成，请遵循规范<br>
 @auther JiangZhiYong
 */
package com.jjy.game.hall.tcp.guild;

import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;
import com.jjy.game.message.Mid.MID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jjy.game.message.hall.HallGuildMessage.GuildListRequest;


@HandlerEntity(mid=MID.GuildListReq_VALUE,msg=GuildListRequest.class)
public class GuildListHandler extends TcpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(GuildListHandler.class);

	@Override
	public void run() {
		GuildListRequest request = getMsg();
	}
}