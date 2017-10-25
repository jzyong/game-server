package com.jjy.game.tool.tcp.bydr.fight;

import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.bydr.BydrFightMessage.FireResultResponse;
import com.jjy.game.tool.client.Player;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;

/**
 * 开炮结果
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年10月20日 上午11:27:32
 */
@HandlerEntity(mid = MID.FireResultRes_VALUE, msg = FireResultResponse.class)
public class FireResultResHandler extends TcpHandler {

	@Override
	public void run() {
		FireResultResponse res = getMsg();
		Player player = (Player) session.getAttribute(Player.PLAYER);
		if (res.getDieFishIdCount() > 0) {
//			player.showLog(String.format("%s 打死鱼%d，金币%d", player.getUserName(), res.getDieFishId(0), res.getGold()));
		}
	}

}
