package com.jzy.game.model.struct;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import com.jzy.game.model.constant.Config;

/**
 * 公会
 * <p>直接操作mongodb数据库，不缓存</p>
 * @author JiangZhiYong
 * @QQ 359135103
 * @Date 2017/9/18 0018
 */
@Entity
public class Guild {
	
	@Id
	@Indexed
    private long id;
	
	/**名称*/
	private String name;
	
	/**帮主ID*/
	private long masterId;
	
	/**公会成员*/
	@Embedded
	private Set<Long> members=new HashSet<>(50);
	/**申请列表*/
	@Embedded
	private Set<Long> applys=new HashSet<>();
	/**创建时间*/
	private Date createTime;

	
	
    public Guild() {
        id =Config.getId();
	}

	public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getMasterId() {
		return masterId;
	}

	public void setMasterId(long masterId) {
		this.masterId = masterId;
	}

	public Set<Long> getMembers() {
		return members;
	}

	public void setMembers(Set<Long> members) {
		this.members = members;
	}

	public Set<Long> getApplys() {
		return applys;
	}

	public void setApplys(Set<Long> applys) {
		this.applys = applys;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
    
    
}
