package com.jzy.game.engine.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ExtraProcessor;
import com.alibaba.fastjson.parser.deserializer.ExtraTypeProvider;
import com.alibaba.fastjson.parser.deserializer.ParseProcess;

/**
 * 反射工具
 *
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 * @phone 18782024395
 */
public final class ReflectUtil {

	private static final Logger log = LoggerFactory.getLogger(ReflectUtil.class);

	private ReflectUtil() {
	}

	/**
	 * 循环向上转型, 获取对象的 DeclaredField
	 *
	 * @param object
	 *            : 子类对象
	 * @param fieldName
	 *            : 父类中的属性名
	 * @return 父类中的属性对象
	 */
	public static Field getDeclaredField(Object object, String fieldName) {
		Field field = null;
		Class<?> clazz = object.getClass();
		for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
			try {
				field = clazz.getDeclaredField(fieldName);
				return field;
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 获取所有属性，包括父类
	 * 
	 * @return
	 */
	public static List<Field> getDeclaredFields(Object object) {
		List<Field> fields = new ArrayList<>();
		Class<?> clazz = object.getClass();
		for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
			try {
				Field[] declaredFields = clazz.getDeclaredFields();
				fields.addAll(Arrays.asList(declaredFields));
			} catch (Exception e) {
			}
		}
		return fields;
	}

	/**
	 * 获取类的setter方法
	 * 
	 * @return
	 */
	public static Map<String, Method> getWriteMethod(Class<?> clazz) {
		Map<String, Method> getMethods = new ConcurrentHashMap<>(10);
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();
				// 过滤class属性
				if (!"class".equals(key)) {
					// 得到property对应的getter方法
					Method write = property.getWriteMethod();
					if (write != null) {
						getMethods.put(key, write);
					}
				}
			}
		} catch (Exception e) {
			log.error("ReflectError", e);
		}
		return getMethods;
	}

	/**
	 * 获取类的getter方法
	 * 
	 * @return
	 */
	public static Map<String, Method> getReadMethod(Class<?> clazz) {
		Map<String, Method> getMethods = new ConcurrentHashMap<>(10);
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();
				// 过滤class属性
				if (!"class".equals(key)) {
					// 得到property对应的getter方法
					Method write = property.getReadMethod();
					if (write != null) {
						getMethods.put(key, write);
					}
				}
			}
		} catch (Exception e) {
			log.error("ReflectError", e);
		}
		return getMethods;
	}

	/**
	 * 拼接在某属性的 set方法
	 *
	 * @param field
	 * @return String
	 */
	public static String parSetName(Field field) {
		String fieldName = field.getName();
		if (null == fieldName || "".equals(fieldName)) {
			return null;
		}
		int startIndex = 0;
		return new StringBuilder("set").append(fieldName.substring(startIndex, startIndex + 1).toUpperCase())
				.append(fieldName.substring(startIndex + 1)).toString();
	}

	/**
	 * 判断是否存在某属性的 set方法
	 *
	 * @param methods
	 * @param fieldSetMet
	 * @return boolean
	 */
	public static Method getSetMet(Method[] methods, String fieldSetMet) {
		for (Method met : methods) {
			if (fieldSetMet.equals(met.getName())) {
				return met;
			}
		}
		return null;
	}

	/**
	 *
	 * @param text
	 * @param value
	 */
	public static void reflectObject(String text, Object value) {
		reflectObject(text, value, new Feature[0]);
	}

	/**
	 *
	 * @param text
	 * @param value
	 * @param features
	 */
	public static void reflectObject(String text, Object value, Feature... features) {
		reflectObject(text, value, ParserConfig.getGlobalInstance(), JSON.DEFAULT_PARSER_FEATURE, features);
	}

	/**
	 *
	 * @param input
	 * @param value
	 * @param config
	 * @param featureValues
	 * @param features
	 */
	private static void reflectObject(String input, Object value, ParserConfig config, int featureValues,
			Feature... features) {
		reflectObject(input, value, config, null, featureValues, features);
	}

	/**
	 * wzy扩展
	 *
	 * @param input
	 * @param value
	 * @param config
	 * @param processor
	 * @param featureValues
	 * @param features
	 */
	private static void reflectObject(String input, Object value, ParserConfig config, ParseProcess processor,
			int featureValues, Feature... features) {
		if (input == null) {
			return;
		}
		for (Feature featrue : features) {
			featureValues = Feature.config(featureValues, featrue, true);
		}

		DefaultJSONParser parser = new DefaultJSONParser(input, config, featureValues);

		if (processor instanceof ExtraTypeProvider) {
			parser.getExtraTypeProviders().add((ExtraTypeProvider) processor);
		}

		if (processor instanceof ExtraProcessor) {
			parser.getExtraProcessors().add((ExtraProcessor) processor);
		}
		parser.parseObject(value);
		parser.handleResovleTask(value);
		parser.close();
	}

	public static byte[] toBytes(Serializable obj) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		try {
			oos.writeObject(obj);
			oos.flush();
			return baos.toByteArray();
		} finally {
			oos.close();
			baos.close();
		}
	}

	public static Object getObject(byte[] buf) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);
		ObjectInputStream ois = new ObjectInputStream(bais);
		try {
			return ois.readObject();
		} finally {
			ois.close();
			bais.close();
		}
	}

	/**
	 * 深拷贝
	 * 
	 * @param obj
	 * @return
	 */
	public static Object deepCopy(Serializable obj) {
		try {
			return getObject(toBytes(obj));
		} catch (Exception e) {
			log.error("深拷贝", e);
		}
		return null;
	}

}
