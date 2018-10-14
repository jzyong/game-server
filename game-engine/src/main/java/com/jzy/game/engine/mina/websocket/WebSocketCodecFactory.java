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
 */
public class WebSocketCodecFactory implements ProtocolCodecFactory{
    private final ProtocolEncoder encoder;
    private final ProtocolDecoder decoder;

    public WebSocketCodecFactory() {
            encoder = new WebSocketEncoder();
            decoder = new WebSocketDecoder();
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return decoder;
    }    
}
