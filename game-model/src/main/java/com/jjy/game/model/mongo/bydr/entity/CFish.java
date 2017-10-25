package com.jjy.game.model.mongo.bydr.entity;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import com.jjy.game.model.mongo.IConfigChecker;

/**
 * 鱼配置信息
 *
 * @author JiangZhiYong
 * @date 2017-04-24 QQ:359135103
 */
@Entity(value = "c_fish", noClassnameStored = true)
public class CFish implements IConfigChecker{

	@Id
	@Indexed
	private int id;

	/** 名称 */
	private String name;
	/** 鱼的种类 */
	private int type;

	private int baseSpeed;



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	
	

	public int getBaseSpeed() {
		return baseSpeed;
	}

	public void setBaseSpeed(int baseSpeed) {
		this.baseSpeed = baseSpeed;
	}



	

}
