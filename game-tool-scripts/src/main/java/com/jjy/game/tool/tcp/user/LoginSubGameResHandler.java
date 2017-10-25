package com.jjy.game.tool.tcp.user;

import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.bydr.BydrRoomMessage;
import com.jjy.game.message.hall.HallLoginMessage.LoginSubGameResponse;
import com.jjy.game.tool.client.Player;
import static com.jjy.game.tool.client.PressureServiceThread.SEND_TIME;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;

/**
 * 登录子游戏返回
 *
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月11日 上午11:13:57
 */
@HandlerEntity(mid = MID.LoginSubGameRes_VALUE, msg = LoginSubGameResponse.class)
public class LoginSubGameResHandler extends TcpHandler {

    @Override
    public void run() {
        LoginSubGameResponse res = getMsg();
        System.err.println(res.toString());
        
        Player player = (Player) session.getAttribute(Player.PLAYER);
        player.showLog(String.format("%s 登录捕鱼成功/n", player.getUserName()));
        //进入房间消息
        BydrRoomMessage.EnterRoomRequest.Builder builder2 = BydrRoomMessage.EnterRoomRequest.newBuilder();
        builder2.setType(BydrRoomMessage.RoomType.CLASSICS);
        builder2.setRank(30);
        player.getTcpSession().setAttribute(SEND_TIME, System.currentTimeMillis());
        player.sendTcpMsg(builder2.build());

//		ApplyAthleticsRequest.Builder builder=ApplyAthleticsRequest.newBuilder();
//		builder.setRank(100);
//		builder.setType(0);
//		session.write(builder.build());
    }

}
