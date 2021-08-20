package cn.safekeeper.common.configuration;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * SafeKeeper安全配置中心
 * @author skylark
 */
@Setter
@Getter
public class SafeKeeperConfiguration {


    /**
     * SafeKeeper版本号
     */
    public static final String SAFE_KEEPER_VERSION="1.0.0";

    /**
     * token标识
     */
    private String safeKeeperName="safekeeper";

    /**
     * 默认登录维度
     */
    private String defaultLoginType="safekeeper";

    /**
     * token前缀
     */
    private String tokenPrefix;

    private String cookieDomain="";

    /**
     * 过期时间为10天,如果设置-1就是永久
     */
    private long timeout = 10 * 24 * 60 * 60;

    /**
     * 是否可以同时进行登录
     */
    private boolean isConcurrentlyLogin=true;

    /**
     * 是否从参数中进行读取token来判断
     */
    private boolean isReadFromParam=true;
    /**
     * 是否从请求体里面读取token来判断
     */
    private boolean isReadFromBody=true;

    /**
     * 是否从请求的request中的header中来读取token
     */
    private boolean isReadFromHead=true;

    /**
     * 是否从请求的cookie中来读取token
     */
    private boolean isReadFromCookie=true;
    /**
     * 是否打印
     */
    private boolean isPrint=false;

    /**
     * 默认就是uuid
     */
    private String tokenType="uuid";

    /**
     * 是否支撑并发登录
     */
    private boolean isConcurrent=true;

    /**
     * 是否共享token
     */
    private boolean isShare=true;

    /**
     * 自动续期
     */
    private boolean autoRenew=true;

    /** 获取[token专属session]时是否必须登录 (如果配置为true，会在每次获取[token-session]时校验是否登录) */
    private Boolean tokenSessionCheckLogin = true;

    /**
     * token临时有效期 [指定时间内无操作就视为token过期] (单位: 秒), 默认-1 代表不限制
     * (例如可以设置为1800代表30分钟内无操作就过期)
     */
    private long activityTimeout = -1;

    /**
     * 安全拦截路由
     */
    private List<String> includeList = new ArrayList<String>(){};

    /**
     * 安全放行路由
     */
    private List<String> excludeList = new ArrayList<String>(){};
}
