package com.jzy.game.engine.mina.message;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.jzy.game.engine.util.MsgUtil;

import io.netty.channel.Channel;

/**
 * 
 * 带用户ID的消息
 * <br>
 * netty消息发送必须设置msgID,mina不能设置
 * @author JiangZhiYong
 * @date 2017-03-31
 * QQ:359135103
 */
public final class IDMessage implements Runnable{

    private final Object msg;
    private final long userID;	//用户ID或角色ID，当角色ID不存在时，用用户ID
    private  IoSession session;
    private Channel channel;
    private Integer msgId;	//消息ID
    
    /**
     * netty使用
     * @param channel
     * @param msg
     * @param userID
     * @param msgId
     */
    public IDMessage(Channel channel,Object msg, long userID,Integer msgId) {
		super();
		this.channel=channel;
		this.msg = msg;
		this.userID = userID;
		this.msgId=msgId;
	}

	/**
     * mina 使用
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
        }else if(channel!=null&&channel.isActive()){
        	channel.write(this);		
        }
    }

    public Object getMsg() {
        return msg;
    }

	public Integer getMsgId() {
		return msgId;
	}

	public void setMsgId(Integer msgId) {
		this.msgId = msgId;
	}
    
    
}
