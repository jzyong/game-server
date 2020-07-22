/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jzy.game.engine.mina.code;

import java.nio.ByteOrder;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.util.CipherUtil;
import com.jzy.game.engine.util.IntUtil;
import com.jzy.game.engine.util.MsgUtil;

/**
 * 游戏客户端消息解码
 *
 * <p>
 * 包长度（2）+消息ID（4）+消息长度（4）+消息内容 <br>
 * 返回byte数组已去掉包长度
 * </p>
 * TODO 加解密
 *
 * @author JiangZhiYong
 * @QQ 359135103
 * @version $Id: $Id
 */
public class ClientProtocolDecoder extends ProtocolDecoderImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientProtocolDecoder.class);

	private static final String START_TIME = "start_time"; // 消息开始时间
	private static final String RECEIVE_COUNT = "receive_count";// 消息接收次数

	/** Constant <code>AES_KEY</code> */
	public static final byte[] AES_KEY = "vWf7g1Gt701h0.#0".getBytes();
	/** Constant <code>AES_IV</code> */
	public static final byte[] AES_IV = "rgnHV16#8HQFc&16".getBytes();

	private int maxCountPerSecond = 100; // 每秒钟最大接收消息数

	/**
	 * <p>Constructor for ClientProtocolDecoder.</p>
	 */
	public ClientProtocolDecoder() {
	}

	/** {@inheritDoc} */
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		int readAbleLen = in.remaining();
		if (readAbleLen < 2) {
			return false;
		}
		in.mark(); // 标记阅读位置
		byte[] bs = new byte[2];
		in.get(bs, 0, 2);
		short packageLength = IntUtil.bigEndianByteToShort(bs, 0, 2);
		if (packageLength < 1 || packageLength > maxReadSize) {
			LOGGER.warn("消息包长度为：{}", packageLength);
			in.clear();
			session.closeNow();
			return false;
		}

		if (in.remaining() < packageLength) { // 消息长度不够，重置位置
			in.reset();
			return false;
		}
		// 消息Id(4字节)+protobufLength(4字节)+消息体+时间戳(8字节)+签名数据长度(4字节)+签名数据+截取签名长度(4字节)
		bs = new byte[packageLength];
		in.get(bs);
		int protobufLength = IntUtil.bigEndianByteToInt(bs, 4, 4);
		if (packageLength > protobufLength + 8) {
			LOGGER.debug("消息签名验证");
			if (checkMsgSign(bs, protobufLength)) {
				byte[] datas = new byte[8 + protobufLength];
				System.arraycopy(bs, 0, datas, 0, datas.length);
				out.write(bs);
			} else {
				session.closeNow();
				return false;
			}
		} else {
			out.write(bs);
		}

		// decodeBytes(packageLength, in, out);

		if (!checkMsgFrequency(session)) {
			return false;
		}

		return true;
	}

	/**
	 * 检测签名<br>
	 * TODO 待测试 ,参考老项目 2017年10月16日 下午3:36:10
	 * 
	 * @param bytes
	 *            消息Id(4字节)+protobufLength(4字节)+消息体+时间戳(8字节)+签名数据长度(4字节)+签名数据+截取签名长度(4字节)
	 * @param protobufLength
	 */
	private boolean checkMsgSign(byte[] bytes, int protobufLength) throws Exception {
		// 客户端时间戳
		long timeStamp = IntUtil.bytes2Long(bytes, 8 + protobufLength, 8, ByteOrder.LITTLE_ENDIAN);
		// 计算签名
		String sign1 = calculateSign(bytes, timeStamp);
		// 解密签名数组
		int len_md5_data = IntUtil.bigEndianByteToInt(bytes, 16 + protobufLength, 4);
		byte[] bytesMd5 = new byte[len_md5_data];
		System.arraycopy(bytes, 20 + protobufLength, bytesMd5, 0, len_md5_data);
		bytesMd5 = decryptAES(bytesMd5);

		// 截取签名
		int len_clear_sign = IntUtil.bigEndianByteToInt(bytes, 20 + protobufLength + len_md5_data, 4);
		byte[] clearSignBytes = new byte[len_clear_sign];
		System.arraycopy(bytesMd5, 0, clearSignBytes, 0, len_clear_sign);
		String sign2 = new String(clearSignBytes, "utf-8");

		// 检查签名是否一致
		if (!sign1.equals(sign2)) {
			log.info("---------------------------签名验证失败!" + Arrays.toString(bytes));
			return false;
		}
		return true;
	}

	private String calculateSign(byte[] b, long timeStamp) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			int c = b[i];
			if (c < 0) {
				c += 256;
			}
			sb.append(c);
		}
		sb.append(timeStamp);
		return CipherUtil.MD5Encode(sb.toString()).toUpperCase();
	}

	/**
	 * AES解密
	 *
	 * @author codingtony 2017年5月23日下午6:14:09
	 * @param bytes an array of {@link byte} objects.
	 * @return an array of {@link byte} objects.
	 * @throws java.lang.Exception if any.
	 */
	public static byte[] decryptAES(byte[] bytes) throws Exception {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec keyspec = new SecretKeySpec(AES_KEY, "AES");
			IvParameterSpec ivspec = new IvParameterSpec(AES_IV);
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
			byte[] original = cipher.doFinal(bytes);
			return original;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 检测玩家消息发送频率
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年8月21日 下午1:47:58
	 * @param session
	 */
	private boolean checkMsgFrequency(IoSession session) {
		int count = 0;
		long startTime = 0L;
		if (session.containsAttribute(START_TIME)) {
			startTime = (long) session.getAttribute(START_TIME);
		}
		if (session.containsAttribute(RECEIVE_COUNT)) {
			count = (int) session.getAttribute(RECEIVE_COUNT);
		}
		long interval= session.getLastReadTime() - startTime;
		if (interval > 1000L) {
			if (count > getMaxCountPerSecond()) {
				MsgUtil.close(session, "%s %d--> %dms内消息过于频繁:%d,超过次数：%d", MsgUtil.getIp(session),session.getId(),interval, count,
						getMaxCountPerSecond());
				return false;
			}
			startTime = session.getLastReadTime();
			count = 0;
			session.setAttribute(START_TIME, startTime);
		}
		count++;
		session.setAttribute(RECEIVE_COUNT, count);
		return true;
	}

	/**
	 * <p>Getter for the field <code>maxCountPerSecond</code>.</p>
	 *
	 * @return a int.
	 */
	public int getMaxCountPerSecond() {
		return maxCountPerSecond;
	}

	/**
	 * <p>Setter for the field <code>maxCountPerSecond</code>.</p>
	 *
	 * @param maxCountPerSecond a int.
	 */
	public void setMaxCountPerSecond(int maxCountPerSecond) {
		this.maxCountPerSecond = maxCountPerSecond;
	}

}
