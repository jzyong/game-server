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
 * @version $Id: $Id
 */
public class WebSocketCodecPacket {
    private final IoBuffer packet;
    
    /*
     * Builds an instance of WebSocketCodecPacket that simply wraps around 
     * the given IoBuffer.
     */
    /**
     * <p>buildPacket.</p>
     *
     * @param buffer a {@link org.apache.mina.core.buffer.IoBuffer} object.
     * @return a {@link com.jzy.game.engine.mina.websocket.WebSocketCodecPacket} object.
     */
    public static WebSocketCodecPacket buildPacket(IoBuffer buffer){
        return new WebSocketCodecPacket(buffer);
    }
    
    private WebSocketCodecPacket(IoBuffer buffer){
        packet = buffer;
    }
    
    /**
     * <p>Getter for the field <code>packet</code>.</p>
     *
     * @return a {@link org.apache.mina.core.buffer.IoBuffer} object.
     */
    public IoBuffer getPacket(){
        return packet;
    }
}
