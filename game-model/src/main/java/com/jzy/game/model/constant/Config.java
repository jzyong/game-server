package com.jzy.game.model.constant;

import org.simpleframework.xml.Element;

/**
 * 全局常量
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年7月10日 下午3:06:44
 */
public class Config {
	private static final Object obj = new Object();
	/** ID计数器 */
	private static long id = 0;

	// =============常量 begin============

	/** 用户会话 */
	public static final String USER_SESSION = "userSession";
	/** 大厅最大消息ID */
	public static final int HALL_MAX_MID = 20000;
	/** 当前服务器ID */
	public static int SERVER_ID = 1;
	/**服务器名称*/
	public static String SERVER_NAME="";
	/** 服务器验证字符串 */
	public static final String SERVER_AUTH = "daa0cf5b-e72d-422c-b278-450b28a702c6";
	/**是否使用安全套接字*/
	public static final boolean USE_SSL = false; 

	// =============常量 end============

	// =============配置变量 begin============
	


	public static long getId() {
		synchronized (obj) {
			id += 1;
			return (((long) SERVER_ID) & 0xFFFF) << 48 | (System.currentTimeMillis() / 1000L & 0xFFFFFFFF) << 16
					| id & 0xFFFF;
		}
	}



	
	
	
}
