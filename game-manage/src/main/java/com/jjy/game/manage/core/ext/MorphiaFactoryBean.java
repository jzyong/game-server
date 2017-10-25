package com.jjy.game.manage.core.ext;

import org.mongodb.morphia.Morphia;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * mongodb Morphia 工厂
 *
 * @author JiangZhiYong
 * @date 2017-05-22
 * QQ:359135103
 */
public class MorphiaFactoryBean extends AbstractFactoryBean<Morphia> {
	/**
	 * 要扫描并映射的包
	 */
	private String[] mapPackages;

	/**
	 * 要映射的类
	 */
	private String[] mapClasses;

	/**
	 * 扫描包时，是否忽略不映射的类 这里按照Morphia的原始定义，默认设为false
	 */
	private boolean ignoreInvalidClasses;

	@Override
	protected Morphia createInstance() throws Exception {
		Morphia m = new Morphia();
		if (mapPackages != null) {
			for (String packageName : mapPackages) {
				m.mapPackage(packageName, ignoreInvalidClasses);
			}
		}
		if (mapClasses != null) {
			for (String entityClass : mapClasses) {
				m.map(Class.forName(entityClass));
			}
		}
		return m;
	}

	@Override
	public Class<?> getObjectType() {
		return Morphia.class;
	}

	public String[] getMapPackages() {
		return mapPackages;
	}

	public void setMapPackages(String[] mapPackages) {
		this.mapPackages = mapPackages;
	}

	public String[] getMapClasses() {
		return mapClasses;
	}

	public void setMapClasses(String[] mapClasses) {
		this.mapClasses = mapClasses;
	}

	public boolean isIgnoreInvalidClasses() {
		return ignoreInvalidClasses;
	}

	public void setIgnoreInvalidClasses(boolean ignoreInvalidClasses) {
		this.ignoreInvalidClasses = ignoreInvalidClasses;
	}

	/*----------------------setters-----------------------*/
}