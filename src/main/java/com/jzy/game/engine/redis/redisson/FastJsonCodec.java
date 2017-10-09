package com.jzy.game.engine.redis.redisson;

import java.io.IOException;

import org.redisson.client.codec.Codec;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.util.JsonUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.CharsetUtil;

/**
 * 自定义fastjson序列化，为了兼容之前jedis序列化方式 <br>
 * 不同序列化工具，序列化字符串不一样 <br>
 * <p>
 * key：为字符串 value:为json字符串
 * </p>
 * TODO 待完善
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年9月29日 下午5:36:35
 */
public class FastJsonCodec implements Codec {
	private static final Logger LOGGER = LoggerFactory.getLogger(FastJsonCodec.class);
	private Class<?> keyClass;
	private Class<?> valueClass;

	public FastJsonCodec(Class<?> valueClass) {
		super();
		this.valueClass = valueClass;
	}

	/**
	 * 
	 * @param keyClazz
	 *            key对象类型
	 * @param valueClass
	 *            value对象类型
	 */
	public FastJsonCodec(Class<?> keyClass, Class<?> valueClass) {
		super();
		this.keyClass = keyClass;
		this.valueClass = valueClass;
	}

	public FastJsonCodec() {
		super();
	}

	private final Encoder encoder = new Encoder() {
		@Override
		public ByteBuf encode(Object in) throws IOException {
			ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
			out.writeCharSequence(JsonUtil.toJSONStringWriteClassNameWithFiled(in), CharsetUtil.UTF_8);
			return out;
		}
	};

	private final Decoder<Object> decoder = new Decoder<Object>() {
		@Override
		public Object decode(ByteBuf buf, State state) {
			String str = buf.toString(CharsetUtil.UTF_8);
			buf.readerIndex(buf.readableBytes());
			try {
				if (valueClass != null && str.startsWith("{")) {
					return JsonUtil.parseObject(str, valueClass);
				} else if (keyClass != null && !str.startsWith("{")) {
					if (keyClass.isAssignableFrom(Long.class)) {
						return Long.parseLong(str);
					} else if (keyClass.isAssignableFrom(Integer.class)) {
						return Integer.parseInt(str);
					}
				} else {
					return str;
				}
			} catch (Exception e) {
				LOGGER.error(String.format("反序列化%s失败", str), e);
			}
			return "";
		}
	};

	@Override
	public Decoder<Object> getMapValueDecoder() {
		return decoder;
	}

	@Override
	public Encoder getMapValueEncoder() {
		return encoder;
	}

	@Override
	public Decoder<Object> getMapKeyDecoder() {
		return decoder;
	}

	@Override
	public Encoder getMapKeyEncoder() {
		return encoder;
	}

	@Override
	public Decoder<Object> getValueDecoder() {
		return decoder;
	}

	@Override
	public Encoder getValueEncoder() {
		return encoder;
	}

}
