package com.jzy.game.engine.cache.cooldown;

import org.slf4j.LoggerFactory;

import com.jzy.game.engine.cache.MemoryPool;
import com.jzy.game.engine.struct.Person;
import com.jzy.game.engine.util.TimeUtil;

import org.slf4j.Logger;

/**
 * 冷却管理类
 *
 * @author JiangZhiYong
 * @date 2017-05-03 QQ:359135103
 */

public class CooldownManager {

	private static final Logger log = LoggerFactory.getLogger(CooldownManager.class);

	private static final CooldownManager instance = new CooldownManager();

	public static CooldownManager getInstance() {
		return instance;
	}

	private CooldownManager() {
	}

	private final MemoryPool<Cooldown> cooldownPool = new MemoryPool<>(100000);

	/**
	 * 添加冷却
	 *
	 * @param person
	 *            对象
	 * @param type
	 *            类型
	 * @param key
	 *            关键字
	 * @param delay
	 *            冷却时间
	 */
	public Cooldown addCooldown(Person person, String type, String key, long delay) {
		Cooldown cooldown = null;
		if (person == null) {
			return cooldown;
		}
		// 初始化冷却关键字
		String cooldownKey = type;
		if (key != null) {
			cooldownKey = type + "_" + key;
		}
		if (person.getCooldowns().containsKey(cooldownKey)) {
			cooldown = person.getCooldowns().get(cooldownKey);
			cooldown.setStart(TimeUtil.currentTimeMillis());
			cooldown.setDelay(delay);
		} else {
			// 初始化冷却信息
			cooldown = createCooldown();
			cooldown.setType(type);
			cooldown.setKey(cooldownKey);
			cooldown.setStart(TimeUtil.currentTimeMillis());
			cooldown.setDelay(delay);
			// 添加冷却
			person.getCooldowns().put(cooldownKey, cooldown);
		}
		return cooldown;
	}

	/**
	 * 冷却剩余时间
	 */
	public long getCooldownTime(Person person, String type, String key) {
		Cooldown cooldown = getCooldown(person, type, key);
		if (cooldown != null) {
			return cooldown.getRemainTime();
		}
		return 0;
	}

	/**
	 * @return 冷却结束时间、
	 */
	public long getCoolEndTime(Person person, String type, String key) {
		Cooldown cooldown = getCooldown(person, type, key);
		if (cooldown != null) {
			return cooldown.getEndTime();
		}
		return 0;
	}

	/**
	 * 冷却对象
	 */
	public Cooldown getCooldown(Person person, String type, String key) {
		if (person == null) {
			return null;
		}
		// 初始化冷却关键字
		String cooldownKey = type;
		if (key != null) {
			cooldownKey = cooldownKey + "_" + key;
		}
		return person.getCooldowns().get(cooldownKey);
	}

	/**
	 * 移除冷却
	 *
	 * @param person
	 *            对象
	 * @param type
	 *            类型
	 * @param key
	 *            关键字
	 */
	public void removeCooldown(Person person, String type, String key) {
		if (person == null) {
			return;
		}
		// 初始化冷却关键字
		String cooldownKey = type;
		if (key != null) {
			cooldownKey = cooldownKey + "_" + key;
		}

		// 移除冷却
		if (person.getCooldowns().containsKey(cooldownKey)) {
			Cooldown cooldown = person.getCooldowns().remove(cooldownKey);
			if (cooldown != null) {
				cooldownPool.put(cooldown);
			}
		}
	}

	/**
	 * 是否存在这种冷却类型
	 *
	 * @param person
	 *            对象
	 * @param type
	 *            冷却类型
	 * @param key
	 *            关键字
	 * @return
	 */
	public boolean isExistCooldownType(Person person, String type, String key) {
		if (person == null) {
			return false;
		}
		// 初始化冷却关键字
		String cooldownKey = type;
		if (key != null) {
			cooldownKey = cooldownKey + "_" + key;
		}
		// 是否存在
		if (person.getCooldowns().containsKey(cooldownKey)) {
			return true;
		}
		return false;
	}

	/**
	 * 是否在冷却中
	 *
	 * @param person
	 *            对象
	 * @param type
	 *            冷却类型
	 * @param key
	 *            关键字
	 * @return true CD中无法施放技能,或使用物品
	 */
	public boolean isCooldowning(Person person, String type, String key) {
		int allow = 0;
		return isCooldowning(person, type, key, allow);
	}

	/**
	 * 是否在冷却中
	 *
	 * @param person
	 *            对象
	 * @param type
	 *            冷却类型
	 * @param key
	 *            关键字
	 * @param allow
	 *            放宽时间
	 * @return true CD中无法施放技能,或使用物品
	 */
	public boolean isCooldowning(Person person, String type, String key, int allow) {
		if (person == null) {
			return false;
		}
		// 初始化冷却关键字
		String cooldownKey = type;
		if (key != null) {
			cooldownKey = cooldownKey + "_" + key;
		}

		// 查看冷却
		Cooldown cooldown = person.getCooldowns().get(cooldownKey);
		if (cooldown != null) {
			// 放宽100毫秒冷却
			return TimeUtil.currentTimeMillis() <= cooldown.getStart() + cooldown.getDelay() - allow;
		}
		return false;
	}

	private Cooldown createCooldown() {
		try {
			return cooldownPool.get(Cooldown.class);
		} catch (Exception e) {
			log.error("从对象池中获得缓冲冷却对象失败", e);
		}
		return null;
	}
}
