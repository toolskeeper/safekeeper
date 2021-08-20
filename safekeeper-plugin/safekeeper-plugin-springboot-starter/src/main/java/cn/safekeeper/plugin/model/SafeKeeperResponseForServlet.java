package cn.safekeeper.plugin.model;

import cn.safekeeper.common.model.SafeKeeperResponse;
import cn.safekeeper.common.utils.SafeKeeperUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 响应实现
 * @author skylark
 *
 */
public class SafeKeeperResponseForServlet implements SafeKeeperResponse {

	/**
	 * 底层Request对象
	 */
	private HttpServletResponse response;
	
	/**
	 * 实例化
	 * @param response response对象 
	 */
	public SafeKeeperResponseForServlet(HttpServletResponse response) {
		this.response = response;
	}
	
	/**
	 * 获取底层源对象 
	 */
	@Override
	public Object getMyself() {
		return response;
	}

	/**
	 * 删除指定Cookie 
	 */
	@Override
	public void deleteCookie(String name) {
		addCookie(name, null, null, null, 0);
	}

	/**
	 * 写入指定Cookie 
	 */
	@Override
	public void addCookie(String name, String value, String path, String domain, int timeout) {
		Cookie cookie = new Cookie(name, value);
		if(SafeKeeperUtils.isEmpty(path)) {
			path = "/";
		}
		if(!SafeKeeperUtils.isEmpty(domain)) {
			cookie.setDomain(domain);
		}
		cookie.setPath(path);
		cookie.setMaxAge(timeout);
		response.addCookie(cookie);
	}

	
	/**
	 * 在响应头里写入一个值 
	 */
	@Override
	public SafeKeeperResponse setHeader(String name, String value) {
		response.setHeader(name, value);
		return this;
	}

}
