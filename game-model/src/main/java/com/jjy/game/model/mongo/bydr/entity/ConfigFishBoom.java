/**
* 工具自动生成,暂时不支持泛型和对象
* @author JiangZhiYong
* @date 2017-10-31
*/
package com.jjy.game.model.mongo.bydr.entity;

import java.util.List;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import com.jjy.game.model.mongo.IConfigChecker;

/**
 *
 * @date 2017-10-31
 */
@Entity(value = "config_fish_boom", noClassnameStored = true)
public class ConfigFishBoom implements IConfigChecker{

	@Id
	@Indexed
	/**编号*/
	private int id;
	/**阵型id*/
	private List formationIds;
	/**时间*/
	private List refreshTimes;
	/**线路id*/
	private List lineIds;
	/**存活时间*/
	private List surviveTimes;

	/**编号*/
	public int getId(){
		return this.id;
	}
	
	public void setId(int id){
		this.id=id;
	}
	/**阵型id*/
	public List getFormationIds(){
		return this.formationIds;
	}
	
	public void setFormationIds(List formationIds){
		this.formationIds=formationIds;
	}
	/**时间*/
	public List getRefreshTimes(){
		return this.refreshTimes;
	}
	
	public void setRefreshTimes(List refreshTimes){
		this.refreshTimes=refreshTimes;
	}
	/**线路id*/
	public List getLineIds(){
		return this.lineIds;
	}
	
	public void setLineIds(List lineIds){
		this.lineIds=lineIds;
	}
	/**存活时间*/
	public List getSurviveTimes(){
		return this.surviveTimes;
	}
	
	public void setSurviveTimes(List surviveTimes){
		this.surviveTimes=surviveTimes;
	}

}
