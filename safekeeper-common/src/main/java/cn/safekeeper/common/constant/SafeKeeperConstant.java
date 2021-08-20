package cn.safekeeper.common.constant;

/**
 * 全局常量
 * @author skylark
 */
public class SafeKeeperConstant {

    /**
     * 默认登录设备名称
     */
    public static final String DEFAULT_LOGIN_DEVICE="safe";

    /**
     * 如果token为本次请求新创建的，key存储在当前request中
     */
    public static final String CURRENT_CREATED_KEY = "CURRENT_CREATED_KEY_";

    /**
     * 如果本次请求已经验证过[无操作过期], 存储在当前request中
     */
    public static final String TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY = "TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY_";

    /**
     * 常量key标记: 在进行临时身份切换时使用的key
     */
    public static final String SWITCH_TO_SAVE_KEY = "SWITCH_TO_SAVE_KEY_";

    /**
     * 常量key标记: 在进行Token二级验证时使用的key
     */
    public static final String SAFE_AUTH_SAVE_KEY = "SAFE_AUTH_SAVE_KEY_";

    /**
     * 连接符号
     */
    public static final String TOKEN_CONNECTOR_CHAT  = "-";

    /**
     * 切面、拦截器、过滤器等各种组件的注册优先级顺序
     */
    public static final int ASSEMBLY_ORDER = -99;

    /** 异常标记值 */
    public static final String BE_VALUE = "disable";
}
