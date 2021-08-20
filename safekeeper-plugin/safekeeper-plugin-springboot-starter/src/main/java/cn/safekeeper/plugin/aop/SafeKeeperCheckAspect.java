package cn.safekeeper.plugin.aop;


import cn.safekeeper.common.constant.SafeKeeperConstant;
import cn.safekeeper.core.manager.SafeKeeperManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

/**
 * SafeKeeper 基于 Spring Aop 的注解鉴权
 * @author skylark
 */
@Aspect
@Order(SafeKeeperConstant.ASSEMBLY_ORDER)
public class SafeKeeperCheckAspect {
	
	/**
	 * 构建
	 */
	public SafeKeeperCheckAspect() {
	}

	/**
	 * 定义AOP签名 (切入所有使用SafeKeeper鉴权注解的方法)
	 */
	public static final String POINTCUT =
			"@within(cn.safekeeper.common.annotations.SafeKeeperHasLogin) || @annotation(cn.safekeeper.common.annotations.SafeKeeperHasLogin) || "
			+ "@within(cn.safekeeper.common.annotations.SafeKeeperHasPermission) || @annotation(cn.safekeeper.common.annotations.SafeKeeperHasPermission) || "
			+ "@within(cn.safekeeper.common.annotations.SafeKeeperHasRole) || @annotation(cn.safekeeper.common.annotations.SafeKeeperHasRole)";

	/**
	 * 声明AOP签名
	 */
	@Pointcut(POINTCUT)
	public void pointcut() {
	}

	/**
	 * 环绕切入
	 * 
	 * @param joinPoint 切面对象
	 * @return 底层方法执行后的返回值
	 * @throws Throwable 底层方法抛出的异常
	 */
	@Around("pointcut()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		// 注解鉴权
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		SafeKeeperManager.getSafeKeeperLogicAction().checkMethodAnnotation(signature.getMethod());
		try {
			// 执行原有逻辑
			Object obj = joinPoint.proceed();
			return obj;
		} catch (Throwable e) {
			throw e;
		}
	}

}
