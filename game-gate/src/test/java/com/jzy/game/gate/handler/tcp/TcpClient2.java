package com.jzy.game.gate.handler.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * 服务器接口测试工具类 每个服务器接口都需要在此进行请求、响应测试
 * 
 * @author Administrator
 * @date 2016年9月9日 下午1:58:15
 */
public class TcpClient2 {

	private Selector selector;

	SocketChannel socketChannel;

	private final String hostIp;

	private final int hostListenningPort;
	final String imei;

	public TcpClient2(String hostIp, int hostListenningPort) throws IOException {
		this(hostIp, hostListenningPort, "");
	}

	public TcpClient2(String HostIp, int HostListenningPort, String imei) throws IOException {
        hostIp = HostIp;
        hostListenningPort = HostListenningPort;
		this.imei = imei;
		initialize();
	}

	/**
	 * @throws IOException
	 */
	private void initialize() throws IOException {
		try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            selector = Selector.open();

            socketChannel.register(selector, SelectionKey.OP_CONNECT);

            socketChannel.connect(new InetSocketAddress(hostIp, hostListenningPort));

		} catch (Exception e) {

			e.printStackTrace();
		}

		new TCPClientReadThread(this, selector, imei);
	}

	public Selector getSelector() {
		return selector;
	}

	public void setSelector(Selector selector) {
		this.selector = selector;
	}


}
