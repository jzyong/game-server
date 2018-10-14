package com.jzy.game.engine.server;

/**
 * 服务器类型
 *
 * @author JiangZhiYong
 * @date 2017-03-30
 * QQ:359135103
 */
public enum ServerType {
    NONE(-1),
    /**网关*/
    GATE(1),
    GAME(2),
    /**集群管理服*/
    CLUSTER(3),
    LOG(4),
    /**聊天*/
    CHAT(5),
    PAY(6),
    /**大厅*/
    HALL(7),
	
    /**捕鱼达人*/
	GAME_BYDR(101),
	GAME_BYDR_WORLD(102),
	//备用
	GAME_3(103),
	GAME_4(104),
	GAME_5(105),
	GAME_6(106),
	GAME_7(107),
	GAME_8(108),
	
	
	;
	

    public static ServerType valueof(int type) {
        for (ServerType t : ServerType.values()) {
            if (t.getType() == type) {
                return t;
            }
        }
        return NONE;
    }

    private final int type;

    ServerType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
