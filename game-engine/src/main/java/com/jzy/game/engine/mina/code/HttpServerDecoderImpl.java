package com.jzy.game.engine.mina.code;

import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.http.ArrayUtil;
import org.apache.mina.http.HttpRequestImpl;
import org.apache.mina.http.api.HttpMethod;
import org.apache.mina.http.api.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息解码
 *
 * @author JiangZhiYong
 * @version $Id: $Id
 */
public class HttpServerDecoderImpl extends CumulativeProtocolDecoder {
	private static final Charset CHARSET = Charset.forName("UTF-8");

	private static final Logger LOG = LoggerFactory.getLogger(HttpServerDecoderImpl.class);

	/**
	 * Regex to parse HttpRequest Request Line
	 */
	public static final Pattern REQUEST_LINE_PATTERN = Pattern.compile(" ");

	/**
	 * Regex to parse out QueryString from HttpRequest
	 */
	public static final Pattern QUERY_STRING_PATTERN = Pattern.compile("\\?");

	/**
	 * Regex to parse out parameters from query string
	 */
	public static final Pattern PARAM_STRING_PATTERN = Pattern.compile("\\&|;");

	/**
	 * Regex to parse out key/value pairs
	 */
	public static final Pattern KEY_VALUE_PATTERN = Pattern.compile("=");

	/**
	 * Regex to parse raw headers and body
	 */
	public static final Pattern RAW_VALUE_PATTERN = Pattern.compile("\\r\\n\\r\\n");

	/**
	 * Regex to parse raw headers from body
	 */
	public static final Pattern HEADERS_BODY_PATTERN = Pattern.compile("\\r\\n");

	/**
	 * Regex to parse header name and value
	 */
	public static final Pattern HEADER_VALUE_PATTERN = Pattern.compile(":");

	/**
	 * Regex to split cookie header following RFC6265 Section 5.4
	 */
	public static final Pattern COOKIE_SEPARATOR_PATTERN = Pattern.compile(";");
	/** 已解析的HTTP对象 */
	public static final String HTTP_REQUEST = "http.request";

	/** {@inheritDoc} */
	@Override
	protected boolean doDecode(IoSession session, IoBuffer msg, ProtocolDecoderOutput out) throws Exception {
		/**
		 * 消息已经解析
		 * 谷歌浏览器一次请求存在多次收到消息，还额外请求了/favicon.ico路径
		 */
		if (session.containsAttribute(HTTP_REQUEST)) {
			return false;
		}
		msg.mark();
		HttpRequestImpl rq = parseHttpRequestHead(msg.buf(), msg);
		if (rq != null) {
			out.write(rq);
			session.setAttribute(HTTP_REQUEST, rq);
			// LOG.info("解析成功");
			return true;
		}
		msg.reset();
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public void finishDecode(final IoSession session, final ProtocolDecoderOutput out) throws Exception {
	}

	/** {@inheritDoc} */
	@Override
	public void dispose(final IoSession session) throws Exception {
	}

	private HttpRequestImpl parseHttpRequestHead(final ByteBuffer buffer, IoBuffer msg) throws Exception {
		// Java 6 >> String raw = new String(buffer.array(), 0, buffer.limit(),
		// Charset.forName("UTF-8"));
		final String raw = new String(buffer.array(), 0, buffer.limit());
//		LOG.debug(raw);
		final String[] headersAndBody = RAW_VALUE_PATTERN.split(raw, -1);

		if (headersAndBody.length <= 1) {
			return null;
		}

		String[] headerFields = HEADERS_BODY_PATTERN.split(headersAndBody[0]);
		headerFields = ArrayUtil.dropFromEndWhile(headerFields, "");

		final String requestLine = headerFields[0];
		final Map<String, String> generalHeaders = new HashMap<String, String>();

		for (int i = 1; i < headerFields.length; i++) {
			final String[] header = HEADER_VALUE_PATTERN.split(headerFields[i]);
			generalHeaders.put(header[0].toLowerCase(), header[1].trim());
		}

		final String[] elements = REQUEST_LINE_PATTERN.split(requestLine);
		final HttpMethod method = HttpMethod.valueOf(elements[0]);
		final HttpVersion version = HttpVersion.fromString(elements[2]);
		final String[] pathFrags = QUERY_STRING_PATTERN.split(elements[1]);
		final String requestedPath = pathFrags[0];
		String queryString = pathFrags.length >= 2 ? pathFrags[1] : "";
		queryString = URLDecoder.decode(queryString, "UTF-8");

		// we put the buffer position where we found the beginning of the HTTP
		// body
		buffer.position(headersAndBody[0].length() + 4);

		// POST 請求
		String contentLen = generalHeaders.get("content-length");
		// post 数据
		if (contentLen != null && method == HttpMethod.POST) {
			LOG.debug("found content len : {}", contentLen);
			LOG.debug("decoding BODY: {} bytes", msg.remaining());
			int contentLength = Integer.valueOf(contentLen);
			if (contentLength <= msg.remaining()) {
				byte[] content = new byte[contentLength];
				msg.get(content);
				String str = new String(content, CHARSET);
				queryString = URLDecoder.decode(str, "UTF-8");
			}
		}
		return new HttpRequestImpl(version, method, requestedPath, queryString, generalHeaders);
	}
}
