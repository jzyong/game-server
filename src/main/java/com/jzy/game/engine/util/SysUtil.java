package com.jzy.game.engine.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 系统工具
 *
 * @author JiangZhiYong
 * @date 2017-03-31
 * QQ:359135103
 */
public class SysUtil {

    private static final Logger log = LoggerFactory.getLogger(SysUtil.class);

    /**
     * 关闭服务
     * @param cls
     * @param e
     * @param info
     * @param obs
     */
    public static void exit(Class cls, Exception e, String info, Object... obs) {
        log.error(cls.getName(), e);
        log.warn(info, obs);
        System.exit(1);
    }
}
