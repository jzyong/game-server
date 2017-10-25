package com.jjy.game.hall.util;

import java.util.Date;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.jjy.game.model.struct.Item;
import com.jzy.game.engine.util.JsonUtil;

/**
 * json
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年9月29日 下午5:26:13
 */
public class JsonUtilTest {

	/**
	 * 测试jackson和fastjson格式
	 * @author JiangZhiYong
	 * @QQ 359135103
	 * 2017年9月29日 下午5:28:29
	 */
	@Test
	public void testJacksonAndFastJson() {
		//jackson {"@class":"com.jjy.game.model.struct.Item","configId":1,"createTime":["java.util.Date",1506677002383],"id":380216560713729,"num":20}
		Item item = new Item();
		item.setConfigId(1);
		item.setNum(20);
		item.setCreateTime(new Date());
		String filed = JsonUtil.toJSONStringWriteClassNameWithFiled(item);
		String jsonString = JSON.toJSONString(item);
		System.err.println(jsonString);
		System.err.println(filed);
	}
}
