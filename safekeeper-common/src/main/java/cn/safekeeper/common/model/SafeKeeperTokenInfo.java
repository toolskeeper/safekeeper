package cn.safekeeper.common.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * SafeKeeper实体
 * @author skylark
 *
 */
@Setter
@Getter
@ToString
public class SafeKeeperTokenInfo {

	/** token名称 */
	private String tokenName;

	/** token值 */
	private String tokenValue;

	/** 此token是否已经登录 */
	private Boolean isLogin;

	/** 此token对应的LoginId*/
	private Object loginId;

	/** 账号登录维度 */
	private String loginType;

	/** token剩余有效期 (单位: 秒) */
	private long tokenTimeout;

	/**session剩余有效时间 (单位: 秒) */
	private long sessionTimeout;

	/** Token-Session剩余有效时间 (单位: 秒) */
	private long tokenSessionTimeout;

	/** token剩余无操作有效时间 (单位: 秒) */
	private long tokenActivityTimeout;

	/** 登录设备标识 */
	private String loginDevice;

	/** 自定义数据 */
	private String tag;

}
