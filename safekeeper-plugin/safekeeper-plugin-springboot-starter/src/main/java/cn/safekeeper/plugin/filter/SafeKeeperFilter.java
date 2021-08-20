package cn.safekeeper.plugin.filter;

import cn.safekeeper.common.constant.SafeKeeperConstant;
import cn.safekeeper.common.exception.*;
import cn.safekeeper.common.function.SafeKeeperErrorFunction;
import cn.safekeeper.common.function.SafeKeeperFilterFunction;
import cn.safekeeper.plugin.context.SafeKeeperRouter;
import cn.safekeeper.plugin.web.CodeEnum;
import cn.safekeeper.plugin.web.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Servlet全局过滤器 
 * @author skylark
 *
 */
@Order(SafeKeeperConstant.ASSEMBLY_ORDER)
@Slf4j
public class SafeKeeperFilter implements Filter {

	/**
	 * 安全拦截路由
	 */
	private final List<String> includeList = new ArrayList<>();

	/**
	 * 安全放行路由
	 */
	private final List<String> excludeList = new ArrayList<>();

	/**
	 * 对象json
	 */
	private final ObjectMapper mapper=new ObjectMapper();

	/**
	 * 添加拦截路由
	 * @param paths 路由
	 * @return 对象
	 */
	public SafeKeeperFilter addInclude(String... paths) {
		includeList.addAll(Arrays.asList(paths));
		return this;
	}
	
	/**
	 * 添加放行路由
	 * @param paths 路由
	 * @return 对象
	 */
	public SafeKeeperFilter addExclude(String... paths) {
		excludeList.addAll(Arrays.asList(paths));
		return this;
	}

	/**
	 * 写入 拦截路由 集合
	 * @param pathList 路由集合 
	 * @return 对象
	 */
	public SafeKeeperFilter setIncludeList(List<String> pathList) {
		includeList.addAll(pathList);
		return this;
	}
	
	/**
	 * 写入 放行路由 集合
	 * @param pathList 路由集合 
	 * @return 对象
	 */
	public SafeKeeperFilter setExcludeList(List<String> pathList) {
		excludeList.addAll(pathList);
		return this;
	}

	/**
	 * 认证函数
	 */
	private SafeKeeperFilterFunction auth = auth -> {};

	/**
	 * 异常处理函数
	 */
	public SafeKeeperErrorFunction error = error-> null;

	/**
	 * 前置函数
	 */
	private SafeKeeperFilterFunction beforeAuth =beforeAuth -> {};

	/**
	 * 写入认证函数
	 * @param auth 函数实现
	 * @return 对象自身
	 */
	public SafeKeeperFilter setAuth(SafeKeeperFilterFunction auth) {
		this.auth = auth;
		return this;
	}

	/**
	 * 异常处理函数
	 * @param error 每次认证函数发生异常时执行此函数
	 * @return 对象自身
	 */
	public SafeKeeperFilter setError(SafeKeeperErrorFunction error) {
		this.error = error;
		return this;
	}

	/**
	 * 前置函数
	 * @param beforeAuth 在每次认证函数之前执行
	 * @return 对象自身
	 */
	public SafeKeeperFilter setBeforeAuth(SafeKeeperFilterFunction beforeAuth) {
		this.beforeAuth = beforeAuth;
		return this;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException{
		try {
			log.debug("SafeKeeperFilter过滤器执行开始");
			log.debug("SafeKeeperFilter过滤器校验集合是:"+includeList);
			log.debug("SafeKeeperFilter过滤器放行集合是:"+excludeList);
			// 执行SafeKeeper全局过滤器
			SafeKeeperRouter.match(includeList, excludeList, () -> {
				beforeAuth.run(request);
				auth.run(request);
			});
			log.debug("SafeKeeperFilter过滤器执行完成");
			log.debug("SafeKeeperFilter执行后面业务过滤器开始");
			chain.doFilter(request, response);
			log.debug("SafeKeeperFilter执行后面业务过滤器完成");
		} catch (Throwable e) {
			log.debug("SafeKeeperFilter拦截到了异常",e);
			response.setContentType("application/json; charset=utf-8");
			response.getWriter().print(deserializeExceptionToJson(e));
		}

	}

	private String deserializeExceptionToJson(Throwable e){
		Object result = error.run(e);
		String resultString = String.valueOf(result);
		log.debug("SafeKeeperFilter error函数得到的结果是:"+result);

		if(e instanceof SafeKeeperException){
			if(e instanceof SafeKeeperLockedException){
				log.debug("SafeKeeperFilter 匹配到SafeKeeperLockedException异常");
				SafeKeeperLockedException ex=(SafeKeeperLockedException)e;
				Result<String> objectResult = Result.failedWith(null, ex.getCode(), resultString);
				log.debug("SafeKeeperFilter 匹配到SafeKeeperLockedException结果对象是:"+objectResult);
				try {
					return mapper.writeValueAsString(objectResult);
				} catch (JsonProcessingException jsonProcessingException) {
					jsonProcessingException.printStackTrace();
				}
			}else if(e instanceof SafeKeeperLoginException){
				log.debug("SafeKeeperFilter 匹配到SafeKeeperLoginException异常");
				SafeKeeperLoginException ex=(SafeKeeperLoginException)e;
				Result<String> objectResult = Result.failedWith(null, ex.getCode(), resultString);
				log.debug("SafeKeeperFilter 匹配到SafeKeeperLoginException结果对象是:"+objectResult);
				try {
					return mapper.writeValueAsString(objectResult);
				} catch (JsonProcessingException jsonProcessingException) {
					jsonProcessingException.printStackTrace();
				}
			}else if(e instanceof SafeKeeperPermissionException){
				log.debug("SafeKeeperFilter 匹配到SafeKeeperPermissionException异常");
				SafeKeeperPermissionException ex=(SafeKeeperPermissionException)e;
				Result<String> objectResult = Result.failedWith(null, ex.getCode(), resultString);
				log.debug("SafeKeeperFilter 匹配到SafeKeeperPermissionException结果对象是:"+objectResult);
				try {
					return mapper.writeValueAsString(objectResult);
				} catch (JsonProcessingException jsonProcessingException) {
					jsonProcessingException.printStackTrace();
				}
			}else if(e instanceof SafeKeeperRoleException){
				log.debug("SafeKeeperFilter 匹配到SafeKeeperRoleException异常");
				SafeKeeperRoleException ex=(SafeKeeperRoleException)e;
				Result<String> objectResult = Result.failedWith(null, ex.getCode(), resultString);
				log.debug("SafeKeeperFilter 匹配到SafeKeeperRoleException结果对象是:"+objectResult);
				try {
					return mapper.writeValueAsString(objectResult);
				} catch (JsonProcessingException jsonProcessingException) {
					jsonProcessingException.printStackTrace();
				}
			}
			log.debug("SafeKeeperFilter 没有匹配到子异常");
			Result<String> objectResult = Result.failedWith(null, CodeEnum.ERROR.getCode(), resultString);
			try {
				return mapper.writeValueAsString(objectResult);
			} catch (JsonProcessingException jsonProcessingException) {
				jsonProcessingException.printStackTrace();
			}
		}
		Result<String> objectResult = Result.failedWith(null,
				CodeEnum.OTHER.getCode(),
				resultString);
		try {
			return mapper.writeValueAsString(objectResult);
		} catch (JsonProcessingException jsonProcessingException) {
			jsonProcessingException.printStackTrace();
		}
		return resultString;
	}



	@Override
	public void init(FilterConfig filterConfig){
	}

	@Override
	public void destroy() {
	}

}
