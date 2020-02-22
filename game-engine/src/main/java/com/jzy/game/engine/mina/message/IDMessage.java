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
 *
 * @author JiangZhiYong
 * @date 2017-03-31
 * QQ:359135103
 * @version $Id: $Id
 */
public final class IDMessage implements Runnable{

    private final Object msg;
    private final long userID;	//用户ID或角色ID，当角色ID不存在时，用用户ID
    private  IoSession session;
    private Channel channel;
    private Integer msgId;	//消息ID
    
    /**
     * netty使用
     *
     * @param channel a {@link io.netty.channel.Channel} object.
     * @param msg a {@link java.lang.Object} object.
     * @param userID a long.
     * @param msgId a {@link java.lang.Integer} object.
     */
    public IDMessage(Channel channel,Object msg, long userID,Integer msgId) {
        this.channel=channel;
		this.msg = msg;
		this.userID = userID;
		this.msgId=msgId;
	}

    /**
     * mina 使用
     *
     * @param session a {@link org.apache.mina.core.session.IoSession} object.
     * @param msg a {@link java.lang.Object} object.
     * @param userID a long.
     */
    public IDMessage(IoSession session, Object msg, long userID) {
        this.msg = msg;
        this.userID = userID;
        this.session=session;
    }

    /**
     * <p>Getter for the field <code>userID</code>.</p>
     *
     * @return a long.
     */
    public long getUserID() {
        return userID;
    }

    /**
     * <p>Getter for the field <code>session</code>.</p>
     *
     * @return a {@link org.apache.mina.core.session.IoSession} object.
     */
    public IoSession getSession() {
        return session;
    }

    /** {@inheritDoc} */
    @Override
    public void run() {
        if (session!=null&&session.isConnected()) {
            IoBuffer buf=MsgUtil.toIobuffer(this);
            session.write(buf);
        }else if(channel!=null&&channel.isActive()){
        	channel.write(this);		
        }
    }

    /**
     * <p>Getter for the field <code>msg</code>.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    public Object getMsg() {
        return msg;
    }

	/**
	 * <p>Getter for the field <code>msgId</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getMsgId() {
		return msgId;
	}

	/**
	 * <p>Setter for the field <code>msgId</code>.</p>
	 *
	 * @param msgId a {@link java.lang.Integer} object.
	 */
	public void setMsgId(Integer msgId) {
		this.msgId = msgId;
	}
    
    
}
