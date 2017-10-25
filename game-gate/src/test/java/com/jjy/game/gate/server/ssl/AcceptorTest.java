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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import org.junit.Test;

/**
 * Tests echo server example.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class AcceptorTest extends AbstractTest {
	public AcceptorTest() {
	}

	@Test
	public void testTCP() throws Exception {
		testTCP0(new Socket("127.0.0.1", port));
	}

	@Test
	public void testTCPWithSSL() throws Exception {
		// Add an SSL filter
		useSSL = true;

		// Create a echo client with SSL factory and test it.
		SslSocketFactory.setSslEnabled(true);
		SslServerSocketFactory.setSslEnabled(true);
		testTCP0(SslSocketFactory.getSocketFactory().createSocket("localhost", port));
//		testTCP0(SslSocketFactory.getSocketFactory().createSocket("localhost", 8002));
	}

	private void testTCP0(Socket client) throws Exception {
		client.setSoTimeout(3000);
		byte[] writeBuf = new byte[16];

		for (int i = 0; i < 10; i++) {
			fillWriteBuffer(writeBuf, i);
			client.getOutputStream().write(writeBuf);
		}

		byte[] readBuf = new byte[writeBuf.length];

		for (int i = 0; i < 10; i++) {
			fillWriteBuffer(writeBuf, i);

			int readBytes = 0;
			while (readBytes < readBuf.length) {
				int nBytes = client.getInputStream().read(readBuf, readBytes, readBuf.length - readBytes);

				if (nBytes < 0) {
					fail("Unexpected disconnection.");
				}

				readBytes += nBytes;
			}

			assertTrue(Arrays.equals(writeBuf, readBuf));
		}

		client.setSoTimeout(500);

		try {
			client.getInputStream().read();
			fail("Unexpected incoming data.");
		} catch (SocketTimeoutException e) {
		}

		client.getInputStream().close();
		client.close();
	}

	public void testUDP() throws Exception {
		DatagramSocket client = new DatagramSocket();
		client.connect(new InetSocketAddress("127.0.0.1", port));
		client.setSoTimeout(500);

		byte[] writeBuf = new byte[16];
		byte[] readBuf = new byte[writeBuf.length];
		DatagramPacket wp = new DatagramPacket(writeBuf, writeBuf.length);
		DatagramPacket rp = new DatagramPacket(readBuf, readBuf.length);

		for (int i = 0; i < 10; i++) {
			fillWriteBuffer(writeBuf, i);
			client.send(wp);

			client.receive(rp);
			assertEquals(writeBuf.length, rp.getLength());
			assertTrue(Arrays.equals(writeBuf, readBuf));
		}

		try {
			client.receive(rp);
			fail("Unexpected incoming data.");
		} catch (SocketTimeoutException e) {
		}

		client.close();
	}

	private void fillWriteBuffer(byte[] writeBuf, int i) {
		for (int j = writeBuf.length - 1; j >= 0; j--) {
			writeBuf[j] = (byte) (j + i);
		}
	}
}
