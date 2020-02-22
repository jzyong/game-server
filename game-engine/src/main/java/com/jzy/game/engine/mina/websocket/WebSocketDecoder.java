/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jzy.game.engine.mina.websocket;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * Decodes incoming buffers in a manner that makes the sender transparent to the
 * decoders further up in the filter chain. If the sender is a native client then
 * the buffer is simply passed through. If the sender is a websocket, it will extract
 * the content out from the dataframe and parse it before passing it along the filter
 * chain.
 *
 * @author DHRUV CHOPRA
 * @version $Id: $Id
 */
public class WebSocketDecoder extends CumulativeProtocolDecoder{    
    
    /** {@inheritDoc} */
    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {        
        IoBuffer resultBuffer;
        if(!session.containsAttribute(WebSocketUtils.SessionAttribute)){
            // first message on a new connection. see if its from a websocket or a 
            // native socket.
            if(tryWebSockeHandShake(session, in, out)){
                // websocket handshake was successful. Don't write anything to output
                // as we want to abstract the handshake request message from the handler.
                in.position(in.limit());
                return true;
            }
            else{
                // message is from a native socket. Simply wrap and pass through.
                resultBuffer = IoBuffer.wrap(in.array(), 0, in.limit());
                in.position(in.limit());
                session.setAttribute(WebSocketUtils.SessionAttribute, false);
            }
            out.write(resultBuffer);
        }
        else if(session.containsAttribute(WebSocketUtils.SessionAttribute) && true==(Boolean)session.getAttribute(WebSocketUtils.SessionAttribute)){            
            // there is incoming data from the websocket. Decode and send to handler or next filter.     
            int startPos = in.position();
            resultBuffer = buildWSDataBuffer(in, session);
            if(resultBuffer == null){
                // There was not enough data in the buffer to parse. Reset the in buffer
                // position and wait for more data before trying again.
                in.position(startPos);
                return false;
            }
            //转换为byte数组
//            int int1 = resultBuffer.getInt();
           out.write(resultBuffer.array());
        }
        else{
            // session is known to be from a native socket. So
            // simply wrap and pass through.
            resultBuffer = IoBuffer.wrap(in.array(), 0, in.limit());    
            in.position(in.limit());
            out.write(resultBuffer);
        }                        
        return true;
    }

    /**
    *   Try parsing the message as a websocket handshake request. If it is such
    *   a request, then send the corresponding handshake response (as in Section 4.2.2 RFC 6455).
    */
    private boolean tryWebSockeHandShake(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
        
        try{
            String payLoadMsg = new String(in.array());
            String socketKey = WebSocketUtils.getClientWSRequestKey(payLoadMsg);
            if(socketKey.length() <= 0){
                return false;
            }
            String challengeAccept = WebSocketUtils.getWebSocketKeyChallengeResponse(socketKey);            
            WebSocketHandShakeResponse wsResponse = WebSocketUtils.buildWSHandshakeResponse(challengeAccept);
            session.setAttribute(WebSocketUtils.SessionAttribute, true);
            session.write(wsResponse);
            return true;
        }
        catch(Exception e){
            // input is not a websocket handshake request.
            return false;
        }        
    }
    
    // Decode the in buffer according to the Section 5.2. RFC 6455
    // If there are multiple websocket dataframes in the buffer, this will parse
    // all and return one complete decoded buffer.
    private static IoBuffer buildWSDataBuffer(IoBuffer in, IoSession session) {

        IoBuffer resultBuffer = null;
        do{
            byte frameInfo = in.get();            
            byte opCode = (byte) (frameInfo & 0x0f);
            if (opCode == 8) {
                // opCode 8 means close. See RFC 6455 Section 5.2
                // return what ever is parsed till now.
                session.close(true);
                return resultBuffer;
            }        
            int frameLen = (in.get() & (byte) 0x7F);
            if(frameLen == 126){
                frameLen = in.getShort();
            }
            
            // Validate if we have enough data in the buffer to completely
            // parse the WebSocket DataFrame. If not return null.
            if(frameLen+4 > in.remaining()){                
                return null;
            }
            byte mask[] = new byte[4];
            for (int i = 0; i < 4; i++) {
                mask[i] = in.get();
            }

            /*  now un-mask frameLen bytes as per Section 5.3 RFC 6455
                Octet i of the transformed data ("transformed-octet-i") is the XOR of
                octet i of the original data ("original-octet-i") with octet at index
                i modulo 4 of the masking key ("masking-key-octet-j"):

                j                   = i MOD 4
                transformed-octet-i = original-octet-i XOR masking-key-octet-j
            * 
            */
             
            byte[] unMaskedPayLoad = new byte[frameLen];
            for (int i = 0; i < frameLen; i++) {
                byte maskedByte = in.get();
                unMaskedPayLoad[i] = (byte) (maskedByte ^ mask[i % 4]);
            }
            
            if(resultBuffer == null){
                resultBuffer = IoBuffer.wrap(unMaskedPayLoad);
                resultBuffer.position(resultBuffer.limit());
                resultBuffer.setAutoExpand(true);
            }
            else{
                resultBuffer.put(unMaskedPayLoad);
            }
        }
        while(in.hasRemaining());
        
        resultBuffer.flip();
        return resultBuffer;

    }    
}
