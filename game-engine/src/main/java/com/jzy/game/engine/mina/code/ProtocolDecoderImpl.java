package com.jzy.game.engine.mina.code;

import java.util.function.Predicate;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;

/**
 * 消息解码
 *
 * @author JiangZhiYong
 * @date 2017-03-30
 * QQ:359135103
 */
public class ProtocolDecoderImpl extends CumulativeProtocolDecoder {

    protected static final Logger log = LoggerFactory.getLogger(ProtocolDecoderImpl.class);

    protected int maxReadSize = 10240;	//最大消息长度
    protected Predicate<IoSession> overMaxReadSizeHandler;

    public ProtocolDecoderImpl() {

    }

    @Override
    protected boolean doDecode(IoSession session, IoBuffer ib, ProtocolDecoderOutput out) throws Exception {
        if (ib.remaining() < 4) {
            return false;
        }
        ib.mark();
        int length = ib.getInt();
        if (length < 1 || length > maxReadSize) {
            int id = ib.getInt();
            ib.clear();
            log.warn("消息解析异常：长度{},id {}, 大于长度 maxReadSize {}", length, id, maxReadSize);
            session.closeNow();
            return false;
        }
        if (getOverMaxReadSizeHandler() != null && getOverMaxReadSizeHandler().test(session)) {
            int id = ib.getInt();
            ib.clear();
            log.warn("消息解析异常：长度{},id {}", length, id);
            return false;
        }

        if (ib.remaining() < length) {
            ib.reset();
            return false;
        }
        decodeBytes(length, ib, out);
        return true;
    }

    /**
     * 不包括消息长度
     *
     * @param length
     * @param ib
     * @param out
     */
    protected void decodeBytes(int length, IoBuffer ib, ProtocolDecoderOutput out) {
        byte[] bytes = new byte[length];
        ib.get(bytes);
        out.write(bytes);
    }

    public int getMaxReadSize() {
        return maxReadSize;
    }

    public void setMaxReadSize(int maxReadSize) {
        this.maxReadSize = maxReadSize;
    }

    public Predicate<IoSession> getOverMaxReadSizeHandler() {
        return overMaxReadSizeHandler;
    }

    public void setOverMaxReadSizeHandler(Predicate<IoSession> overMaxReadSizeHandler) {
        this.overMaxReadSizeHandler = overMaxReadSizeHandler;
    }
}
