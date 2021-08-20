package cn.safekeeper.plugin.context;

import cn.safekeeper.common.function.SafeKeeperFunction;
import cn.safekeeper.core.manager.SafeKeeperManager;
import java.util.List;

/**
 * 路由匹配操作工具类 
 * @author skylark
 *
 */
public class SafeKeeperRouter {
	/**
	 * 路由匹配
	 * @param pattern 路由匹配符 
	 * @param path 被匹配的路由  
	 * @return 是否匹配成功 
	 */
	public static boolean isMatch(String pattern, String path) {
		return SafeKeeperManager.getSafeKeeperContext().matchPath(pattern, path);
	}

	/**
	 * 路由匹配   
	 * @param patterns 路由匹配符集合 
	 * @param path 被匹配的路由  
	 * @return 是否匹配成功 
	 */
	public static boolean isMatch(List<String> patterns, String path) {
		for (String pattern : patterns) {
			if(isMatch(pattern, path)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 路由匹配 (使用当前URI) 
	 * @param pattern 路由匹配符 
	 * @return 是否匹配成功 
	 */
	public static boolean isMatchCurrURI(String pattern) {
		return isMatch(pattern, SafeKeeperManager.getSafeKeeperContext().getRequest().getRequestPath());
	}

	/**
	 * 路由匹配 (使用当前URI) 
	 * @param patterns 路由匹配符集合 
	 * @return 是否匹配成功 
	 */
	public static boolean isMatchCurrURI(List<String> patterns) {
		return isMatch(patterns, SafeKeeperManager.getSafeKeeperContext().getRequest().getRequestPath());
	}

	/**
	 * 路由匹配，如果匹配成功则执行认证函数 
	 * @param pattern 路由匹配符
	 * @param function 要执行的方法 
	 */
	public static void match(String pattern, SafeKeeperFunction function) {
		if(isMatchCurrURI(pattern)) {
			function.run();
		}
	}

	/**
	 * 路由匹配 (并指定排除匹配符)，如果匹配成功则执行认证函数 
	 * @param pattern 路由匹配符 
	 * @param excludePattern 要排除的路由匹配符 
	 * @param function 要执行的方法 
	 */
	public static void match(String pattern, String excludePattern, SafeKeeperFunction function) {
		if(isMatchCurrURI(pattern)) {
			if(!isMatchCurrURI(excludePattern)) {
				function.run();
			}
		}
	}

	/**
	 * 路由匹配，如果匹配成功则执行认证函数 
	 * @param patterns 路由匹配符集合
	 * @param function 要执行的方法 
	 */
	public static void match(List<String> patterns, SafeKeeperFunction function) {
		if(isMatchCurrURI(patterns)) {
			function.run();
		}
	}

	/**
	 * 路由匹配 (并指定排除匹配符)，如果匹配成功则执行认证函数 
	 * @param patterns 路由匹配符集合
	 * @param excludePatterns 要排除的路由匹配符集合
	 * @param function 要执行的方法 
	 */
	public static void match(List<String> patterns, List<String> excludePatterns, SafeKeeperFunction function) {
		if(isMatchCurrURI(patterns)) {
			if(!isMatchCurrURI(excludePatterns)) {
				function.run();
			}
		}
	}
	
	
}
