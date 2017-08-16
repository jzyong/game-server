package com.jzy.game.engine.script;

import java.io.File;
import java.util.function.Consumer;

import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.IHandler;

/**
 * 脚本管理
 *
 * @author JiangZhiYong
 * @date 2017-03-30 QQ:359135103
 */
public class ScriptManager {

	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(ScriptManager.class);
	private static final ScriptManager instance = new ScriptManager();
	private static final ScriptPool scriptPool; // 基础脚本类

	// 静态初始化
	static {
		scriptPool = new ScriptPool();
		try {
			String property = System.getProperty("user.dir");
			String path = property + "-scripts" + File.separator + "src" + File.separator + "main" + File.separator // 脚本路径
					+ "java" + File.separator;
			String outpath = property + File.separator + "target" + File.separator + "scriptsbin" + File.separator; // class类编译路径
			String jarsDir = property + File.separator + "target" + File.separator; // jar包路径
			scriptPool.setSource(path, outpath, jarsDir);
		} catch (Exception ex) {
		}
	}

	public static ScriptManager getInstance() {
		return instance;
	}

	public ScriptPool getBaseScriptEntry() {
		return scriptPool;
	}

	/**
	 * 初始化脚本
	 * 
	 * @param result
	 *            加载输出结果字符串
	 */
	public String init(Consumer<String> result) {
		return scriptPool.loadJava(result);
	}

	/**
	 * 加载指定实例，可以是文件也可以是目录
	 *
	 * @param source
	 * @return
	 */
	public String loadJava(String... source) {
		return scriptPool.loadJava(source);
	}

	/**
	 * 获取消息处理器
	 * 
	 * @param mid
	 *            消息ID
	 * @return
	 */
	public Class<? extends IHandler> getTcpHandler(int mid) {
		return scriptPool.getHandlerMap().get(mid);
	}

	/**
	 * 获取handler配置
	 * 
	 * @param mid
	 * @return
	 */
	public HandlerEntity getTcpHandlerEntity(int mid) {
		return scriptPool.getHandlerEntityMap().get(mid);
	}

	/**
	 * tcp消息是否已经注册
	 * 
	 * @param mid
	 *            消息ID
	 * @return
	 */
	public boolean tcpMsgIsRegister(int mid) {
		return scriptPool.getHandlerMap().containsKey(mid);
	}

	/**
	 * 获取消息处理器
	 * 
	 * @param path
	 *            请求路径
	 * @return
	 */
	public Class<? extends IHandler> getHttpHandler(String path) {
		return scriptPool.getHttpHandlerMap().get(path);
	}

	/**
	 * 获取handler配置
	 * 
	 * @param path
	 * @return
	 */
	public HandlerEntity getHttpHandlerEntity(String path) {
		return scriptPool.getHttpHandlerEntityMap().get(path);
	}

	/**
	 * 手动添加消息处理器
	 * <p>
	 * 非脚本目录下的需要手动添加到容器中
	 * </p>
	 * 
	 * @author JiangZhiYong
	 * @QQ 359135103 2017年7月24日 下午1:37:13
	 * @param clazz
	 */
	public void addIHandler(Class<? extends IHandler> clazz) {
		scriptPool.addHandler(clazz);
	}
}