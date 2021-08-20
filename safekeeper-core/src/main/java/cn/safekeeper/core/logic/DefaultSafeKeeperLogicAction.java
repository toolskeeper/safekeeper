package cn.safekeeper.core.logic;

import cn.safekeeper.common.annotations.SafeKeeperHasLogin;
import cn.safekeeper.common.annotations.SafeKeeperHasPermission;
import cn.safekeeper.common.annotations.SafeKeeperHasRole;
import cn.safekeeper.common.utils.SafeKeeperUtils;
import cn.safekeeper.core.manager.SafeKeeperManager;
import cn.safekeeper.core.session.SafeKeeperSession;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 逻辑执行默认实现
 * @author skylark
 */
public class DefaultSafeKeeperLogicAction implements SafeKeeperLogicAction{

    /**
     * 创建token
     * @param loginId 账号id
     * @param loginType 账号类型
     * @return 返回token
     */
    @Override
    public String createToken(Object loginId, String loginType) {
        return TokenCreatorFactory.getTokenCreator().createToken(loginId,loginType);
    }

    /**
     * 创建session会话
     * @param sessionId Session的Id
     * @return session会话对象
     */
    @Override
    public SafeKeeperSession createSession(String sessionId) {
        return new SafeKeeperSession(sessionId);
    }

    /**
     * 判断集合中是否存在该元素
     * @param list 集合
     * @param element 元素
     * @return 是否存在
     */
    @Override
    public boolean hasElement(List<String> list, String element) {
        // 空集合直接返回false
        if(list == null || list.size() == 0) {
            return false;
        }
        if (list.contains(element)) {
            return true;
        }
        // 开始模糊匹配
        for (String patter : list) {
            if(SafeKeeperUtils.vagueMatch(patter, element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查方法上的注解
     * @param method Method对象
     */
    @Override
    public void checkMethodAnnotation(Method method) {
        // 先校验 Method 所属 Class 上的注解
        validateAnnotation(method.getDeclaringClass());
        // 再校验 Method 上的注解
        validateAnnotation(method);
    }

    /**
     * 从指定元素校验注解
     * @param target 目标注解
     */
    protected void validateAnnotation(AnnotatedElement target) {
        // 校验 @SafeKeeperHasLogin 注解
        if(target.isAnnotationPresent(SafeKeeperHasLogin.class)) {
            SafeKeeperHasLogin at = target.getAnnotation(SafeKeeperHasLogin.class);
            SafeKeeperManager.getSafeKeeperProcessor(at.type()).checkByAnnotation(at);
        }
        // 校验 @SafeKeeperHasRole 注解
        if(target.isAnnotationPresent(SafeKeeperHasRole.class)) {
            SafeKeeperHasRole at = target.getAnnotation(SafeKeeperHasRole.class);
            SafeKeeperManager.getSafeKeeperProcessor(at.type()).checkByAnnotation(at);
        }
        // 校验 @SafeKeeperHasPermission 注解
        if(target.isAnnotationPresent(SafeKeeperHasPermission.class)) {
            SafeKeeperHasPermission at = target.getAnnotation(SafeKeeperHasPermission.class);
            SafeKeeperManager.getSafeKeeperProcessor(at.type()).checkByAnnotation(at);
        }

    }
}
