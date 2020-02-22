package com.jzy.game.engine.handler;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.http.HttpRequestImpl;
import org.apache.mina.http.api.HttpStatus;

import com.alibaba.fastjson.JSON;
import com.jzy.game.engine.mina.code.HttpResponseImpl;

/**
 * http请求
 *
 * @author JiangZhiYong
 * @date 2017-03-31 QQ:359135103
 * @version $Id: $Id
 */
public abstract class HttpHandler implements IHandler {

	private HttpResponseImpl response; // 返回消息

	private IoSession session; // 消息来源
	private HttpRequestImpl request; // 请求消息

	private long createTime;

	/** {@inheritDoc} */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/** {@inheritDoc} */
	@Override
	public IoSession getSession() {
		return session;
	}

	/** {@inheritDoc} */
	@Override
	public void setSession(IoSession session) {
		this.session = session;
	}

	/** {@inheritDoc} */
	@Override
	public HttpRequestImpl getMessage() { // HttpRequestImpl
		return (HttpRequestImpl) request;
	}

	/** {@inheritDoc} */
	@Override
	public void setMessage(Object message) {
		if (message instanceof HttpRequestImpl) {
            request = (HttpRequestImpl) message;
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * 返回消息
	 */
	@Override
	public HttpResponseImpl getParameter() {
		if (response == null) {
            response = new HttpResponseImpl();
		}
		return response;
	}

	/**
	 * {@inheritDoc}
	 *
	 * 返回消息
	 */
	@Override
	public void setParameter(Object parameter) {
		// if (parameter instanceof HttpResponseMessage) {
        response = (HttpResponseImpl) parameter;
		// }
	}

	/** {@inheritDoc} */
	@Override
	public long getCreateTime() {
		return createTime;
	}

	/** {@inheritDoc} */
	@Override
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	/**
	 * 没有返回值的情况下处理
	 *
	 * @return a {@link com.jzy.game.engine.mina.code.HttpResponseImpl} object.
	 */
	protected HttpResponseImpl errResponseMessage() {
		HttpResponseImpl response = new HttpResponseImpl();
		response.setStatus(HttpStatus.CLIENT_ERROR_NOT_FOUND);
		return response;
	}

	/**
	 * 发消息
	 */
	public void response() {
		if (response != null) {
			session.write(response);
		} else {
			session.write(errResponseMessage());
		}
	}

	/**
	 * 返回状态消息
	 */
	public void responseWithStatus() {
		if (response != null) {
			if (response.bodyLength() < 1) {
                response.appendBody(response.getStatus().line());
			}
			session.write(response);
		} else {
			session.write(errResponseMessage());
		}
	}

	/**
	 * 获取字符串参数
	 *
	 * @param field a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public String getString(String field) {
		return getMessage().getParameter(field);
	}

	/**
	 * <p>getInt.</p>
	 *
	 * @param field a {@link java.lang.String} object.
	 * @return 0默认值
	 */
	public int getInt(String field) {
		String string = getString(field);
		if (string == null) {
			return 0;
		}
		return Integer.valueOf(string);
	}

	/**
	 * <p>getLong.</p>
	 *
	 * @param field a {@link java.lang.String} object.
	 * @return a long.
	 */
	public long getLong(String field) {
		String string = getString(field);
		if (string == null) {
			return 0;
		}
		return Long.valueOf(string);
	}

	/**
	 * <p>getDouble.</p>
	 *
	 * @param field a {@link java.lang.String} object.
	 * @return a double.
	 */
	public double getDouble(String field) {
		String string = getString(field);
		if (string == null) {
			return 0;
		}
		return Double.valueOf(string);
	}

	/**
	 * 发送消息
	 *
	 * @param object a {@link java.lang.Object} object.
	 */
	public void sendMsg(Object object) {
		getParameter().appendBody(JSON.toJSONString(object));
		response();
	}

}
