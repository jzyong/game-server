package com.jzy.game.engine.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.thread.ExecutorPool;
import com.jzy.game.engine.thread.ServerThread;
import com.jzy.game.engine.thread.ThreadPoolExecutorConfig;
import com.jzy.game.engine.thread.ThreadType;

/**
 * 抽象服务
 *
 * @author JiangZhiYong
 * @date 2017-03-30 QQ:359135103
 * @param <Conf>
 */
public abstract class Service<Conf extends BaseServerConfig> implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

    private final Map<ThreadType, Executor> serverThreads = new ConcurrentHashMap<>();

    /**
     * 不创建地图线程组
     *
     * @param threadPoolExecutorConfig
     */
    public Service(ThreadPoolExecutorConfig threadPoolExecutorConfig) {
        // 初始化IO默认线程池
        if (threadPoolExecutorConfig != null) {
            // 客户端的请求,默认使用其执行
            ThreadPoolExecutor ioHandlerThreadExcutor = threadPoolExecutorConfig.newThreadPoolExecutor();
            serverThreads.put(ThreadType.IO, ioHandlerThreadExcutor);
        }
        int heart = 0;
        int commondSize = 100;
        if (threadPoolExecutorConfig != null) {
            heart = threadPoolExecutorConfig.getHeart();
            commondSize = threadPoolExecutorConfig.getCommandSize();
        }
        ServerThread syncThread = new ServerThread(new ThreadGroup("全局同步线程"),
                "全局同步线程:" + this.getClass().getSimpleName(), heart, commondSize);
        syncThread.start();
        serverThreads.put(ThreadType.SYNC, syncThread);
    }

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(new CloseByExit(this)));
        initThread();
        running();
    }

    /**
     * 初始化线程
     */
    protected void initThread() {
    }

    /**
     * 运行中
     */
    protected abstract void running();

    /**
     * 关闭回调
     */
    protected void onShutdown() {
        serverThreads.values().forEach(executor -> {
            if (executor != null) {
                try {
                    if (executor instanceof ServerThread) {
                        if (((ServerThread) executor).isAlive()) {
                            ((ServerThread) executor).stop(true);
                        }
                    } else if (executor instanceof ThreadPoolExecutor) {
                        if (!((ThreadPoolExecutor) executor).isShutdown()) {
                            ((ThreadPoolExecutor) executor).shutdown();
                            while (!((ThreadPoolExecutor) executor).awaitTermination(5, TimeUnit.SECONDS)) {
                                LOGGER.error("线程池剩余线程:" + ((ThreadPoolExecutor) executor).getActiveCount());
                            }
                        }
                    }else if(executor instanceof ExecutorPool){
                    	((ExecutorPool)executor).stop();
                    }
                } catch (Exception e) {
                    LOGGER.error("关闭线程", e);
                }

            }
        });
    }

    /**
     * 关闭
     *
     * @param flag
     */
    public void stop(boolean flag) {
        onShutdown();
    }

    public Map<ThreadType, Executor> getServerThreads() {
        return serverThreads;
    }

    /**
     * 获得线程
     *
     * @param threadType
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends Executor> T getExecutor(ThreadType threadType) {
        return (T) serverThreads.get(threadType);
    }

    /**
     * 连接创建
     *
     * @param session
     */
    public void onIoSessionConnect(IoSession session) {
    }

    /**
     * 连接删除
     *
     * @param session
     */
    public void onIoSessionClosed(IoSession session) {
    }

    /**
     * 关服回调
     *
     * @author JiangZhiYong
     * @date 2017-03-30 QQ:359135103
     */
    private static final class CloseByExit implements Runnable {

        private final static Logger log = LoggerFactory.getLogger(CloseByExit.class);
        @SuppressWarnings("rawtypes")
        private final Service server;

        @SuppressWarnings("rawtypes")
        public CloseByExit(Service server) {
            this.server = server;
        }

        @Override
        public void run() {
            server.onShutdown();
            log.warn("服务{}已停止", server.getClass().getName());
        }
    }

}
