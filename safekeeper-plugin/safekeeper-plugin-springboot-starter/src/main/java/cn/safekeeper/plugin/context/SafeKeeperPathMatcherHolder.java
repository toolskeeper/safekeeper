package cn.safekeeper.plugin.context;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * 基于spring工具类提供的path匹配实现，默认使用ant风格的进行匹配
 * @author skylark
 *
 */
public class SafeKeeperPathMatcherHolder {

	/**
	 * 路由匹配器
	 */
	public static PathMatcher pathMatcher;

	/**
	 * 获取路由匹配器
	 * @return 路由匹配器
	 */
	public static PathMatcher getPathMatcher() {
		if(pathMatcher == null) {
			pathMatcher = new AntPathMatcher();
		}
		return pathMatcher;
	}
	
	/**
	 * 写入路由匹配器
	 * @param pathMatcher 路由匹配器
	 */
	public static void setPathMatcher(PathMatcher pathMatcher) {
		SafeKeeperPathMatcherHolder.pathMatcher = pathMatcher;
	}
	
}
