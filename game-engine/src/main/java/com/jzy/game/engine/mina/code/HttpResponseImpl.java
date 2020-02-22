package com.jzy.game.engine.mina.code;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.apache.mina.http.api.HttpResponse;
import org.apache.mina.http.api.HttpStatus;
import org.apache.mina.http.api.HttpVersion;

/**
 * http返回消息
 *
 * @author JiangZhiYong
 * @date 2017-03-31
 * QQ:359135103
 * @version $Id: $Id
 */
public class HttpResponseImpl implements HttpResponse {

    private static final Logger LOG = LoggerFactory.getLogger(HttpResponseImpl.class);

    private final HashMap<String, String> headers = new HashMap<>();		//头信息

    private final HttpVersion version = HttpVersion.HTTP_1_1;
    private HttpStatus status = HttpStatus.CLIENT_ERROR_FORBIDDEN;
    private final StringBuffer bodyStringBuffer;	//内容
    private byte[] body;	//内容

    /**
     * <p>Constructor for HttpResponseImpl.</p>
     */
    public HttpResponseImpl() {
        headers.put("Server", "HttpServer (" + "Mina 2.0.13" + ')');
        headers.put("Cache-Control", "private");
        headers.put("Content-Type", "text/html; charset=UTF-8");
        headers.put("Connection", "keep-alive");
        headers.put("Keep-Alive", "500");
        headers.put("Date", new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date()));
        headers.put("Last-Modified", new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date()));
        status = HttpStatus.SUCCESS_OK;
        bodyStringBuffer = new StringBuffer();
    }

    /**
     * <p>setContentType.</p>
     *
     * @param contentType a {@link java.lang.String} object.
     */
    public void setContentType(String contentType) {
        headers.put("Content-Type", contentType);
    }
    
    /**
     * 追加内容
     *
     * @param s a {@link java.lang.String} object.
     * @return a {@link com.jzy.game.engine.mina.code.HttpResponseImpl} object.
     */
    public HttpResponseImpl appendBody(String s) {
        bodyStringBuffer.append(s);
        return this;
    }
    
    /**
     * 内容长度
     *
     * @return a int.
     */
    public int bodyLength(){
        return bodyStringBuffer.length();
    }

    /**
     * <p>Getter for the field <code>body</code>.</p>
     *
     * @return an array of {@link byte} objects.
     */
    public byte[] getBody() {
        try {
            if (body == null) {
                body = bodyStringBuffer.toString().getBytes("utf-8");
            }
        } catch (IOException ex) {
            LOG.error("getBody", ex);
        }
        return body;
    }

    /** {@inheritDoc} */
    @Override
    public HttpVersion getProtocolVersion() {
        return version;
    }

    /** {@inheritDoc} */
    @Override
    public String getContentType() {
        return headers.get("Content-type");
    }

    /** {@inheritDoc} */
    @Override
    public boolean isKeepAlive() {
        // TODO check header and version for keep alive
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsHeader(String name) {
        return headers.containsKey(name);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    /** {@inheritDoc} */
    @Override
    public HttpStatus getStatus() {
        return status;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP RESPONSE STATUS: ").append(status).append('\n');
        sb.append("VERSION: ").append(version).append('\n');

        sb.append("-- HEADER --- \n");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(':').append(entry.getValue()).append('\n');
        }

        return sb.toString();
    }

    /**
     * <p>Setter for the field <code>status</code>.</p>
     *
     * @param status the status to set
     */
    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
