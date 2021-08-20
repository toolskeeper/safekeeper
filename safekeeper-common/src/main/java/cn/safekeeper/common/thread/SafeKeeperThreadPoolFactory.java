package cn.safekeeper.common.thread;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 创建线程池工厂类
 * @author skylark
 */
public class SafeKeeperThreadPoolFactory {

    private static Map<String,ThreadPoolExecutor> threadPool= new ConcurrentHashMap<>();
    public static ExecutorService getExecutorService(String name) {
        if(threadPool.get(name)!=null){
            return threadPool.get(name);
        }
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4, 0,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(1), new SafeKeeperNamedThreadFactory(name),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        threadPool.put(name,threadPoolExecutor);
        return threadPoolExecutor;
    }
}