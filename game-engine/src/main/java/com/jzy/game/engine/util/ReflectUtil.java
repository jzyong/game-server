package com.jzy.game.engine.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
//import org.apache.commons.beanutils.BeanUtils;
//import org.apache.commons.beanutils.ConvertUtils;
//import org.apache.commons.beanutils.converters.ByteConverter;
//import org.apache.commons.beanutils.converters.DoubleConverter;
//import org.apache.commons.beanutils.converters.FloatConverter;
//import org.apache.commons.beanutils.converters.IntegerConverter;
//import org.apache.commons.beanutils.converters.LongConverter;
//import org.apache.commons.beanutils.converters.ShortConverter;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ExtraProcessor;
import com.alibaba.fastjson.parser.deserializer.ExtraTypeProvider;
import com.alibaba.fastjson.parser.deserializer.ParseProcess;

import org.slf4j.Logger;

/**
 * 反射工具
 *
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 * @phone 18782024395
 */
public class ReflectUtil {

    private static final Logger log = LoggerFactory.getLogger(ReflectUtil.class);





    /**
     * 循环向上转型, 获取对象的 DeclaredField
     *
     * @param object : 子类对象
     * @param fieldName : 父类中的属性名
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
     *获取类的setter方法
     * @return 
     */
    public static Map<String, Method> getWriteMethod(Class clazz) {
        Map<String, Method> getMethods = new ConcurrentHashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                // 过滤class属性  
                if (!key.equals("class")) {
                    // 得到property对应的getter方法  
                    Method write = property.getWriteMethod();
                    if(write!=null){
                        getMethods.put(key, write);
                    }
                }
            }
        } catch (Exception e) {
            log.error("ReflectError",e);
        }
        return getMethods;
    }
    
    /**
     *获取类的getter方法
     * @return 
     */
     public static Map<String, Method> getReadMethod(Class clazz) {
        Map<String, Method> getMethods = new ConcurrentHashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                // 过滤class属性  
                if (!key.equals("class")) {
                    // 得到property对应的getter方法  
                    Method write = property.getReadMethod();
                    if(write!=null){
                        getMethods.put(key, write);
                    }
                }
            }
        } catch (Exception e) {
            log.error("ReflectError",e);
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
	 * wzy扩展
	 *
	 * @param text
	 * @param value
	 */
	public static final void reflectObject(String text, Object value) {
		reflectObject(text, value, new Feature[0]);
	}

	/**
	 * wzy扩展
	 *
	 * @param text
	 * @param value
	 * @param features
	 */
	public static final void reflectObject(String text, Object value, Feature... features) {
		reflectObject(text, value, ParserConfig.getGlobalInstance(), JSON.DEFAULT_PARSER_FEATURE, features);
	}

	/**
	 * wzy扩展
	 *
	 * @param input
	 * @param value
	 * @param config
	 * @param featureValues
	 * @param features
	 */
	private static final void reflectObject(String input, Object value, ParserConfig config, int featureValues,
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
	private static final void reflectObject(String input, Object value, ParserConfig config, ParseProcess processor,
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
    
}
