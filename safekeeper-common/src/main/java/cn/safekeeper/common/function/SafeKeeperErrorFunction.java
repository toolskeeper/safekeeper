package cn.safekeeper.common.function;

/**
 * 异常方法执行抽象
 * @author skylark
 */
public interface SafeKeeperErrorFunction {
    /**
     * 执行方法
     * @param e 任意异常
     * @return 任意对象
     */
    Object run(Throwable e);

}
