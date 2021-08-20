package cn.safekeeper.common.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂类
 * @author skylark
 */
public class SafeKeeperNamedThreadFactory implements ThreadFactory {
    private AtomicInteger count = new AtomicInteger(1);
    private ThreadGroup group;
    private String namePrefix;
    private boolean daemon;

    public SafeKeeperNamedThreadFactory(String namePrefix) {
        this(namePrefix, false);
    }

    public SafeKeeperNamedThreadFactory(String namePrefix, boolean daemon) {
        SecurityManager securityManager = System.getSecurityManager();
        this.group = securityManager != null ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(group, runnable, namePrefix + "-thread-" + count.getAndIncrement(), 0);
        thread.setDaemon(daemon);
        return thread;
    }
}