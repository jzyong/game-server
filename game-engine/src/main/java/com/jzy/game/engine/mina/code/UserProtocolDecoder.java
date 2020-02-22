package com.jzy.game.engine.mina.code;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.jzy.game.engine.util.MsgUtil;

/**
 * 客户端消息解析
 * <br>
 * 消息频率检查
 *
 * @author wzyi
 * @QQ 156320312
 * @Te 18202020823
 * @version $Id: $Id
 */
public class UserProtocolDecoder extends ProtocolDecoderImpl {

    private static final String START_TIME = "start_time";
    private static final String RECEIVE_COUNT = "receive_count";

    private int maxCountPerSecond = 30;

    /**
     * <p>Constructor for UserProtocolDecoder.</p>
     */
    public UserProtocolDecoder() {
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doDecode(IoSession session, IoBuffer ib, ProtocolDecoderOutput out) throws Exception {
        boolean res = super.doDecode(session, ib, out);
        if (res) {
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
                    MsgUtil.close(session, "--> 消息过于频繁:%d,超过次数：%d", count, getMaxCountPerSecond());
                    return false;
                }
                startTime = session.getLastReadTime();
                count = 0;
                session.setAttribute(START_TIME, startTime);
            }
            count++;
            session.setAttribute(RECEIVE_COUNT, count);
        }
        return res;
    }

    /**
     * <p>Getter for the field <code>maxCountPerSecond</code>.</p>
     *
     * @return a int.
     */
    public int getMaxCountPerSecond() {
        return maxCountPerSecond;
    }

    /**
     * <p>Setter for the field <code>maxCountPerSecond</code>.</p>
     *
     * @param maxCountPerSecond a int.
     */
    public void setMaxCountPerSecond(int maxCountPerSecond) {
        this.maxCountPerSecond = maxCountPerSecond;
    }
}
