package cn.safekeeper.core.manager;

import cn.safekeeper.common.configuration.SafeKeeperCodeMsgConfiguration;
import cn.safekeeper.common.configuration.SafeKeeperConfiguration;
import cn.safekeeper.common.exception.SafeKeeperException;
import cn.safekeeper.common.model.SafeKeeperAuthorizationCallBack;
import cn.safekeeper.common.model.SafeKeeperContext;
import cn.safekeeper.common.utils.SafeKeeperUtils;
import cn.safekeeper.core.logic.DefaultSafeKeeperLogicAction;
import cn.safekeeper.core.logic.SafeKeeperLogicAction;
import cn.safekeeper.core.listener.DefaultSafeKeeperTokenListener;
import cn.safekeeper.core.listener.SafeKeeperTokenListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SafeKeeper管理中心
 * @author skylark
 */
public class SafeKeeperManager {

    /**
     * 配置文件 Bean
     */
    private static SafeKeeperConfiguration config;
    public static void setConfig(SafeKeeperConfiguration config) {
        SafeKeeperManager.config = config;
        if(config.isPrint()) {
            //打印logo
            SafeKeeperUtils.printSaToken();
        }
    }
    public static SafeKeeperConfiguration getConfig() {
        if (config == null) {
            synchronized (SafeKeeperConfiguration.class) {
                if (config == null) {
                    //加入没有配置初始化，就直接fatal中断
                    System.err.println("config 不能为空，没有默认初始化，程序直接停止...");
                    System.exit(1);
                }
            }
        }
        return config;
    }

    /**
     * 持久化 Bean
     */
    private static SafeKeeperTokenRealm safeKeeperTokenRealm;
    public static void setSafeKeeperTokenRealm(SafeKeeperTokenRealm safeKeeperTokenRealm) {
        SafeKeeperManager.safeKeeperTokenRealm = safeKeeperTokenRealm;
    }
    public static SafeKeeperTokenRealm getSafeKeeperTokenRealm() {
        if (safeKeeperTokenRealm == null) {
            synchronized (SafeKeeperManager.class) {
                if (safeKeeperTokenRealm == null) {
                    System.err.println("safeKeeperTokenDaoStorage 不能为空，没有默认初始化，程序直接停止...");
                    System.exit(1);
                }
            }
        }
        return safeKeeperTokenRealm;
    }
    /**
     * 权限认证 Bean
     */
    private static SafeKeeperAuthorizationCallBack authorizationCallBack;
    public static void setSafeKeeperAuthorizationCallBack(SafeKeeperAuthorizationCallBack authorizationCallBack) {
        SafeKeeperManager.authorizationCallBack = authorizationCallBack;
    }
    public static SafeKeeperAuthorizationCallBack getSafeKeeperAuthorizationCallBack() {
        if (authorizationCallBack == null) {
            synchronized (SafeKeeperManager.class) {
                if (authorizationCallBack == null) {
                    System.err.println("authorizationCallBack 不能为空，没有默认初始化，程序直接停止...");
                    System.exit(1);
                }
            }
        }
        return authorizationCallBack;
    }

    /**
     * 框架行为逻辑执行 Bean
     */
    private static SafeKeeperLogicAction safeKeeperLogicAction;
    public static void setSafeKeeperLogicAction(SafeKeeperLogicAction safeKeeperLogicAction) {
        SafeKeeperManager.safeKeeperLogicAction = safeKeeperLogicAction;
    }
    public static SafeKeeperLogicAction getSafeKeeperLogicAction() {
        if (safeKeeperLogicAction == null) {
            synchronized (SafeKeeperManager.class) {
                if (safeKeeperLogicAction == null) {
                    setSafeKeeperLogicAction(new DefaultSafeKeeperLogicAction());
                }
            }
        }
        return safeKeeperLogicAction;
    }

    /**
     * 容器操作 Bean
     */
    private static SafeKeeperContext safeKeeperContext;
    public static void setSafeKeeperContext(SafeKeeperContext safeKeeperContext) {
        SafeKeeperManager.safeKeeperContext = safeKeeperContext;
    }
    public static SafeKeeperContext getSafeKeeperContext() {
        if (safeKeeperContext == null) {
            synchronized (SafeKeeperManager.class) {
                if (safeKeeperContext == null) {
                    System.err.println("safeKeeperContext 不能为空，没有默认初始化，程序直接停止...");
                    System.exit(1);
                }
            }
        }
        return safeKeeperContext;
    }

    /**
     * 侦听器 Bean
     */
    private static SafeKeeperTokenListener tokenListener;
    private static volatile boolean isTokenListenerInit=false;
    public static void setSafeKeeperTokenListener(SafeKeeperTokenListener tokenListener) {
        if(isTokenListenerInit){
            return;
        }
        isTokenListenerInit=true;
        SafeKeeperManager.tokenListener = tokenListener;
    }
    public static SafeKeeperTokenListener getSafeKeeperTokenListener() {
        if (tokenListener == null) {
            synchronized (SafeKeeperManager.class) {
                if (tokenListener == null) {
                    //初始化默认的监听器
                    setSafeKeeperTokenListener(new DefaultSafeKeeperTokenListener());
                }
            }
        }
        return tokenListener;
    }

    /**
     * 状态消息配置 Bean
     */
    private static SafeKeeperCodeMsgConfiguration safeKeeperCodeMsgConfiguration;
    public static void setSafeKeeperCodeMsgConfiguration(SafeKeeperCodeMsgConfiguration safeKeeperCodeMsgConfiguration) {
        SafeKeeperManager.safeKeeperCodeMsgConfiguration = safeKeeperCodeMsgConfiguration;
    }
    public static SafeKeeperCodeMsgConfiguration getSafeKeeperCodeMsg() {
        if (safeKeeperCodeMsgConfiguration == null) {
            synchronized (SafeKeeperManager.class) {
                if (safeKeeperCodeMsgConfiguration == null) {
                    //初始化默认的监听器
                    setSafeKeeperCodeMsgConfiguration(new SafeKeeperCodeMsgConfiguration());
                }
            }
        }
        return safeKeeperCodeMsgConfiguration;
    }



    /**
     * 所有安全框架维度逻辑对象集合
     */
    public static Map<String, SafeKeeperProcessor> safeKeeperLogicMap = new ConcurrentHashMap<>();

    /**
     * 放入集合对象
     * @param safeKeeperProcessor 逻辑对象
     */
    public static void putSafeKeeperProcessor(SafeKeeperProcessor safeKeeperProcessor) {
        safeKeeperLogicMap.put(safeKeeperProcessor.getLoginType(), safeKeeperProcessor);
    }

    /**
     * 根据维度key获取逻辑对象
     * @param loginType 维度key数据
     * @return 逻辑对象
     */
    public static SafeKeeperProcessor getSafeKeeperProcessor(String loginType) {
        if(SafeKeeperUtils.isEmpty(loginType)){
            throw new SafeKeeperException("loginType must not null");
        }
        // 如果type为空则返回框架内置的
        SafeKeeperProcessor safeKeeperProcessor = safeKeeperLogicMap.get(loginType);
        if(safeKeeperProcessor ==null){
            return new SafeKeeperProcessor(loginType);
        }
        return safeKeeperProcessor;
    }

}
