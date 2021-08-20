package cn.safekeeper.plugin.model;

import cn.safekeeper.common.model.SafeKeeperStorage;

import javax.servlet.http.HttpServletRequest;

/**
 * @author skylark
 * 存储实现类
 */
public class SafeKeeperStorageForServlet implements SafeKeeperStorage {

	/**
	 * 底层Request对象
	 */
	private HttpServletRequest request;
	
	/**
	 * 实例化
	 * @param request request对象 
	 */
	public SafeKeeperStorageForServlet(HttpServletRequest request) {
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
	 * 在 [Request作用域] 里写入一个值 
	 */
	@Override
	public void set(String key, Object value) {
		request.setAttribute(key, value);
	}

	/**
	 * 在 [Request作用域] 里获取一个值 
	 */
	@Override
	public Object get(String key) {
		return request.getAttribute(key);
	}

	/**
	 * 在 [Request作用域] 里删除一个值 
	 */
	@Override
	public void delete(String key) {
		request.removeAttribute(key);
	}

}
