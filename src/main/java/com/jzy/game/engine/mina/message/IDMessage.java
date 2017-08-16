package com.jzy.game.engine.mina.message;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.jzy.game.engine.util.MsgUtil;

/**
 * 
 * 带用户ID的消息
 * @author JiangZhiYong
 * @date 2017-03-31
 * QQ:359135103
 */
public final class IDMessage implements Runnable{

    private final Object msg;
    private final long userID;	//用户ID或角色ID，当角色ID不存在时，用用户ID
    private final IoSession session;

    /**
     * 
     * @param session
     * @param msg
     * @param userID 
     */
    public IDMessage(IoSession session, Object msg, long userID) {
        this.msg = msg;
        this.userID = userID;
        this.session=session;
    }

    public long getUserID() {
        return userID;
    }

    public IoSession getSession() {
        return session;
    }

    @Override
    public void run() {
        if (session!=null&&session.isConnected()) {
            IoBuffer buf=MsgUtil.toIobuffer(this);
            session.write(buf);
        }
    }

    public Object getMsg() {
        return msg;
    }
}
