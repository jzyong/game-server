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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.InetSocketAddress;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteException;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.apache.mina.util.AvailablePortFinder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.gate.server.ssl.GateSslContextFactory;

/**
 * Tests echo server example.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class ConnectorTest extends AbstractTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorTest.class);

    private static final int TIMEOUT = 10000; // 10 seconds

    private final int COUNT = 10;

    private final int DATA_SIZE = 16;

    private EchoConnectorHandler handler;
    private SslFilter connectorSSLFilter;

    public ConnectorTest() {
        // Do nothing
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        handler = new EchoConnectorHandler();
        connectorSSLFilter = new SslFilter(GateSslContextFactory
                .getInstance(false));
        connectorSSLFilter.setUseClientMode(true); // set client mode
    }

    @Test
    public void testTCP() throws Exception {
        IoConnector connector = new NioSocketConnector();
        testConnector(connector);
    }

    @Test
    public void testTCPWithSSL() throws Exception {
        useSSL = true;
        // Create a connector
        IoConnector connector = new NioSocketConnector();

        // Add an SSL filter to connector
        connector.getFilterChain().addLast("SSL", connectorSSLFilter);
        testConnector(connector);
    }

    @Test
    public void testUDP() throws Exception {
        IoConnector connector = new NioDatagramConnector();
        testConnector(connector);
    }

    private void testConnector(IoConnector connector) throws Exception {
        connector.setHandler(handler);

        //System.out.println("* Without localAddress");
        testConnector(connector, false);

        //System.out.println("* With localAddress");
        testConnector(connector, true);
    }

    private void testConnector(IoConnector connector, boolean useLocalAddress)
            throws Exception {
        IoSession session = null;
        if (!useLocalAddress) {
            ConnectFuture future = connector.connect(new InetSocketAddress(
                    "127.0.0.1", port));
            future.awaitUninterruptibly();
            session = future.getSession();
        } else {
            int clientPort = AvailablePortFinder.getNextAvailable();
            ConnectFuture future = connector.connect(
                    new InetSocketAddress("127.0.0.1", port),
                    new InetSocketAddress(clientPort));
            future.awaitUninterruptibly();
            session = future.getSession();

            if (session == null) {
                fail("Failed to find out an appropriate local address.");
            }
        }

        // Run a basic connector test.
        testConnector0(session);

        // Send closeNotify to test TLS closure if it is TLS connection.
        if (useSSL) {
            connectorSSLFilter.stopSsl(session).awaitUninterruptibly();

            System.out
                    .println("-------------------------------------------------------------------------------");
            // Test again after we finished TLS session.
            testConnector0(session);

            System.out
                    .println("-------------------------------------------------------------------------------");

            // Test if we can enter TLS mode again.
            //// Send StartTLS request.
            handler.readBuf.clear();
            IoBuffer buf = IoBuffer.allocate(1);
            buf.put((byte) '.');
            buf.flip();
            session.write(buf).awaitUninterruptibly();

            //// Wait for StartTLS response.
            waitForResponse(handler, 1);

            handler.readBuf.flip();
            assertEquals(1, handler.readBuf.remaining());
            assertEquals((byte) '.', handler.readBuf.get());

            // Now start TLS connection
            assertTrue(connectorSSLFilter.startSsl(session));
            testConnector0(session);
        }

        session.closeNow().awaitUninterruptibly();
    }

    private void testConnector0(IoSession session) throws InterruptedException {
        EchoConnectorHandler handler = (EchoConnectorHandler) session
                .getHandler();
        IoBuffer readBuf = handler.readBuf;
        readBuf.clear();
        WriteFuture writeFuture = null;
        
        for (int i = 0; i < COUNT; i++) {
            IoBuffer buf = IoBuffer.allocate(DATA_SIZE);
            buf.limit(DATA_SIZE);
            fillWriteBuffer(buf, i);
            buf.flip();

            writeFuture = session.write(buf);

            if (session.getService().getTransportMetadata().isConnectionless()) {
                // This will align message arrival order in connectionless transport types
                waitForResponse(handler, (i + 1) * DATA_SIZE);
            }
        }

        writeFuture.awaitUninterruptibly();

        waitForResponse(handler, DATA_SIZE * COUNT);

        // Assert data
        //// Please note that BufferOverflowException can be thrown
        //// in SocketIoProcessor if there was a read timeout because
        //// we share readBuf.
        readBuf.flip();
        LOGGER.info("readBuf: " + readBuf);
        assertEquals(DATA_SIZE * COUNT, readBuf.remaining());
        IoBuffer expectedBuf = IoBuffer.allocate(DATA_SIZE * COUNT);
        
        for (int i = 0; i < COUNT; i++) {
            expectedBuf.limit((i + 1) * DATA_SIZE);
            fillWriteBuffer(expectedBuf, i);
        }
        
        expectedBuf.position(0);

        isEquals(expectedBuf, readBuf);
    }

    private void waitForResponse(EchoConnectorHandler handler, int bytes)
            throws InterruptedException {
        for (int j = 0; j < TIMEOUT / 10; j++) {
            if (handler.readBuf.position() >= bytes) {
                break;
            }
            Thread.sleep(10);
        }

        assertEquals(bytes, handler.readBuf.position());
    }

    private void fillWriteBuffer(IoBuffer writeBuf, int i) {
        while (writeBuf.remaining() > 0) {
            writeBuf.put((byte) i++);
        }
    }

    private static class EchoConnectorHandler extends IoHandlerAdapter {
        private final IoBuffer readBuf = IoBuffer.allocate(1024);

        private EchoConnectorHandler() {
            readBuf.setAutoExpand(true);
        }

        @Override
        public void messageReceived(IoSession session, Object message) {
            readBuf.put((IoBuffer) message);
        }

        @Override
        public void messageSent(IoSession session, Object message) {
        }

        @Override
        public void exceptionCaught(IoSession session, Throwable cause) {
            LOGGER.warn("Unexpected exception.", cause);
            if (cause instanceof WriteException) {
                WriteException e = (WriteException) cause;
                LOGGER.warn("Failed write requests: {}", e.getRequests());
            }
        }
    }
}
