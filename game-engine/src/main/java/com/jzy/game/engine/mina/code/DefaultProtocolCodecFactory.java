package com.jzy.game.engine.mina.code;

/**
 * 默认消息解析工厂
 *
 * @author JiangZhiYong
 * @date 2017-03-30
 * QQ:359135103
 */
public class DefaultProtocolCodecFactory extends ProtocolCodecFactoryImpl{

    public DefaultProtocolCodecFactory() {
        super(new ProtocolDecoderImpl(), new ProtocolEncoderImpl());
    }
}
