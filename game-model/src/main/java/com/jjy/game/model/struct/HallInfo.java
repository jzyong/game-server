/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jjy.game.model.struct;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * 全局大厅服务器信息
 *
 * @author JiangZhiYong
 * @QQ 359135103
 */
@Entity(value = "hall_info", noClassnameStored = true)
public class HallInfo {
    @Id
    private int id=1;
    /**用户ID编号*/
    private long userIdNum;
     /**角色ID编号*/
    private long roleIdNum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUserIdNum() {
        return userIdNum;
    }

    public void setUserIdNum(long userIdNum) {
        this.userIdNum = userIdNum;
    }

    public long getRoleIdNum() {
        return roleIdNum;
    }

    public void setRoleIdNum(long roleIdNum) {
        this.roleIdNum = roleIdNum;
    }
    
    
}
