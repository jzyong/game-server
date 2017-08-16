package com.jzy.game.engine.struct.json;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 属性的set get方法对象封装
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年7月7日 上午11:42:55
 */
public class FieldMethod {

    private final Method setmethod;
    private final Method getmethod;
    private final Field field;

    public FieldMethod(Method getmethod, Method setmethod, Field field) {
        this.getmethod = getmethod;
        this.field = field;
        this.setmethod=setmethod;
    }
    
    public String getName(){
        return this.field==null?"null":this.field.getName();
    }


    public Method getGetmethod() {
        return getmethod;
    }

    public Field getField() {
        return field;
    }


    public Method getSetmethod() {
        return setmethod;
    }

}
