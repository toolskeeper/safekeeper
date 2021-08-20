package cn.safekeeper.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 通过方法上的注解就可以验证是否具备权限访问
 * @author skylark
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface SafeKeeperHasPermission {

    /**
     * 访问方法时需要校验的权限码
     * @return 权限集合
     */
    String [] permissions() default {};

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
