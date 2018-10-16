package com.jzy.game.ai.btree;

import static com.jzy.game.ai.btree.BehaviorTreeConstants.*;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.ai.btree.branch.Parallel;
import com.jzy.game.ai.btree.branch.Parallel.Orchestrator;
import com.jzy.game.ai.btree.branch.Parallel.Policy;
import com.jzy.game.ai.btree.branch.RandomSelector;
import com.jzy.game.ai.btree.branch.RandomSequence;
import com.jzy.game.ai.btree.branch.Selector;
import com.jzy.game.ai.btree.branch.Sequence;
import com.jzy.game.ai.btree.decorator.AlwaysFail;
import com.jzy.game.ai.btree.decorator.AlwaysSucceed;
import com.jzy.game.ai.btree.decorator.Invert;
import com.jzy.game.ai.btree.decorator.Repeat;
import com.jzy.game.ai.btree.decorator.SemaphoreGuard;
import com.jzy.game.ai.btree.decorator.UntilFail;
import com.jzy.game.ai.btree.decorator.UntilSuccess;
import com.jzy.game.engine.struct.Person;
import com.jzy.game.engine.util.Args;
import com.jzy.game.engine.util.Args.Two;
import com.jzy.game.engine.util.FileUtil;
import com.jzy.game.engine.util.ReflectUtil;
import com.jzy.game.engine.util.StringUtil;

/**
 * 行为树 <br>
 * 
 * @author JiangZhiYong
 * @QQ 359135103 2017年11月24日
 */
public class BehaviorTreeManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(BehaviorTreeManager.class);
	private static volatile BehaviorTreeManager behaviorTreeManager;

	/** 行为树对象缓存 */
	private Map<String, BehaviorTree<? extends Person>> behaviorTrees = new HashMap<>();

	private BehaviorTreeManager() {

	}

	public static BehaviorTreeManager getInstance() {
		if (behaviorTreeManager == null) {
			synchronized (BehaviorTreeManager.class) {
				if (behaviorTreeManager == null) {
					behaviorTreeManager = new BehaviorTreeManager();
				}
			}
		}
		return behaviorTreeManager;
	}

	/**
	 * 解析行为树，xml配置文件
	 * 
	 * @param path
	 */
	public void parseBehaviorTree(String path) {
		List<File> files = new ArrayList<>();
		File f = new File(path);
		if (!f.exists()) {
			throw new IllegalStateException(String.format("%s 行为树文件不存在", path));
		}
		FileUtil.getRfFiles(files, f, new String[] { ".xml" });
		try {
			if (!files.isEmpty()) {
				for (File file : files) {
					if (file.exists()) {
						// 加载行为树
						Two<String, BehaviorTree<? extends Person>> tree = createBehaviorTree(file);
						behaviorTrees.put(tree.a(), tree.b());
						// LOGGER.debug("行为树{} 加入容器", tree.a());
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("解析行为树xml", e);
		}

	}

	/**
	 * 创建行为树
	 * 
	 * @param file
	 * @return
	 */
	private Args.Two<String, BehaviorTree<? extends Person>> createBehaviorTree(File file) throws Exception {
		String xmlStr = FileUtil.readTxtFile(file.getPath());
		Document document = DocumentHelper.parseText(xmlStr);
		Element rootElement = document.getRootElement(); // 根节点

		Element idElement = rootElement.element(XML_ID); // id节点
		if (idElement == null) {
			throw new RuntimeException(String.format("%s 行为树id未配置", file.getPath()));
		}
		String id = idElement.getTextTrim();
		Element treeElement = rootElement.element(XML_TREE); // 行为树节点
		// 获取根节点
		if (treeElement == null || !treeElement.hasContent()) {
			throw new RuntimeException(String.format("%s 行为树节点未配置", file.getPath()));
		}
		List<?> treeRootElements = treeElement.elements();
		if (treeRootElements.size() > 1) {
			throw new RuntimeException(String.format("%s 行为树存在%d根节点", file.getPath(), treeRootElements.size()));
		}
		// 行为树xml根节点
		Element rootTaskElement = (Element) treeRootElements.get(0);
		// 行为树根任务
		Task<Person> rootTask = createTask(rootTaskElement);
		// 递归设置分支节点和叶子节点
		addTask(rootTaskElement, rootTask);

		BehaviorTree<? extends Person> behaviorTree = new BehaviorTree<>(rootTask);
		return Args.of(id, behaviorTree);
	}

	/**
	 * 递归添加行为树子节点 <br>
	 * 
	 * @param element
	 *            xml配置节点
	 * @param task
	 *            父任务
	 */
	private void addTask(Element element, Task<Person> task) {
		Iterator<Element> iterator = element.elementIterator();
		while (iterator.hasNext()) {
			Element secondElement = iterator.next();
			Task<Person> secondTask = createTask(secondElement);
			if (secondElement.getName().equalsIgnoreCase(XML_GUARD)) {
				task.setGuard(secondTask);
			} else {
				task.addChild(secondTask);
			}
			if (secondElement.hasContent()) {
				addTask(secondElement, secondTask);
			}
		}
	}

	/**
	 * 创建行为树节点
	 * 
	 * @param element
	 * @return
	 */
	private Task<Person> createTask(Element element) {
		if (element == null) {
			throw new RuntimeException("传入行为数节点为空");
		}
		Task<Person> task = null;

		switch (element.getName()) {
		case XML_SELECTOR:
			task = new Selector<>();
			break;
		case XML_RANDOM_SELECTOR:
			task = new RandomSelector<>();
			break;
		case XML_SEQUENCE:
			task = new Sequence<>();
			break;
		case XML_RANDOM_SEQUENCE:
			task = new RandomSequence<>();
			break;
		case XML_PARALLEL:
			// 设置并行器执行方式
			Policy policy = Policy.Sequence;
			Attribute policyAttr = element.attribute(XML_ATTRIBUTE_POLICY);
			if (policy != null && Policy.Selector.name().equalsIgnoreCase(policyAttr.getValue())) {
				policy = Policy.Selector;
			}

			Orchestrator orchestrator = Orchestrator.Resume;
			Attribute orchestratorAttr = element.attribute(XML_ATTRIBUTE_ORCHESTRATOR);
			if (orchestratorAttr != null && Orchestrator.Join.name().equalsIgnoreCase(orchestratorAttr.getValue())) {
				orchestrator = Orchestrator.Join;
			}
			task = new Parallel<>(policy, orchestrator);
			break;
		case XML_LEAF:
			task = createLeafTask(element);
			break;
		case XML_GUARD:
			// note 防御暂时默认设置为顺序执行节点，依次检测
			task = new Sequence<>();
			break;
		case XML_ALWAYS_FAIL:
			task = new AlwaysFail<>();
			break;
		case XML_ALWAYS_SUCCEED:
			task = new AlwaysSucceed<>();
			break;
		case XML_INVERT:
			task = new Invert<>();
			break;
		case XML_REPEAT:
			int times = -1;
			Attribute timesAttr = element.attribute(XML_ATTRIBUTE_TIMES);
			if (timesAttr != null && !StringUtil.isNullOrEmpty(timesAttr.getValue())) {
				times = Integer.parseInt(timesAttr.getValue());
			}
			task = new Repeat<>(times);
			break;
		case XML_SEAMPHORE_GUARD:
			Attribute nameAttr = element.attribute(XML_ATTRIBUTE_NAME);
			if (nameAttr == null || StringUtil.isNullOrEmpty(nameAttr.getValue())) {
				throw new IllegalStateException(String.format("信号量装饰器为设置name属性"));
			}
			task = new SemaphoreGuard<>(nameAttr.getValue());
			break;
		case XML_UNTIL_FAIL:
			task=new UntilFail<>();
			break;
		case XML_UNTIL_SUCCESS:
			task=new UntilSuccess<>();
			break;
			
		default:
			throw new IllegalStateException(String.format("节点 %s 名称非法", element.getName()));
		}
		Attribute nameAttr = element.attribute(XML_ATTRIBUTE_NAME);

		// 设置调试别称，如果未设置，使用类名设置
		if (nameAttr != null) {
			task.setName(nameAttr.getValue());
		} else {
			Attribute classAttr = element.attribute(XML_ATTRIBUTE_CLASS);
			if (classAttr != null) {
				task.setName(classAttr.getValue());
			}
		}
		return task;
	}

	/**
	 * 创建叶子任务
	 * 
	 * @param element
	 * @return
	 */
	@SuppressWarnings({ "unchecked", })
	private LeafTask<Person> createLeafTask(Element element) {
		Attribute leafAttr = element.attribute(XML_ATTRIBUTE_CLASS);
		if (leafAttr == null) {
			throw new IllegalStateException(
					String.format("xml %s %s节点 未配置class属性", element.getUniquePath(), element.getName()));
		}
		String classStr = leafAttr.getValue();
		LeafTask<Person> leafTask = null;
		Class<?> leafTaskClass = null;
		try {
			leafTaskClass = Class.forName(classStr);
			leafTask = (LeafTask<Person>) leafTaskClass.newInstance();

			// 设置属性
			if (element.attributeCount() < 2) { // 没有设置属性参数
				return leafTask;
			}
			Map<String, Method> writeMethods = ReflectUtil.getWriteMethod(leafTaskClass);
			Map<String, String> attrMap = new HashMap<>();
			for (Attribute attribute : (List<Attribute>) element.attributes()) {
				String name = attribute.getName();
				if (name.equalsIgnoreCase(XML_ATTRIBUTE_CLASS)) {
					continue;
				}
				if (!writeMethods.containsKey(name)) {
					LOGGER.warn("配置错误：{}AI 节点{} 属性{} 不存在", element.getDocument().getPath(), classStr, name);
					continue;
				}
				if (StringUtil.isNullOrEmpty(attribute.getValue())) {
					LOGGER.warn("配置错误：{}AI 节点{} 属性{} 为空", element.getDocument().getPath(), classStr, name);
					continue;
				}

				attrMap.put(name, attribute.getValue());
			}

			for (Map.Entry<String, Method> entry : writeMethods.entrySet()) {
				if (!attrMap.containsKey(entry.getKey())) {
					continue;
				}
				Method method = entry.getValue();
				Field field = leafTaskClass.getDeclaredField(entry.getKey());
				if (field.getType().isAssignableFrom(int.class)) {
					method.invoke(leafTask, Integer.parseInt(attrMap.get(entry.getKey())));
				} else if (field.getType().isAssignableFrom(float.class)) {
					method.invoke(leafTask, Float.parseFloat(attrMap.get(entry.getKey())));
				} else if (field.getType().isAssignableFrom(double.class)) {
					method.invoke(leafTask, Double.parseDouble(attrMap.get(entry.getKey())));
				} else if (field.getType().isAssignableFrom(long.class)) {
					method.invoke(leafTask, Long.parseLong(attrMap.get(entry.getKey())));
				} else if (field.getType().isAssignableFrom(short.class)) {
					method.invoke(leafTask, Short.parseShort(attrMap.get(entry.getKey())));
				} else {
					method.invoke(leafTask, attrMap.get(entry.getKey()));
				}
				// note 其他类型数据解析？？

			}
		} catch (Exception e) {
			LOGGER.error(String.format("%s 创建叶子节点", element.getDocument().getPath()), e);
		}

		return leafTask;
	}

	/**
	 * 克隆行为树
	 * 
	 * @param id
	 *            唯一标识
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public BehaviorTree<? extends Person> cloneBehaviorTree(String id) {
		BehaviorTree<? extends Person> behaviorTree = behaviorTrees.get(id);
		if (behaviorTree == null) {
			return null;
		}
		try {
			return (BehaviorTree<Person>) ReflectUtil.deepCopy(behaviorTree);
		} catch (Exception e) {
			LOGGER.error("克隆行为树", e);
		}
		return null;
	}

}
