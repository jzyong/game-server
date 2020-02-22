/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jzy.game.engine.mina.websocket;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Codec Factory used for creating websocket filter.
 *
 * @author DHRUV CHOPRA
 * @version $Id: $Id
 */
public class WebSocketCodecFactory implements ProtocolCodecFactory{
    private final ProtocolEncoder encoder;
    private final ProtocolDecoder decoder;

    /**
     * <p>Constructor for WebSocketCodecFactory.</p>
     */
    public WebSocketCodecFactory() {
            encoder = new WebSocketEncoder();
            decoder = new WebSocketDecoder();
    }

    /** {@inheritDoc} */
    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return encoder;
    }

    /** {@inheritDoc} */
    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return decoder;
    }    
}
