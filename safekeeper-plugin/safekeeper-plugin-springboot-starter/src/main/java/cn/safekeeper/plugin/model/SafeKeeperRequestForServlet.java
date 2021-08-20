package cn.safekeeper.plugin.model;

import cn.safekeeper.common.model.SafeKeeperRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * SafeKeeperRequest实现
 * @author skylark
 */
public class SafeKeeperRequestForServlet implements SafeKeeperRequest {

	/**
	 * 构造函数
	 */
	public SafeKeeperRequestForServlet(){

	}

	/**
	 * 底层Request对象
	 */
	private HttpServletRequest request;

	/**
	 * 实例化
	 * @param request request对象
	 */
	public SafeKeeperRequestForServlet(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * 获取底层源对象
	 */
	@Override
	public Object getMyself() {
		return request;
	}

	/**
	 * 在 [请求体] 里获取一个值
	 */
	@Override
	public String getParameter(String name) {
		return request.getParameter(name);
	}

	/**
	 * 在 [请求头] 里获取一个值
	 */
	@Override
	public String getHeader(String name) {
		return request.getHeader(name);
	}

	/**
	 * 在 [Cookie作用域] 里获取一个值
	 */
	@Override
	public String getCookieValue(String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie != null && name.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * 返回当前请求path (不包括上下文名称)
	 */
	@Override
	public String getRequestPath() {
		return request.getServletPath();
	}

	/**
	 * 返回当前请求的类型
	 */
	@Override
	public String getMethod() {
		return request.getMethod();
	}



}
