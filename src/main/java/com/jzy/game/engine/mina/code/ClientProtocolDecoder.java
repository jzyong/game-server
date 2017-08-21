/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jzy.game.engine.mina.code;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.util.IntUtil;
import com.jzy.game.engine.util.MsgUtil;

/**
 * 游戏客户端消息解码
 * 
 *  <p>
 * 包长度（2）+消息ID（4）+消息长度（4）+消息内容
 * <br> 返回byte数组已去掉包长度
 * </p>
 * TODO 加解密
 * @author JiangZhiYong
 * @QQ 359135103
 */
public class ClientProtocolDecoder extends ProtocolDecoderImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientProtocolDecoder.class);
    
    private static final String START_TIME = "start_time";	//消息开始时间
    private static final String RECEIVE_COUNT = "receive_count";//消息接收次数
    
    private int maxCountPerSecond = 30;	//每秒钟最大接收消息数

    public ClientProtocolDecoder() {
    }

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        int readAbleLen = in.remaining();
        if (readAbleLen < 2) {
            return false;
        }
        in.mark(); //标记阅读位置
        byte[] bs = new byte[2];
        in.get(bs, 0, 2);
        short packageLength = IntUtil.bigEndianByteToShort(bs, 0, 2);
        if (packageLength <1||packageLength>maxReadSize) {
            LOGGER.warn("消息包长度为：{}", packageLength);
            in.clear();
            session.closeNow();
            return false;
        }

        if (in.remaining() < packageLength) {   //消息长度不够，重置位置
            in.reset();
            return false;
        }

        decodeBytes(packageLength, in, out);
        
        if(!checkMsgFrequency(session)){
        	return false;
        }
        
        return true;
    }
    
    /**
     * 检测玩家消息发送频率
     * @author JiangZhiYong
     * @QQ 359135103
     * 2017年8月21日 下午1:47:58
     * @param session
     */
    private boolean checkMsgFrequency(IoSession session){
    	 int count = 0;
         long startTime = 0L;
         if (session.containsAttribute(START_TIME)) {
             startTime = (long) session.getAttribute(START_TIME);
         }
         if (session.containsAttribute(RECEIVE_COUNT)) {
             count = (int) session.getAttribute(RECEIVE_COUNT);
         }
         if (session.getLastReadTime() - startTime > 1000L) {
             if (count > getMaxCountPerSecond()) {
                 MsgUtil.close(session, "%s--> 消息过于频繁:%d,超过次数：%d", MsgUtil.getIp(session),count, getMaxCountPerSecond());
                 return false;
             }
             startTime = session.getLastReadTime();
             count = 0;
             session.setAttribute(START_TIME, startTime);
         }
         count++;
         session.setAttribute(RECEIVE_COUNT, count);
         return true;
    }

	public int getMaxCountPerSecond() {
		return maxCountPerSecond;
	}

	public void setMaxCountPerSecond(int maxCountPerSecond) {
		this.maxCountPerSecond = maxCountPerSecond;
	}

    
    
}
