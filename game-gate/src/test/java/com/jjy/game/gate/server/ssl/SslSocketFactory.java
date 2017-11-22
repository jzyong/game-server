/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package com.jjy.game.gate.server.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;

import javax.net.SocketFactory;

/**
 * Simple Socket factory to create sockets with or without SSL enabled.
 * If SSL enabled a "bougus" SSL Context is used (suitable for test purposes)
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class SslSocketFactory extends SocketFactory {
    private static boolean sslEnabled = false;

    private static javax.net.ssl.SSLSocketFactory sslFactory = null;

    private static javax.net.SocketFactory factory = null;

    public SslSocketFactory() {
        super();
    }

    @Override
    public Socket createSocket(String arg1, int arg2) throws IOException,
            UnknownHostException {
        if (isSslEnabled()) {
            return getSSLFactory().createSocket(arg1, arg2);
        } else {
            return new Socket(arg1, arg2);
        }
    }

    @Override
    public Socket createSocket(String arg1, int arg2, InetAddress arg3, int arg4)
            throws IOException, UnknownHostException {
        if (isSslEnabled()) {
            return getSSLFactory().createSocket(arg1, arg2, arg3, arg4);
        } else {
            return new Socket(arg1, arg2, arg3, arg4);
        }
    }

    @Override
    public Socket createSocket(InetAddress arg1, int arg2) throws IOException {
        if (isSslEnabled()) {
            return getSSLFactory().createSocket(arg1, arg2);
        } else {
            return new Socket(arg1, arg2);
        }
    }

    @Override
    public Socket createSocket(InetAddress arg1, int arg2, InetAddress arg3,
            int arg4) throws IOException {
        if (isSslEnabled()) {
            return getSSLFactory().createSocket(arg1, arg2, arg3, arg4);
        } else {
            return new Socket(arg1, arg2, arg3, arg4);
        }
    }

    public static javax.net.SocketFactory getSocketFactory() {
        if (factory == null) {
            factory = new SslSocketFactory();
        }
        return factory;
    }

    private javax.net.ssl.SSLSocketFactory getSSLFactory() {
        if (sslFactory == null) {
            try {
                sslFactory = GateSslContextFactory.getInstance(false)
                        .getSocketFactory();
            } catch (Exception e) {
                throw new RuntimeException("could not create SSL socket", e);
            }
        }
        return sslFactory;
    }

    public static boolean isSslEnabled() {
        return sslEnabled;
    }

    public static void setSslEnabled(boolean newSslEnabled) {
        sslEnabled = newSslEnabled;
    }

}
