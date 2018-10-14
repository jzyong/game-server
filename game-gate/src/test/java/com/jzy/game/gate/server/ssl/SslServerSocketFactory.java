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
package com.jzy.game.gate.server.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.GeneralSecurityException;

import javax.net.ServerSocketFactory;
import com.jzy.game.gate.server.ssl.GateSslContextFactory;

/**
 * Simple Server Socket factory to create sockets with or without SSL enabled.
 * If SSL enabled a "bougus" SSL Context is used (suitable for test purposes)
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class SslServerSocketFactory extends ServerSocketFactory {
    private static boolean sslEnabled;

    private static ServerSocketFactory sslFactory;

    private static ServerSocketFactory factory;

    public SslServerSocketFactory() {
    }

    @Override
    public ServerSocket createServerSocket(int port) throws IOException {
        return new ServerSocket(port);
    }

    @Override
    public ServerSocket createServerSocket(int port, int backlog)
            throws IOException {
        return new ServerSocket(port, backlog);
    }

    @Override
    public ServerSocket createServerSocket(int port, int backlog,
            InetAddress ifAddress) throws IOException {
        return new ServerSocket(port, backlog, ifAddress);
    }

    public static ServerSocketFactory getServerSocketFactory()
            throws IOException {
        if (isSslEnabled()) {
            if (sslFactory == null) {
                try {
                    sslFactory = GateSslContextFactory.getInstance(true)
                            .getServerSocketFactory();
                } catch (Exception e) {
                    IOException ioe = new IOException(
                            "could not create SSL socket");
                    ioe.initCause(e);
                    throw ioe;
                }
            }
            return sslFactory;
        } else {
            if (factory == null) {
                factory = new SslServerSocketFactory();
            }
            return factory;
        }

    }

    public static boolean isSslEnabled() {
        return sslEnabled;
    }

    public static void setSslEnabled(boolean newSslEnabled) {
        sslEnabled = newSslEnabled;
    }
}
