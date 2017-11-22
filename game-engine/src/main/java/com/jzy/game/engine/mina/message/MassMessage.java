package com.jzy.game.engine.mina.message;

import java.util.Collection;
import org.apache.mina.core.buffer.IoBuffer;

/**
 * 群发消息
 *
 * @author wzyi
 * @QQ 156320312
 * @Te 18202020823
 */
public class MassMessage {

    private final IoBuffer buffer;
    private final Collection<Long> targets;	//用户ID

    public MassMessage(IoBuffer buffer, Collection<Long> targets) {
        this.buffer = buffer;
        this.targets = targets;
        this.buffer.rewind();
    }

    public int getLength() {
        return buffer.remaining() + targets.size() * 8;
    }
    
    public int getBuffLength() {
        return buffer.remaining();
    }

    /**
     * @return the buffer
     */
    public IoBuffer getBuffer() {
        return buffer;
    }

    /**
     * @return the targets
     */
    public Collection<Long> getTargets() {
        return targets;
    }
}
