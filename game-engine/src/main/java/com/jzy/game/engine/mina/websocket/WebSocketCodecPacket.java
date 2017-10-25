/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jzy.game.engine.mina.websocket;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Defines the class whose objects are understood by websocket encoder.
 * 
 * @author DHRUV CHOPRA
 */
public class WebSocketCodecPacket {
    private IoBuffer packet;
    
    /*
     * Builds an instance of WebSocketCodecPacket that simply wraps around 
     * the given IoBuffer.
     */
    public static WebSocketCodecPacket buildPacket(IoBuffer buffer){
        return new WebSocketCodecPacket(buffer);
    }
    
    private WebSocketCodecPacket(IoBuffer buffer){
        packet = buffer;
    }
    
    public IoBuffer getPacket(){
        return packet;
    }
}
