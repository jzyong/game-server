package com.jzy.game.engine.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ExtraProcessor;
import com.alibaba.fastjson.parser.deserializer.ExtraTypeProvider;
import com.alibaba.fastjson.parser.deserializer.ParseProcess;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jzy.game.engine.struct.json.FieldMethod;

/**
 * json 工具类
 *
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月7日 上午11:22:15
 * @version $Id: $Id
 */
public final class JsonUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

	/** 缓存类对象的set get 方法 */
	private static final Map<Class<?>, Map<String, FieldMethod>> fieldGetMethodCache = new ConcurrentHashMap<>();

	private JsonUtil() {
	}

	/**
	 * map转换为对象
	 *
	 * @param map a {@link java.util.Map} object.
	 * @param object a {@link java.lang.Object} object.
	 */
	public static void map2Object(Map<String, String> map, Object object) {
		StringBuilder sb = new StringBuilder("{");
		try {
			Map<String, FieldMethod> fmmap = fieldGetMethodCache.get(object.getClass());
			if (fmmap == null) {
				registerFiledMethod(object.getClass());
				fmmap = fieldGetMethodCache.get(object.getClass());
			}
			if (fmmap == null) {
				throw new Exception("not register the class of " + object.getClass().getName());
			}
			FieldMethod fm = null;
			Iterator<Entry<String, String>> its = map.entrySet().iterator();
			String v = null;
			while (its.hasNext()) {
				Map.Entry<String, String> entry = its.next();
				try {
					v = entry.getValue();
					if (v == null || isEmpty(v) || !fmmap.containsKey(entry.getKey())) {
						continue;
					}
					fm = fmmap.get(entry.getKey());
					if (fm == null || fm.getField() == null) {
						return;
					}
					if (fm.getField().getType().isAssignableFrom(String.class)||fm.getField().getType().isEnum()) {	//字符串和枚举加上双引号
						sb.append("\"").append(entry.getKey()).append("\":\"").append(v).append("\",");
					} else {
						sb.append("\"").append(entry.getKey()).append("\":").append(v).append(",");
					}
				} catch (Exception e) {

				}
			}
		} catch (Exception e) {

		}
		if (sb.length() > 1) {
			sb.setLength(sb.length() - 1);
		}
		sb.append("}");
		ReflectUtil.reflectObject(sb.toString().trim(), object, new Feature[0]);
	}

	

	/**
	 *
	 * 注册属性方法
	 *
	 * @note 必须携带@JsonField
	 * @param cls a {@link java.lang.Class} object.
	 */
	public static void registerFiledMethod(Class<?> cls) {
		Map<String, FieldMethod> fmmap = getFieldMethodMap(cls, true);
		if (!fmmap.isEmpty()) {
			fieldGetMethodCache.put(cls, fmmap);
		}
	}

	/**
	 * 获取类对象属性map
	 *
	 * @param cls a {@link java.lang.Class} object.
	 * @param haveJSONField
	 *            是否注解 @JsonField
	 * @return a {@link java.util.Map} object.
	 */
	public static Map<String, FieldMethod> getFieldMethodMap(Class<?> cls, boolean haveJSONField) {
		Map<String, FieldMethod> fmmap = new ConcurrentHashMap<>();
		Method[] methods = cls.getDeclaredMethods();
		register(cls, methods, fmmap, haveJSONField);
		return fmmap;
	}

	/**
	 * 获取含有指定参数的方法
	 *
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年10月16日 下午5:25:23
	 * @param cls a {@link java.lang.Class} object.
	 * @param parameterClass 参数类，null加载所有方法
	 * @return a {@link java.util.Map} object.
	 */
	public static Map<String, Method> getFieldMethodMap(Class<?> cls, Class<?> parameterClass) {
		Map<String, Method> fmmap = new ConcurrentHashMap<>();
		Method[] methods = cls.getDeclaredMethods();
		register(cls, methods, fmmap, parameterClass);
		return fmmap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void register(Class<?> cls, Method[] methods, Map<String, Method> fmmap, Class<?> parameterClass) {
		for (Method method : methods) {
			if (Modifier.isStatic(method.getModifiers()) /*|| method.getParameters().length < 1*/) { // 非静态,非常量
				continue;
			}
			try {
				if (parameterClass==null||((Class) method.getParameters()[0].getParameterizedType()).isAssignableFrom(parameterClass)) {
					String abilityName = method.getName();
					fmmap.put(abilityName, method);
				}
			} catch (Exception ex) {

			}
		}
		Class<?> scls = cls.getSuperclass();
		if (scls != null) {
			register(scls, scls.getDeclaredMethods(), fmmap, parameterClass);
		}
	}

	/**
	 * 读取get set方法
	 * 
	 * @param cls
	 * @param methods
	 * @param fmmap
	 * @param haveJSONField
	 */
	private static void register(Class<?> cls, Method[] methods, Map<String, FieldMethod> fmmap,
			boolean haveJSONField) {
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			try {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				JSONField fieldAnnotation = field.getAnnotation(JSONField.class);
				if (haveJSONField && fieldAnnotation == null) {
					continue;
				}
				String fieldGetName = parGetName(field);
				String fieldSetName = ReflectUtil.parSetName(field);
				Method fieldGetMet = getGetMet(methods, fieldGetName);
				Method fieldSetMet = ReflectUtil.getSetMet(methods, fieldSetName);
				if (fieldGetMet != null && fieldSetMet != null) {
					FieldMethod fgm = new FieldMethod(fieldGetMet, fieldSetMet, field);
					fmmap.put(fgm.getName(), fgm);
				} else {
					System.out.println("找不到set或get方法 " + cls.getName() + " field:" + field.getName());
				}
			} catch (Exception e) {
				System.out.println("register field:" + cls.getName() + ":" + field.getName() + " " + e);
				continue;
			}
		}
		Class<?> scls = cls.getSuperclass();
		if (scls != null) {
			register(scls, scls.getDeclaredMethods(), fmmap, haveJSONField);
		}
	}

	/**
	 * 将对象值属性设置到map中
	 * 
	 * @param bean
	 * @param map
	 * @param fmmap
	 */
	private static void getField(Object bean, Map<String, String> map, Map<String, FieldMethod> fmmap) {
		if (bean == null || map == null) {
			return;
		}
		fmmap.values().forEach(fgm -> {
			try {
				Object fieldVal = getFieldValue(bean, fgm.getField(), fgm.getGetmethod());
				if (fieldVal == null) {
					return;
				}
				if (fieldVal.getClass().isAssignableFrom(String.class)) {
					map.put(fgm.getField().getName(), String.valueOf(fieldVal));
					return;
				}
				map.put(fgm.getField().getName(), JSON.toJSONString(fieldVal));
			} catch (Exception e) {

			}
		});
	}

	private static Object getFieldValue(Object bean, Field field, Method getmethod) {
		Object fieldVal = null;
		try {
			fieldVal = getmethod.invoke(bean, new Object[] {});
			return fieldVal;
		} catch (Exception e) {
			if (fieldVal == null) {
				try {
					fieldVal = field.get(bean);
				} catch (Exception ex) {

				}
			}
			return fieldVal;
		}
	}

	/**
	 * 判断是否存在某属性的 get方法
	 *
	 * @param methods an array of {@link java.lang.reflect.Method} objects.
	 * @param fieldGetMet a {@link java.lang.String} object.
	 * @return boolean
	 */
	public static Method getGetMet(Method[] methods, String fieldGetMet) {
		for (Method met : methods) {
			if (fieldGetMet.equals(met.getName())) {
				return met;
			}
		}
		return null;
	}

	/**
	 * 拼接某属性的 get方法
	 *
	 * @param field a {@link java.lang.reflect.Field} object.
	 * @return String
	 */
	public static String parGetName(Field field) {
		String fieldName = field.getName();
		if (null == fieldName || "".equals(fieldName)) {
			return null;
		}
		int startIndex = 0;
		boolean isbool = "boolean".equals(field.getGenericType().toString());
		return new StringBuilder(isbool ? "is" : "get")
				.append(fieldName.substring(startIndex, startIndex + 1).toUpperCase())
				.append(fieldName.substring(startIndex + 1)).toString();
	}

	

	/**
	 * 解析对象
	 *
	 * @param text a {@link java.lang.String} object.
	 * @param clazz a {@link java.lang.Class} object.
	 * @param <T> a T object.
	 * @return a T object.
	 */
	public static <T> T parseObject(String text, Class<T> clazz) {
		return JSON.parseObject(text, clazz);
	}

	/**
	 * 取Bean的属性和值对应关系的MAP
	 * <p>
	 * 必须有jsonField注解
	 * </p>
	 *
	 * @param bean a {@link java.lang.Object} object.
	 * @return a {@link java.util.Map} object.
	 */
	public static Map<String, String> object2Map(Object bean) {
		try {
			if (bean == null) {
				return null;
			}
			if (!fieldGetMethodCache.containsKey(bean.getClass())) {
				registerFiledMethod(bean.getClass());
			}
			Map<String, FieldMethod> fmmap = fieldGetMethodCache.get(bean.getClass());
			if (fmmap == null) {
				throw new Exception("not register the class of " + bean.getClass().getName());
			}
			Map<String, String> map = new HashMap<>();
			getField(bean, map, fmmap);
			return map;
		} catch (Exception e) {
			LOGGER.error("object2Map", e);
		}
		return new HashMap<>();

	}

	/**
	 * 对象转json
	 * <p>
	 * 需要jsonFiled注解
	 * </p>
	 *
	 * @param object a {@link java.lang.Object} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String toJSONString(Object object) {
		SerializeWriter out = new SerializeWriter();
		try {
			Map<String, Field> fieldCacheMap = new HashMap<>();
			ParserConfig.parserAllFieldToCache(object.getClass(), fieldCacheMap);
			JSONSerializer serializer = new JSONSerializer(out);
			PropertyFilter filter = (Object source, String name, Object value) -> {
				Field field = ParserConfig.getFieldFromCache(name, fieldCacheMap);
				if (field != null) {
					JSONField fieldAnnotation = field.getAnnotation(JSONField.class);
					if (fieldAnnotation != null) {
						return true;
					}
					return false;
				}
				return true;
			};
			serializer.getPropertyFilters().add(filter);
			serializer.write(object);
			return out.toString();
		} finally {
			out.close();
		}
	}
	
    /**
     *通过属性序列化,排除get方法，加类名
     *
     * @param paramObject a {@link java.lang.Object} object.
     * @return a {@link java.lang.String} object.
     */
    public static String toJSONStringWriteClassNameWithFiled(Object paramObject) {
        return JSON.toJSONString(paramObject, new SerializerFeature[]{SerializerFeature.WriteClassName, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.IgnoreNonFieldGetter});
    }
    
    /**
     * 通过属性序列化
     *
     * @author JiangZhiYong
     * @QQ 359135103
     * 2017年9月29日 下午7:33:09
     * @param object a {@link java.lang.Object} object.
     * @return a {@link java.lang.String} object.
     */
    public static String toJSONStringWithField(Object object) {
    	return JSON.toJSONString(object, new SerializerFeature[]{SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.IgnoreNonFieldGetter});
    }

	/**
	 * <p>isEmpty.</p>
	 *
	 * @param str a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public static boolean isEmpty(String str) {
		return str.isEmpty() || "\"\"".equals(str) || "\"".equals(str);
	}
}
