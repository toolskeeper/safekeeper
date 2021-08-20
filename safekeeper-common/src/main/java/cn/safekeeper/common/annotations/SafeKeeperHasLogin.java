package cn.safekeeper.common.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 校验是否已经登录
 * @author skylark
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface SafeKeeperHasLogin {
    /**
     * 登录维度类型
     * @return 类型
     */
    String type() default "";
}
