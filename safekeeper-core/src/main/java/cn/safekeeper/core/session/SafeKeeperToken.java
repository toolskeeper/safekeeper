package cn.safekeeper.core.session;

import java.io.Serializable;

/**
 * Token签名
 * 挂在Session上的token签名
 * @author skylark
 *
 */
public class SafeKeeperToken implements Serializable {
	/**
	 * token值
	 */
	private String value;
	/**
	 * 所在设备标识
	 */
	private String device;
	/** 构建一个 */
	public SafeKeeperToken() {
	}
	/**
	 * 构建一个
	 * @param value  token值
	 * @param device 所在设备标识
	 */
	public SafeKeeperToken(String value, String device) {
		this.value = value;
		this.device = device;
	}

	/**
	 * @return token value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @return token登录设备 
	 */
	public String getDevice() {
		return device;
	}

	@Override
	public String toString() {
		return "TokenSign [value=" + value + ", device=" + device + "]";
	}

}
