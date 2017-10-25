package com.jjy.game.tool.client;

import com.google.protobuf.Message;
import javax.swing.JTextArea;
import org.apache.mina.core.session.IoSession;

/**
 * 玩家
 *
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月1日 下午4:16:38
 */
public class Player {

	public static final String PLAYER = "player";

	private long id;
	private String userName;
	private String password;
	private IoSession tcpSession;
	private IoSession udpSession;
	private JTextArea logTextArea;
	private boolean udpLogin; // udp是否登录

	/**
	 * 登录初始化
	 */
	public void loginInit() {
		getTcpSession().setAttribute(PLAYER, this);
		getUdpSession().setAttribute(PLAYER, this);
	}

	public void sendTcpMsg(Message msg) {
		getTcpSession().write(msg);
	}

	/**
	 * 发送udp
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年10月20日 下午1:37:53
	 * @param msg
	 */
	public void sendUdpMsg(Message msg) {
		if (!udpLogin) {
			sendTcpMsg(msg);
			return;
		}
		getUdpSession().write(msg);
	}

	/**
	 * 显示日志
	 *
	 * @param info
	 */
	public void showLog(String info) {
		if (logTextArea != null) {
			logTextArea.append(info + "\n");
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public IoSession getTcpSession() {
		return tcpSession;
	}

	public void setTcpSession(IoSession tcpSession) {
		this.tcpSession = tcpSession;
	}

	public IoSession getUdpSession() {
		return udpSession;
	}

	public void setUdpSession(IoSession udpSession) {
		this.udpSession = udpSession;
	}

	public JTextArea getLogTextArea() {
		return logTextArea;
	}

	public void setLogTextArea(JTextArea logTextArea) {
		this.logTextArea = logTextArea;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isUdpLogin() {
		return udpLogin;
	}

	public void setUdpLogin(boolean udpLogin) {
		this.udpLogin = udpLogin;
	}

}
