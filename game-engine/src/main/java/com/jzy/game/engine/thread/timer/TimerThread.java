package com.jzy.game.engine.thread.timer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.LoggerFactory;

import com.jzy.game.engine.thread.ServerThread;

import org.slf4j.Logger;

/**
 * 定时任务
 *
 * @author JiangZhiYong
 * @date 2017-03-30
 * QQ:359135103
 */
public class TimerThread extends Timer {

    private static final Logger log = LoggerFactory.getLogger(TimerThread.class);
    // 定时器依附的线程
    private final ServerThread serverThread;
    // 定时器任务
    private final Collection<TimerEvent> events = Collections.synchronizedCollection(new ArrayList<TimerEvent>());

    // 定时器中记录的定时器类型
    private final HashSet<String> classLogNames = new HashSet<>();

    // 使用JDK TimerTask
    private TimerTask task;

    @SuppressWarnings("unchecked")
	public TimerThread(ServerThread serverThread, Class<? extends TimerEvent>... classLogNames) {
        super(serverThread.getThreadName() + "-Timer");
        this.serverThread = serverThread;

        for (Class<? extends TimerEvent> cls : classLogNames) {
            this.classLogNames.add(cls.getSimpleName());
        }

        log.info("TimerThread:TimerThread = " + serverThread.getThreadName());
    }

    public void start() {
        log.info("TimerThread:start=" + serverThread.getThreadName() + "=heart=" + serverThread.getHeart());
        task = new TimerTask() {
            @Override
            public void run() {
                synchronized (events) {
                    Iterator<TimerEvent> it = events.iterator();
                    while (it.hasNext()) {
                        TimerEvent event = it.next();
                        if (event.remain() >= 0L) {
                            if (event.getLoop() > 0) {
                                event.setLoop(event.getLoop() - 1);
                                serverThread.execute(event);
                            } else if (event.getLoop() < 0) {
                                serverThread.execute(event);
                            }
                        } else {
                            serverThread.execute(event);
                            event.setLoop(0);
                        }
                        if (event.getLoop() == 0) {
                            it.remove();
                            if (classLogNames.contains(event.getClass().getName())) {
//                                try {
//                                    throw new Exception("TimerThread.TimerTask:run=remove");
//                                } catch (Exception e) {
                                log.error("故意抛出的异常,以做提醒" + Thread.currentThread().getName() + " " + Thread.currentThread().getThreadGroup() + " " + Thread.currentThread());
//                                }
                            }
                        }
                    }
                }
            }
        };
        schedule(task, 0L, serverThread.getHeart());
        log.error("TimerThread:end=" + serverThread.getThreadName() + "=heart=" + serverThread.getHeart());
    }

    public void stop(boolean flag) {
        synchronized (events) {
            events.clear();
//            try {
//                throw new Exception("TimerThread:clear");
//            } catch (Exception e) {
            log.error("故意抛出的异常,以做提醒" + Thread.currentThread().getName() + " " + Thread.currentThread().getThreadGroup() + " " + Thread.currentThread());
            if (task != null) {
                task.cancel();
            }
            cancel();
//            }
        }
        int sign = flag ? 1 : 0;
        log.error("TimerThread:stop=" + serverThread.getThreadName() + "=flag=" + sign);
    }

    public void addTimerEvent(TimerEvent event) {
        synchronized (events) {
            events.add(event);
        }
        if (classLogNames.contains(event.getClass().getName())) {
            log.error("TimerThread:addTimerEvent=" + serverThread.getThreadName() + "=event=" + event.getClass().getName());
        }
    }

    public void removeTimerEvent(TimerEvent event) {
        synchronized (events) {
            events.remove(event);
            if (classLogNames.contains(event.getClass().getName())) {
//                try {
//                    throw new Exception("TimerThread浜嬩欢:removeTimerEvent=remove");
//                } catch (Exception e) {
                log.error("故意抛出的异常,以做提醒" + Thread.currentThread().getName() + " " + Thread.currentThread().getThreadGroup() + " " + Thread.currentThread());
//                }
            }
        }
    }
}
