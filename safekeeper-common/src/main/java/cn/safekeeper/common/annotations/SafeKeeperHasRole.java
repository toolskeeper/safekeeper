package cn.safekeeper.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 校验是否具备当前的角色
 * @author skylark
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface SafeKeeperHasRole {
    /**
     * 访问方法时需要校验的权限码
     * @return 权限集合
     */
    String [] roles() default {};

    /**
     * 验证模式：AND , OR ,EQUALS，默认AND
     * @return 条件
     */
    SafeKeeperCondition mode() default SafeKeeperCondition.AND;

    /**
     * 登录维度类型
     * @return 类型
     */
    String type() default "";
}
