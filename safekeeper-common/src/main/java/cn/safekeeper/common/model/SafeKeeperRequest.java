package cn.safekeeper.common.model;

/**
 * 请求对象
 * @author skylark
 */
public interface SafeKeeperRequest {

    /**
     * 获取对象
     * @return 值
     */
    Object getMyself();

    /**
     * 在 [请求体] 里获取一个值
     * @param name 键
     * @return 值
     */
    String getParameter(String name);

    /**
     * 在 [请求头] 里获取一个值
     * @param name 键
     * @return 值
     */
    String getHeader(String name);

    /**
     * 在 [Cookie作用域] 里获取一个值
     * @param name 键
     * @return 值
     */
    String getCookieValue(String name);

    /**
     * 返回当前请求path (不包括上下文名称)
     * @return 值
     */
    String getRequestPath();

    /**
     * 返回当前请求的类型
     * @return 值
     */
    String getMethod();

}
