package cn.safekeeper.common.model;
import cn.safekeeper.common.constant.SafeKeeperConstant;
import cn.safekeeper.common.configuration.SafeKeeperConfiguration;
import lombok.ToString;

/**
 * 调用登录模型
 * @author skylark
 *
 */
@ToString
public class SafeKeeperValueObject {

	/**
	 * 永久不过期
	 */
	private static final int NEVER_EXPIRE=-1;
	
	/**
	 * 客户端设备标识
	 */
	public String device;
	
	/**
	 * 是否为持久Cookie
	 */
	public Boolean isPersistent;

	/**
	 * 登录token的有效期,单位:秒
	 */
	public Long timeout;


	public String getDevice() {
		return device;
	}

	public SafeKeeperValueObject setDevice(String device) {
		this.device = device;
		return this;
	}

	public Boolean getIsPersistent() {
		return isPersistent;
	}

	public SafeKeeperValueObject setIsPersistent(Boolean isPersistent) {
		this.isPersistent = isPersistent;
		return this;
	}

	public Long getTimeout() {
		return timeout;
	}

	public SafeKeeperValueObject setTimeout(long timeout) {
		this.timeout = timeout;
		return this;
	}

	public int getCookieTimeout() {
		if(!isPersistent) {
			return -1;
		}
		if(timeout == SafeKeeperValueObject.NEVER_EXPIRE) {
			return Integer.MAX_VALUE;
		}
		return timeout.intValue();
	}

	/**
	 * 构建模式创建对象
	 * @param config 配置
	 */
	public void build(SafeKeeperConfiguration config) {
		if(device == null) {
			device = SafeKeeperConstant.DEFAULT_LOGIN_DEVICE;
		}
		if(isPersistent == null) {
			isPersistent = true;
		}
		if(timeout == null) {
			timeout = config.getTimeout();
		}
	}

	/**
	 * 创建默认的对象
	 * @return 对象数据
	 */
	public static SafeKeeperValueObject create() {
		return new SafeKeeperValueObject();
	}

}
