package com.jzy.game.engine.mina.code;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.http.HttpServerEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息编码
 *
 * @author JiangZhiYong
 * @date 2017-03-31 QQ:359135103
 * @version $Id: $Id
 */
public class HttpServerEncoderImpl extends HttpServerEncoder {
	private static final Logger LOG = LoggerFactory.getLogger(HttpServerEncoderImpl.class);
	private static final CharsetEncoder ENCODER = Charset.forName("UTF-8").newEncoder();
	private static final byte[] CRLF = { 13, 10 };
	private static final String CONTENTLENGTH = "Content-Length: ";

	/** {@inheritDoc} */
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		LOG.debug("encode {}", message.getClass().getCanonicalName());
		if ((message instanceof HttpResponseImpl)) {
			HttpResponseImpl msg = (HttpResponseImpl) message;
			IoBuffer buf = IoBuffer.allocate(128).setAutoExpand(true);
			buf.putString(msg.getStatus().line(), ENCODER);
			for (Map.Entry<String, String> header : msg.getHeaders().entrySet()) {
				buf.putString((CharSequence) header.getKey(), ENCODER);
				buf.putString(": ", ENCODER);
				buf.putString((CharSequence) header.getValue(), ENCODER);
				buf.put(CRLF);
			}
			if (msg.getBody() != null) {
				buf.putString(CONTENTLENGTH, ENCODER);
				buf.putString(String.valueOf(msg.getBody().length), ENCODER);
				buf.put(CRLF);
			}
			buf.put(CRLF);
			if (msg.getBody() != null) {
				buf.put(msg.getBody());
			}
			buf.flip();
			out.write(buf);
		}
	}

	/** {@inheritDoc} */
	public void dispose(IoSession session) throws Exception {
	}
}
