package cn.safekeeper.common.configuration;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 全局异常体系code，消息制定，也可以通过外部使用者自己的架构体系code和msg来指定，框架不强求
 * 分为三种异常板块
 * 1，权限板块 permission
 * 2，角色板块 role
 * 3，认证板块 authentication
 * 4，锁定板块 lock
 * 5，框架本身 safe
 * @author skylark
 */
@Setter
@Getter
public class SafeKeeperCodeMsgConfiguration {

    /**********************PERMISSION*******************************/
    private int permissionNotHas=1000;
    private String permissionNotHasMessage="没有权限";

    /************************ROLE**********************************/

    private int rolNotHas=2000;
    private String roleNotHas_message="没有角色";

    /*********************AUTHENTICATION***************************/

    private int authenticationNotToken=3000;
    private String authenticationNotTokenMessage="未提供token";

    private int authenticationInvalidToken = 3001;
    private String authenticationInvalidTokenMessage = "token无效";

    private int authenticationTokenTimeout = 3002;
    private String authenticationTokenTimeoutMessage = "token已过期";

    private int authenticationBeReplaced = 3003;
    private String authenticationBeReplacedMessage= "token已被顶下线";

    private int authenticationKickOut = 3004;
    private String authenticationKickOutMessage = "token已被踢下线";

    private int authenticationNoLogin = 3005;
    private String authenticationNoLoginMessage = "当前会话未登录";

    /*************************LOCK*******************************/

    private int lockTheUser = 4000;
    private String lockTheUserMessage = "锁定用户";

    private int lockTheUserByTime = 4001;
    private String lockTheUserByTimeMessage = "锁定用户时长";

}
