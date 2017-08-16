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

/**
 * 游戏客户端消息解码
 * 
 *  <p>
 * 包长度（2）+消息ID（4）+消息长度（4）+消息内容
 * <br> 返回byte数组已去掉包长度
 * </p>
 *
 * @author JiangZhiYong
 * @QQ 359135103
 */
public class ClientProtocolDecoder extends ProtocolDecoderImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientProtocolDecoder.class);

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
        if (packageLength <= 0) {
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

        //TODO
        return true;
    }

}
