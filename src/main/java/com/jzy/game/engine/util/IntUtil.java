package com.jzy.game.engine.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.apache.mina.core.buffer.IoBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * int 位标识
 *
 * @author JiangZhiYong
 * @date 2017-03-31 QQ:359135103
 */
public class IntUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntUtil.class);

    public static int setIntFlag(int src, int index) {
        return src | (1 << index);
    }

    public static int resetIntFlag(int src, int index) {
        return src & ~(1 << index);
    }

    public static boolean checkIntFlag(int src, int index) {
        return (src & (1 << index)) != 0;
    }

    public static int getIntCount(int src, int begin, int end) {
        int count = 0;
        for (int i = begin; i < end; i++) {
            if (checkIntFlag(src, i)) {
                count++;
            }
        }
        return count;
    }

    /**
     * byte数组 转换为字符串
     *
     * @param target
     * @return
     */
    public static String BytesToStr(byte[] target) {
        StringBuilder buf = new StringBuilder("[");
        for (int i = 0, j = target.length; i < j; i++) {
            buf.append(target[i]).append(",");
        }
        if (buf.length() > 2) {
            buf.setLength(buf.length() - 1);
        }
        buf.append("]");
        return buf.toString();
    }

    /**
     * 大端字节转换为short
     *
     * @param bytes
     * @return
     */
    public static short bigEndianByteToShort(byte[] bytes) {
        if (bytes.length != 2) {
            LOGGER.warn("字节数组长度{}大于2", bytes.length);
            return 0;
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getShort();
    }

    /**
     * 大端字节转换为short
     *
     * @param bytes
     * @return
     */
    public static short bigEndianByteToShort(byte[] bytes, int offset, int length) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getShort();
    }

    /**
     * 大端字节转换为short
     * @param ioBuffer
     * @param offset
     * @param length
     * @return 
     */
    public static short bigEndianByteToShort(IoBuffer ioBuffer, int offset, int length) {
        byte[] b=new byte[2];
        ioBuffer.get(b, offset, length);
        return bigEndianByteToShort(b, offset, length);
    }

    /**
     * 大端字节转换为short
     *
     * @param bytes
     * @return
     */
    public static int bigEndianByteToInt(byte[] bytes, int offset, int length) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getInt();
    }

    /**
     * 转大端byte数组
     *
     * @param x
     * @return
     */
    public static byte[] short2Bytes(short x) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putShort(x);
        return buffer.array();
    }

    /**
     * 转byte数组
     *
     * @param x
     * @param byteOrder 字节顺序类型
     * @return
     */
    public static byte[] short2Bytes(short x, ByteOrder byteOrder) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(byteOrder);
        buffer.putShort(x);
        return buffer.array();
    }

    /**
     * 将int转为低字节在前，高字节在后的byte数组
     */
    public static byte[] writeIntToBytesLittleEnding(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    public static short bytes2Short(byte[] src, ByteOrder byteOrder) {
        ByteBuffer buffer = ByteBuffer.wrap(src);
        buffer.order(byteOrder);
        return buffer.getShort();
    }

    public static int bytes2Int(byte[] src, ByteOrder byteOrder) {
        ByteBuffer buffer = ByteBuffer.wrap(src);
        buffer.order(byteOrder);
        return buffer.getInt();
    }
}
