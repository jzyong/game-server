package com.jzy.game.engine.mina.code;

/**
 * <p>UserProtocolCodecFactory class.</p>
 *
 * @author wzyi
 * @QQ 156320312
 * @Te 18202020823
 * @version $Id: $Id
 */
public class UserProtocolCodecFactory extends ProtocolCodecFactoryImpl {

    /**
     * <p>Constructor for UserProtocolCodecFactory.</p>
     */
    public UserProtocolCodecFactory() {
        super(new UserProtocolDecoder(), new ProtocolEncoderImpl());
        encoder.overScheduledWriteBytesHandler = io -> {
            io.close(true);
            return true;
        };
    }
    
    /**
     * <p>setMaxCountPerSecond.</p>
     *
     * @param maxCountPerSecond a int.
     */
    public void setMaxCountPerSecond(int maxCountPerSecond) {
        ((UserProtocolDecoder)getDecoder()).setMaxCountPerSecond(maxCountPerSecond);
    }
}
