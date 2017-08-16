package com.jzy.game.engine.mina.code;

/**
 *
 * @author wzyi
 * @QQ 156320312
 * @Te 18202020823
 */
public class UserProtocolCodecFactory extends ProtocolCodecFactoryImpl {

    public UserProtocolCodecFactory() {
        super(new UserProtocolDecoder(), new ProtocolEncoderImpl());
        encoder.overScheduledWriteBytesHandler = io -> {
            io.close(true);
            return true;
        };
    }
    
    public void setMaxCountPerSecond(int maxCountPerSecond) {
        ((UserProtocolDecoder)getDecoder()).setMaxCountPerSecond(maxCountPerSecond);
    }
}
