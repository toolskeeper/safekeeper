package cn.safekeeper.core.logic;

import cn.safekeeper.core.session.SafeKeeperSession;

import java.lang.reflect.Method;
import java.util.List;

/**
 * SafeKeeper逻辑运行执行
 * @author skylark
 */
public interface SafeKeeperLogicAction {
    /**
     * 创建一个Token
     * @param loginId 账号id
     * @param loginType 账号类型
     * @return token
     */
    String createToken(Object loginId, String loginType);

    /**
     * 创建一个Session
     * @param sessionId Session的Id
     * @return 创建后的Session
     */
    SafeKeeperSession createSession(String sessionId);

    /**
     * 判断：集合中是否包含指定元素（模糊匹配）
     * @param list 集合
     * @param element 元素
     * @return 是否包含
     */
    boolean hasElement(List<String> list, String element);

    /**
     * 对一个Method对象进行注解检查（注解鉴权内部实现）
     * @param method Method对象
     */
    void checkMethodAnnotation(Method method);
}
