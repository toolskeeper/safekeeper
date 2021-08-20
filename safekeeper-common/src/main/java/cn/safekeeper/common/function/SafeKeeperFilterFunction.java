package cn.safekeeper.common.function;

/**
 * 过滤器函数
 */
public interface SafeKeeperFilterFunction {
    /**
     * 执行
     * @param object 数据
     */
    void run(Object object);
}
