package com.jzy.game.engine.mina.code;

import com.google.protobuf.Message;
import com.jzy.game.engine.mina.message.IDMessage;
import com.jzy.game.engine.util.MsgUtil;

import java.util.function.Predicate;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * 消息编码
 * TODO 需要修改
 *
 * @author JiangZhiYong
 * @date 2017-03-30
 * QQ:359135103
 * @version $Id: $Id
 */
public class ProtocolEncoderImpl implements ProtocolEncoder {

    /** Constant <code>log</code> */
    protected static Logger log = LoggerFactory.getLogger(ProtocolEncoderImpl.class);
    /**
     * 允许的最大堆积未发送消息条数
     */
    protected int maxScheduledWriteMessages = 256;
    /**
     * 当超过设置的最大堆积消息条数时的处理
     */
    protected Predicate<IoSession> overScheduledWriteBytesHandler;

    /**
     * <p>Constructor for ProtocolEncoderImpl.</p>
     */
    public ProtocolEncoderImpl() {
    }

    /** {@inheritDoc} */
    @Override
    public void dispose(IoSession session)
            throws Exception {
    }

    /**
     * {@inheritDoc}
     *
     * 编码，格式：数据长度|数据部分
     */
    @Override
    public void encode(IoSession session, Object obj, ProtocolEncoderOutput out)
            throws Exception {
        if (getOverScheduledWriteBytesHandler() != null && session.getScheduledWriteMessages() > getMaxScheduledWriteMessages() && getOverScheduledWriteBytesHandler().test(session)) {
            return;
        }
        IoBuffer buf = null;
        if (obj instanceof Message) {
            buf = MsgUtil.toIobuffer((Message) obj);
        } else if (obj instanceof IDMessage) {
            buf = MsgUtil.toIobuffer((IDMessage) obj);
        } else if (obj instanceof IoBuffer) {//必须符合完整的编码格式
            buf = (IoBuffer) obj;
        } else if (obj instanceof byte[]) {//必须符合除去消息长度后的编码格式
            byte[] data = (byte[]) obj;
            buf = IoBuffer.allocate(data.length + 4);
            buf.putInt(data.length);
            buf.put(data);
        } else {
            log.warn("未知的数据类型");
            return;
        }
        if (buf != null && session.isConnected()) {
            buf.rewind();
//            log.warn("发送的数据byte[]{}",IntUtil.BytesToStr(buf.array()));
            out.write(buf);
            out.flush();
        }
    }

    /**
     * <p>Getter for the field <code>maxScheduledWriteMessages</code>.</p>
     *
     * @return a int.
     */
    public int getMaxScheduledWriteMessages() {
        return maxScheduledWriteMessages;
    }

    /**
     * <p>Setter for the field <code>maxScheduledWriteMessages</code>.</p>
     *
     * @param maxScheduledWriteMessages a int.
     */
    public void setMaxScheduledWriteMessages(int maxScheduledWriteMessages) {
        this.maxScheduledWriteMessages = maxScheduledWriteMessages < 256 ? 256 : maxScheduledWriteMessages;
    }

    /**
     * <p>Getter for the field <code>overScheduledWriteBytesHandler</code>.</p>
     *
     * @return a {@link java.util.function.Predicate} object.
     */
    public Predicate<IoSession> getOverScheduledWriteBytesHandler() {
        return overScheduledWriteBytesHandler;
    }

    /**
     * <p>Setter for the field <code>overScheduledWriteBytesHandler</code>.</p>
     *
     * @param overScheduledWriteBytesHandler a {@link java.util.function.Predicate} object.
     */
    public void setOverScheduledWriteBytesHandler(Predicate<IoSession> overScheduledWriteBytesHandler) {
        this.overScheduledWriteBytesHandler = overScheduledWriteBytesHandler;
    }
}
