package com.jzy.game.plugin.model;

import com.jzy.game.engine.util.StringUtil;

/**
 * 对象模型
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年10月31日 上午10:47:59
 */
public class FieldModel {
	/** 属性类型 */
	private String fieldType;
	/** 属性名称 */
	private String fieldName;
	/** 属性名称首字母大写 */
	private String fieldNameUpFirst;
	/** 描述 */
	private String description;
	

	public FieldModel(String fieldType, String fieldName, String description) {
		super();
		this.fieldType = fieldType;
		this.fieldName = fieldName;
		this.description = description;
		this.fieldNameUpFirst=StringUtil.upFirstChar(fieldName);
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFieldNameUpFirst() {
		return fieldNameUpFirst;
	}

	public void setFieldNameUpFirst(String fieldNameUpFirst) {
		this.fieldNameUpFirst = fieldNameUpFirst;
	}

}
