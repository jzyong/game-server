package com.jzy.game.model.mongo.bydr.entity;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import com.jzy.game.model.mongo.IConfigChecker;


/**
 * 房间配置
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年10月19日 上午11:42:31
 */
@Entity(value="c_room",noClassnameStored=true)
public class CRoom implements IConfigChecker {
	@Id
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
}
