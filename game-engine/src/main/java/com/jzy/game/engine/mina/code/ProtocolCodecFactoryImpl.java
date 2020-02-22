package com.jzy.game.engine.mina.code;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * 消息解析工厂
 *
 * @author JiangZhiYong
 * @date 2017-03-30
 * QQ:359135103
 * @version $Id: $Id
 */
public class ProtocolCodecFactoryImpl implements ProtocolCodecFactory {

    protected final ProtocolDecoderImpl decoder;
    protected final ProtocolEncoderImpl encoder;

    /**
     * <p>Constructor for ProtocolCodecFactoryImpl.</p>
     *
     * @param decoder a {@link com.jzy.game.engine.mina.code.ProtocolDecoderImpl} object.
     * @param encoder a {@link com.jzy.game.engine.mina.code.ProtocolEncoderImpl} object.
     */
    public ProtocolCodecFactoryImpl(ProtocolDecoderImpl decoder, ProtocolEncoderImpl encoder) {
        this.decoder = decoder;
        this.encoder = encoder;
    }

    /** {@inheritDoc} */
    @Override
    public ProtocolEncoder getEncoder(IoSession is) throws Exception {
        return getEncoder();
    }

    /** {@inheritDoc} */
    @Override
    public ProtocolDecoder getDecoder(IoSession is) throws Exception {
        return getDecoder();
    }

    /**
     * <p>Getter for the field <code>decoder</code>.</p>
     *
     * @return a {@link com.jzy.game.engine.mina.code.ProtocolDecoderImpl} object.
     */
    public ProtocolDecoderImpl getDecoder() {
        return decoder;
    }

    /**
     * <p>Getter for the field <code>encoder</code>.</p>
     *
     * @return a {@link com.jzy.game.engine.mina.code.ProtocolEncoderImpl} object.
     */
    public ProtocolEncoderImpl getEncoder() {
        return encoder;
    }
}
