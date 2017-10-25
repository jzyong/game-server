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
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.GeneralSecurityException;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.util.AvailablePortFinder;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests echo server example.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public abstract class AbstractTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractTest.class);

    protected boolean useSSL;

    protected int port;

    protected SocketAddress boundAddress;

    protected IoAcceptor datagramAcceptor;

    protected IoAcceptor socketAcceptor;

    protected AbstractTest() {
        // Do nothing
    }

    protected static void isEquals(byte[] expected, byte[] actual) {
        assertEquals(toString(expected), toString(actual));
    }

    protected static void isEquals(IoBuffer expected, IoBuffer actual) {
        assertEquals(toString(expected), toString(actual));
    }

    protected static String toString(byte[] buf) {
        StringBuilder str = new StringBuilder(buf.length * 4);
        for (byte element : buf) {
            str.append(element);
            str.append(' ');
        }
        return str.toString();
    }

    protected static String toString(IoBuffer buf) {
        return buf.getHexDump();
    }

    @Before
    public void setUp() throws Exception {
        // Disable SSL by default
        useSSL = false;

        boundAddress = null;
        datagramAcceptor = new NioDatagramAcceptor();
        socketAcceptor = new NioSocketAcceptor();

        ((DatagramSessionConfig) datagramAcceptor.getSessionConfig())
                .setReuseAddress(true);
        ((NioSocketAcceptor) socketAcceptor).setReuseAddress(true);

        // Find an available test port and bind to it.
        boolean socketBound = false;
        boolean datagramBound = false;

        // Let's start from port #1 to detect possible resource leak
        // because test will fail in port 1-1023 if user run this test
        // as a normal user.

        SocketAddress address = null;

        // Find the first available port above 1024
        port = AvailablePortFinder.getNextAvailable(1024);

        socketBound = false;
        datagramBound = false;

        address = new InetSocketAddress(port);

        try {
            socketAcceptor.setHandler(new EchoProtocolHandler() {
                @Override
                public void sessionCreated(IoSession session) {
                    if (useSSL) {
                        try {
                            session.getFilterChain().addFirst(
                                    "SSL",
                                    new SslFilter(GateSslContextFactory
                                            .getInstance(true)));
                        } catch (Exception e) {
                            LOGGER.error("", e);
                            throw new RuntimeException(e);
                        }
                    }
                }

                // This is for TLS re-entrance test
                @Override
                public void messageReceived(IoSession session, Object message)
                        throws Exception {
                    if (!(message instanceof IoBuffer)) {
                        return;
                    }

                    IoBuffer buf = (IoBuffer) message;
                    
                    buf.mark();

                    if (session.getFilterChain().contains("SSL")
                            && buf.remaining() == 1 && buf.get() == (byte) '.') {
                        LOGGER.info("TLS Reentrance");
                        ((SslFilter) session.getFilterChain().get("SSL"))
                                .startSsl(session);

                        // Send a response
                        buf.capacity(1);
                        buf.flip();
                        session.setAttribute(SslFilter.DISABLE_ENCRYPTION_ONCE);
                        session.write(buf);
                    } else {
                        buf.reset();
                        super.messageReceived(session, buf);
                    }
                }
            });

            socketAcceptor.bind(address);
            socketBound = true;

            datagramAcceptor.setHandler(new EchoProtocolHandler());
            datagramAcceptor.bind(address);
            datagramBound = true;
        } catch (IOException e) {
            // Do nothing
        } finally {
            if (socketBound && !datagramBound) {
                socketAcceptor.unbind();
            }
            if (datagramBound && !socketBound) {
                datagramAcceptor.unbind();
            }
        }

        // If there is no port available, test fails.
        if (!socketBound || !datagramBound) {
            throw new IOException("Cannot bind any test port.");
        }

        boundAddress = address;
        LOGGER.info("Using port " + port + " for testing.");
    }

    @After
    public void tearDown() throws Exception {
        if (boundAddress != null) {
            socketAcceptor.dispose();
            datagramAcceptor.dispose();
        }
    }
}
