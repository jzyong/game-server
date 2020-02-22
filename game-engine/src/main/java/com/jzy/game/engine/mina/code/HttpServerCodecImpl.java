package com.jzy.game.engine.mina.code;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.http.HttpServerDecoder;

/**
 * http消息解码编码工厂
 *
 * @author JiangZhiYong
 * @date 2017-03-31 QQ:359135103
 * @version $Id: $Id
 */
public class HttpServerCodecImpl extends ProtocolCodecFilter {

	/** Key for decoder current state */
	private static final String DECODER_STATE_ATT = "http.ds";

	/** Key for the partial HTTP requests head */
	private static final String PARTIAL_HEAD_ATT = "http.ph";
	private static final ProtocolEncoder encoder = new HttpServerEncoderImpl();
	private static final ProtocolDecoder decoder = new HttpServerDecoderImpl();

	/**
	 * <p>Constructor for HttpServerCodecImpl.</p>
	 */
	public HttpServerCodecImpl() {
		super(encoder, decoder);
	}

	/** {@inheritDoc} */
	@Override
	public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
		super.sessionClosed(nextFilter, session);
		session.removeAttribute(DECODER_STATE_ATT);
		session.removeAttribute(PARTIAL_HEAD_ATT);
	}
}
