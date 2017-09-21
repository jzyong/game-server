/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jzy.game.engine.mina.websocket;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Static utility class containing methods used for websocket encoding
 * and decoding.
 *
 * @author DHRUV CHOPRA
 */
class WebSocketUtils {
    
    static final String SessionAttribute = "isWEB";
    // Construct a successful websocket handshake response using the key param
    // (See RFC 6455).
    static WebSocketHandShakeResponse buildWSHandshakeResponse(String key){
        String response = "HTTP/1.1 101 Web Socket Protocol Handshake\r\n";
        response += "Upgrade: websocket\r\n";
        response += "Connection: Upgrade\r\n";
        response += "Sec-WebSocket-Accept: " + key + "\r\n";
        response += "\r\n";        
        return new WebSocketHandShakeResponse(response);
    }
    
    // Parse the string as a websocket request and return the value from
    // Sec-WebSocket-Key header (See RFC 6455). Return empty string if not found.
    static String getClientWSRequestKey(String WSRequest) {
        String[] headers = WSRequest.split("\r\n");
        String socketKey = "";
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].contains("Sec-WebSocket-Key")) {
                socketKey = (headers[i].split(":")[1]).trim();
                break;
            }
        }
        return socketKey;
    }    
    
    // 
    // Builds the challenge response to be used in WebSocket handshake.
    // First append the challenge with "258EAFA5-E914-47DA-95CA-C5AB0DC85B11" and then
    // make a SHA1 hash and finally Base64 encode it. (See RFC 6455)
    static String getWebSocketKeyChallengeResponse(String challenge) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        challenge += "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        MessageDigest cript = MessageDigest.getInstance("SHA-1");
        cript.reset();
        cript.update(challenge.getBytes("utf8"));
        byte[] hashedVal = cript.digest();        
        return Base64.encodeBytes(hashedVal);
    }
}
