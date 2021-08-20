package cn.safekeeper.common.model;

/**
 * 上下文环境
 * @author skylark
 */
public interface SafeKeeperContext {

    /**
     * 获取请求对象
     * @return 对象
     */
    SafeKeeperRequest getRequest();

    /**
     * 获取响应对象
     * @return 对象
     */
    SafeKeeperResponse getResponse();

    /**
     * 获取存储对象
     * @return 对象
     */
    SafeKeeperStorage getStorage();
    /**
     * 校验指定路由匹配符是否可以匹配成功指定路径
     * @param pattern 路由匹配符
     * @param path 需要匹配的路径
     * @return 结果
     */
    public boolean matchPath(String pattern, String path);

}
