package com.jzy.game.engine.mina.code;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.mina.message.MassMessage;
import com.jzy.game.engine.util.MsgUtil;

/**群发消息
 * @author wzyi
 * @QQ 156320312
 * @Te 18202020823
 */
public class MassProtocolEncoder extends ProtocolEncoderImpl {

    protected static Logger log = LoggerFactory.getLogger(MassProtocolEncoder.class);

    public MassProtocolEncoder() {
    }

    /**
     * 编码，格式：数据长度|数据部分
     *
     * @param session
     * @param obj
     * @param out
     * @throws Exception
     */
    @Override
    public void encode(IoSession session, Object obj, ProtocolEncoderOutput out)
            throws Exception {
        if (getOverScheduledWriteBytesHandler() != null && session.getScheduledWriteMessages() > getMaxScheduledWriteMessages() && getOverScheduledWriteBytesHandler().test(session)) {
            return;
        }
        IoBuffer buf = null;
        if (obj instanceof MassMessage) {
            buf = MsgUtil.toIobuffer((MassMessage) obj);
        } else {
            log.warn("未知的数据类型");
            return;
        }
        if (buf != null && session.isConnected()) {
            buf.rewind();
            out.write(buf);
            out.flush();
        }
    }
}
