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
 */
public abstract class HttpHandler implements IHandler {

	private HttpResponseImpl response; // 返回消息

	private IoSession session; // 消息来源
	private HttpRequestImpl request; // 请求消息

	private long createTime;

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public IoSession getSession() {
		return session;
	}

	@Override
	public void setSession(IoSession session) {
		this.session = session;
	}

	@Override
	public HttpRequestImpl getMessage() { // HttpRequestImpl
		return (HttpRequestImpl) request;
	}

	@Override
	public void setMessage(Object message) {
		if (message instanceof HttpRequestImpl) {
            request = (HttpRequestImpl) message;
		}
	}

	/**
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
	 * 返回消息
	 */
	@Override
	public void setParameter(Object parameter) {
		// if (parameter instanceof HttpResponseMessage) {
        response = (HttpResponseImpl) parameter;
		// }
	}

	@Override
	public long getCreateTime() {
		return createTime;
	}

	@Override
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	/**
	 * 没有返回值的情况下处理
	 *
	 * @return
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
	 * @param field
	 * @return
	 */
	public String getString(String field) {
		return getMessage().getParameter(field);
	}

	/**
	 * 
	 * @param field
	 * @return 0默认值
	 */
	public int getInt(String field) {
		String string = getString(field);
		if (string == null) {
			return 0;
		}
		return Integer.valueOf(string);
	}

	public long getLong(String field) {
		String string = getString(field);
		if (string == null) {
			return 0;
		}
		return Long.valueOf(string);
	}

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
	 * @param object
	 */
	public void sendMsg(Object object) {
		getParameter().appendBody(JSON.toJSONString(object));
		response();
	}

}
