package com.jzy.game.ai.btree;

/**
 * 行为树常量
 * 
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 *
 */
public class BehaviorTreeConstants {

	/** xml根节点 */
	public static final String XML_ROOT = "Root";

	/** 描述节点 */
	public static final String XML_INFO = "Info";

	/** id标识节点 */
	public static final String XML_ID = "Id";

	/** 行为树标识节点 */
	public static final String XML_TREE = "Tree";

	/** 选择器标识节点 */
	public static final String XML_SELECTOR = "Selector";
	
	/** 随机选择器标识节点 */
	public static final String XML_RANDOM_SELECTOR = "RandomSelector";

	/** 顺序执行标识节点 */
	public static final String XML_SEQUENCE = "Sequence";
	
	/** 随机顺序执行标识节点 */
	public static final String XML_RANDOM_SEQUENCE = "RandomSequence";

	/** 并行执行标识节点 */
	public static final String XML_PARALLEL = "Parallel";

	/** 防御节点标识 */
	public static final String XML_GUARD = "Guard";

	/** 叶子节点标识 */
	public static final String XML_LEAF = "Leaf";
	
	/** 直接失败节点标识 */
	public static final String XML_ALWAYS_FAIL = "AlwaysFail";
	
	/** 直接成功节点标识 */
	public static final String XML_ALWAYS_SUCCEED = "AlwaysSucceed";
	
	/** 颠倒结果节点标识 */
	public static final String XML_INVERT = "Invert";
	
	/** 重复执行指定次数节点标识 */
	public static final String XML_REPEAT = "Repeat";
	
	/** 任务信号量节点标识 */
	public static final String XML_SEAMPHORE_GUARD = "SemaphoreGuard";
	
	/** 一直到到失败节点标识 */
	public static final String XML_UNTIL_FAIL = "UntilFail";
	
	/** 一直到成功节点标识 */
	public static final String XML_UNTIL_SUCCESS = "UntilSuccess";

	/** xml name属性 */
	public static final String XML_ATTRIBUTE_NAME = "name";
	
	/** xml class属性 */
	public static final String XML_ATTRIBUTE_CLASS = "class";

	/** xml policy属性 */
	public static final String XML_ATTRIBUTE_POLICY = "policy";
	
	/** xml orchestrator属性 */
	public static final String XML_ATTRIBUTE_ORCHESTRATOR = "orchestrator";
	
	/** xml times属性 */
	public static final String XML_ATTRIBUTE_TIMES = "times";

}
