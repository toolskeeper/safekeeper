package cn.safekeeper.core.manager;

import cn.safekeeper.common.annotations.SafeKeeperCondition;
import cn.safekeeper.common.annotations.SafeKeeperHasLogin;
import cn.safekeeper.common.annotations.SafeKeeperHasPermission;
import cn.safekeeper.common.annotations.SafeKeeperHasRole;
import cn.safekeeper.common.configuration.SafeKeeperCodeMsgConfiguration;
import cn.safekeeper.common.configuration.SafeKeeperConfiguration;
import cn.safekeeper.common.constant.SafeKeeperConstant;
import cn.safekeeper.common.exception.*;
import cn.safekeeper.common.model.*;
import cn.safekeeper.common.function.SafeKeeperFunction;
import cn.safekeeper.common.utils.SafeKeeperUtils;
import cn.safekeeper.core.session.SafeKeeperSession;
import cn.safekeeper.core.session.SafeKeeperToken;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 权限验证，逻辑实现类
 * @author skylark
 */
@Getter
@Setter
public class SafeKeeperProcessor {
    private static final Logger LOGGER= LoggerFactory.getLogger(SafeKeeperProcessor.class);
    /**
     * safekeeper多维度
     */
    public String loginType;

    /**
     * 禁用标志拼接
     */
    private String forbidden_flag="forbidden";

    /**
     * token相关处理组件
     */
    private SafeKeeperTokenProcessor safeKeeperTokenProcessor;

    /**
     * 认证处理组件
     */
    private SafeKeeperAuthenticationProcessor safeKeeperAuthenticationProcessor;

    /**
     * 消息状态配置类
     */
    private SafeKeeperCodeMsgConfiguration safeKeeperCodeMsgConfiguration= SafeKeeperManager.getSafeKeeperCodeMsg();

    /**
     * 初始化StpLogic, 并指定账号类型
     * @param loginType 账号体系标识
     */
    public SafeKeeperProcessor(String loginType) {
        LOGGER.debug("初始化SafeKeeperProcessor loginType：【"+loginType+"】");
        if(SafeKeeperUtils.isEmpty(loginType)){
            throw new SafeKeeperException("loginType must not null");
        }

        this.loginType = loginType;
        safeKeeperTokenProcessor=new SafeKeeperTokenProcessor(loginType);
        safeKeeperAuthenticationProcessor=new SafeKeeperAuthenticationProcessor(loginType,safeKeeperTokenProcessor);
        SafeKeeperManager.putSafeKeeperProcessor(this);
        LOGGER.debug("初始化SafeKeeperProcessor完成...并且加入到SafeKeeperManager");
    }

    /**
     * 获取当前登录维度类型
     * @return 类型数据
     */
    public String getLoginType(){
        return safeKeeperTokenProcessor.getLoginType();
    }

    /**
     * 获取token名称
     * @return token
     */
    public String getSafeKeeperTokenName() {
        return safeKeeperTokenProcessor.getTokenNameFromConfig();
    }

    /**
     * 创建token
     * @param loginId 登录成功后的业务id
     * @return 返回token
     */
    public String createKeeperToken(Object loginId) {
        return safeKeeperTokenProcessor.createKeeperToken(loginId);
    }

    /**
     * 在当前会话中加入token数据
     * @param tokenValue token
     * @param cookieTimeout 存活时间(秒)
     */
    public void setSafeKeeperToken(String tokenValue, int cookieTimeout){
        safeKeeperTokenProcessor.setSafeKeeperToken(tokenValue,cookieTimeout);
    }

    /**
     * 获取当前TokenValue
     * @return 当前tokenValue
     */
    public String getTokenValue(){
        return safeKeeperTokenProcessor.getTokenValue();
    }
    /**
     * 获取当前会话的Token信息
     * @return token信息
     */
    public SafeKeeperTokenInfo getTokenInfo() {
        SafeKeeperTokenInfo info = new SafeKeeperTokenInfo();
        info.setTokenName(getSafeKeeperTokenName());
        info.setTokenValue(getTokenValue());
        info.setIsLogin(isLogin());
        info.setLoginId(safeKeeperAuthenticationProcessor.getLoginIdDefaultNull());
        info.setLoginType(getLoginType());
        info.setTokenTimeout(getTokenTimeout());
        info.setSessionTimeout(getSessionTimeout());
        info.setTokenSessionTimeout(getTokenSessionTimeout());
        info.setTokenActivityTimeout(getTokenActivityTimeout());
        info.setLoginDevice(getLoginDevice());
        LOGGER.debug("SafeKeeperTokenInfo对象数据信息:{}",info);
        return info;
    }
    /**
     * 安全登录入口
     */
    public void login(Object id) {
        safeKeeperAuthenticationProcessor.login(id, new SafeKeeperValueObject());
    }


    /**
     * 安全登录入口
     * @param id 业务id
     * @param device 设备标识
     */
    public void login(Object id, String device) {
        safeKeeperAuthenticationProcessor.login(id, new SafeKeeperValueObject().setDevice(device));
    }

    /**
     * 会话登录【记住模式】
     * @param id 业务id
     * @param isPersistent 持久化
     */
    public void login(Object id, boolean isPersistent) {
        safeKeeperAuthenticationProcessor.login(id, new SafeKeeperValueObject().setIsPersistent(isPersistent));
    }

    /**
     * 会话注销
     */
    public void logout() {
        safeKeeperAuthenticationProcessor.logout();
    }


    /**
     * 会话注销，根据账号id （踢人下线）
     * @param loginId 账号id
     */
    public void logoutByLoginId(Object loginId) {
        safeKeeperAuthenticationProcessor.logoutByLoginId(loginId, null);
    }



    /**
     * 当前会话是否已经登录
     * @return 是否已登录
     */
    public boolean isLogin() {
        return safeKeeperAuthenticationProcessor.getLoginIdDefaultNull() != null;
    }

    /**
     * 检验当前会话是否已经登录
     */
    public void checkLogin() {
        safeKeeperAuthenticationProcessor.getLoginId();
    }


    /**
     * 通过token获取id
     * @param tokenValue token
     * @return 账号id
     */
    public Object getLoginIdByToken(String tokenValue) {
        return safeKeeperAuthenticationProcessor.getLoginIdNotHandle(tokenValue);
    }

    /**
     * 获取指定key的Session, 如果Session尚未创建，isCreate=是否新建并返回
     * @param sessionId SessionId
     * @param isCreate 是否新建
     * @return Session对象
     */
    public SafeKeeperSession getSessionBySessionId(String sessionId, boolean isCreate) {
        SafeKeeperSession safeKeeperSession = SafeKeeperManager.getSafeKeeperTokenRealm().getSession(sessionId);
        if(safeKeeperSession == null && isCreate) {
            safeKeeperSession = SafeKeeperManager.getSafeKeeperLogicAction().createSession(sessionId);
            SafeKeeperManager.getSafeKeeperTokenRealm().setSession(safeKeeperSession, getConfig().getTimeout());
        }
        return safeKeeperSession;
    }

    /**
     * 获取指定key的Session, 如果Session尚未创建，则返回null
     * @param sessionId SessionId
     * @return Session对象
     */
    public SafeKeeperSession getSessionBySessionId(String sessionId) {
        return getSessionBySessionId(sessionId, false);
    }

    /**
     * 获取指定账号id的Session, 如果Session尚未创建，isCreate=是否新建并返回
     * @param loginId 账号id
     * @param isCreate 是否新建
     * @return Session对象
     */
    public SafeKeeperSession getSessionByLoginId(Object loginId, boolean isCreate) {
        return getSessionBySessionId(splicingKeySession(loginId), isCreate);
    }

    /**
     * 获取指定账号id的Session，如果Session尚未创建，则新建并返回
     * @param loginId 账号id
     * @return Session对象
     */
    public SafeKeeperSession getSessionByLoginId(Object loginId) {
        return getSessionByLoginId(loginId, true);
    }

    /**
     * 获取当前会话的Session, 如果Session尚未创建，isCreate=是否新建并返回
     * @param isCreate 是否新建
     * @return Session对象
     */
    public SafeKeeperSession getSession(boolean isCreate) {
        return getSessionByLoginId(safeKeeperAuthenticationProcessor.getLoginId(), isCreate);
    }

    /**
     * 获取当前会话的Session，如果Session尚未创建，则新建并返回
     * @return Session对象
     */
    public SafeKeeperSession getSession() {
        return getSession(true);
    }

    /**
     * 获取指定Token-Session，如果Session尚未创建，isCreate代表是否新建并返回
     * @param tokenValue token值
     * @param isCreate 是否新建
     * @return session对象
     */
    public SafeKeeperSession getTokenSessionByToken(String tokenValue, boolean isCreate) {
        return getSessionBySessionId(splicingKeyTokenSession(tokenValue), isCreate);
    }

    /**
     * 获取指定Token-Session，如果Session尚未创建，则新建并返回
     * @param tokenValue Token值
     * @return Session对象
     */
    public SafeKeeperSession getTokenSessionByToken(String tokenValue) {
        return getSessionBySessionId(splicingKeyTokenSession(tokenValue), true);
    }

    /**
     * 获取当前Token-Session，如果Session尚未创建，isCreate代表是否新建并返回
     * @param isCreate 是否新建
     * @return Session对象
     */
    public SafeKeeperSession getTokenSession(boolean isCreate) {
        // 如果配置了需要校验登录状态，则验证一下
        if(getConfig().getTokenSessionCheckLogin()) {
            checkLogin();
        } else {
            // 如果配置忽略token登录校验，则必须保证token不为null (token为null的时候随机创建一个)
            String tokenValue = getTokenValue();
            if(tokenValue == null || Objects.equals(tokenValue, "")) {
                // 随机一个token送给Ta
                tokenValue = createKeeperToken(null);
                // 写入 [最后操作时间]
                setLastActivityToNow(tokenValue);
                // 在当前会话写入这个tokenValue
                int cookieTimeout = (int)(getConfig().getTimeout() == SafeKeeperTokenRealm.NEVER_EXPIRE ? Integer.MAX_VALUE : getConfig().getTimeout());
                setSafeKeeperToken(tokenValue, cookieTimeout);
            }
        }
        // 返回这个token对应的专属session
        return getSessionBySessionId(splicingKeyTokenSession(getTokenValue()), isCreate);
    }

    /**
     * 获取当前Token-Session，如果Session尚未创建，则新建并返回
     * @return Session对象
     */
    public SafeKeeperSession getTokenSession() {
        return getTokenSession(true);
    }

    /**
     * 写入指定token的 [最后操作时间] 为当前时间戳
     * @param tokenValue 指定token
     */
    protected void setLastActivityToNow(String tokenValue) {
        // 如果token == null 或者 设置了[永不过期], 则立即返回
        if(tokenValue == null || getConfig().getActivityTimeout() == SafeKeeperTokenRealm.NEVER_EXPIRE) {
            return;
        }
        // 将[最后操作时间]标记为当前时间戳
        SafeKeeperManager.getSafeKeeperTokenRealm().set(splicingKeyLastActivityTime(tokenValue), String.valueOf(System.currentTimeMillis()), getConfig().getTimeout());
    }

    /**
     * 清除指定token的 [最后操作时间]
     * @param tokenValue 指定token
     */
    protected void clearLastActivity(String tokenValue) {
        // 如果token == null 或者 设置了[永不过期], 则立即返回
        if(tokenValue == null || getConfig().getActivityTimeout() == SafeKeeperTokenRealm.NEVER_EXPIRE) {
            return;
        }
        // 删除[最后操作时间]
        SafeKeeperManager.getSafeKeeperTokenRealm().delete(splicingKeyLastActivityTime(tokenValue));
        // 清除标记
        SafeKeeperManager.getSafeKeeperContext().getStorage().delete((SafeKeeperConstant.TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY));
    }

    /**
     * 检查指定token 是否已经[临时过期]，如果已经过期则抛出异常
     * @param tokenValue 指定token
     */
    public void checkActivityTimeout(String tokenValue) {
        // 如果token == null 或者 设置了[永不过期], 则立即返回
        if(tokenValue == null || getConfig().getActivityTimeout() == SafeKeeperTokenRealm.NEVER_EXPIRE) {
            return;
        }
        // 如果本次请求已经有了[检查标记], 则立即返回
        SafeKeeperStorage storage = SafeKeeperManager.getSafeKeeperContext().getStorage();
        if(storage.get(SafeKeeperConstant.TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY) != null) {
            return;
        }
        // ------------ 验证是否已经 [临时过期]
        // 获取 [临时剩余时间]
        long timeout = getTokenActivityTimeoutByToken(tokenValue);
        // -1 代表此token已经被设置永不过期，无须继续验证
        if(timeout == SafeKeeperTokenRealm.NEVER_EXPIRE) {
            return;
        }
        // -2 代表已过期，抛出异常
        if(timeout == SafeKeeperTokenRealm.NOT_VALUE_EXPIRE) {
            SafeKeeperLoginException exception=new SafeKeeperLoginException.
                    Builder().
                    setCode(safeKeeperCodeMsgConfiguration.getAuthenticationTokenTimeout())
                    .setMessage(safeKeeperCodeMsgConfiguration.getAuthenticationTokenTimeoutMessage())
                    .setLoginType(loginType).build();
            throw exception;
        }
        // --- 至此，验证已通过

        // 打上[检查标记]，标记一下当前请求已经通过验证，避免一次请求多次验证，造成不必要的性能消耗
        storage.set(SafeKeeperConstant.TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY, true);
    }

    /**
     * 检查当前token 是否已经[临时过期]，如果已经过期则抛出异常
     */
    public void checkActivityTimeout() {
        checkActivityTimeout(getTokenValue());
    }

    /**
     * 续签指定token：(将 [最后操作时间] 更新为当前时间戳)
     * @param tokenValue 指定token
     */
    public void updateLastActivityToNow(String tokenValue) {
        // 如果token == null 或者 设置了[永不过期], 则立即返回
        if(tokenValue == null || getConfig().getActivityTimeout() == SafeKeeperTokenRealm.NEVER_EXPIRE) {
            return;
        }
        SafeKeeperManager.getSafeKeeperTokenRealm().update(splicingKeyLastActivityTime(tokenValue), String.valueOf(System.currentTimeMillis()));
    }

    /**
     * 续签当前token：(将 [最后操作时间] 更新为当前时间戳)
     * <h1>请注意: 即时token已经 [临时过期] 也可续签成功，
     * 如果此场景下需要提示续签失败，可在此之前调用 checkActivityTimeout() 强制检查是否过期即可 </h1>
     */
    public void updateLastActivityToNow() {
        updateLastActivityToNow(getTokenValue());
    }

    /**
     * 获取当前登录者的token剩余有效时间 (单位: 秒)
     * @return token剩余有效时间
     */
    public long getTokenTimeout() {
        return SafeKeeperManager.getSafeKeeperTokenRealm().getTimeout(splicingKeyTokenValue(getTokenValue()));
    }

    /**
     * 获取指定loginId的token剩余有效时间 (单位: 秒)
     * @param loginId 指定loginId
     * @return token剩余有效时间
     */
    public long getTokenTimeoutByLoginId(Object loginId) {
        return SafeKeeperManager.getSafeKeeperTokenRealm().getTimeout(splicingKeyTokenValue(getTokenValueByLoginId(loginId)));
    }

    /**
     * 获取当前登录者的Session剩余有效时间 (单位: 秒)
     * @return token剩余有效时间
     */
    public long getSessionTimeout() {
        return getSessionTimeoutByLoginId(safeKeeperAuthenticationProcessor.getLoginIdDefaultNull());
    }

    /**
     * 获取指定loginId的Session剩余有效时间 (单位: 秒)
     * @param loginId 指定loginId
     * @return token剩余有效时间
     */
    public long getSessionTimeoutByLoginId(Object loginId) {
        return SafeKeeperManager.getSafeKeeperTokenRealm().getSessionTimeout(splicingKeySession(loginId));
    }

    /**
     * 获取当前token的专属Session剩余有效时间 (单位: 秒)
     * @return token剩余有效时间
     */
    public long getTokenSessionTimeout() {
        return getTokenSessionTimeoutByTokenValue(getTokenValue());
    }

    /**
     * 获取指定token的专属Session剩余有效时间 (单位: 秒)
     * @param tokenValue 指定token
     * @return token剩余有效时间
     */
    public long getTokenSessionTimeoutByTokenValue(String tokenValue) {
        return SafeKeeperManager.getSafeKeeperTokenRealm().getSessionTimeout(splicingKeyTokenSession(tokenValue));
    }

    /**
     * 获取当前token[临时过期]剩余有效时间 (单位: 秒)
     * @return token[临时过期]剩余有效时间
     */
    public long getTokenActivityTimeout() {
        return getTokenActivityTimeoutByToken(getTokenValue());
    }

    /**
     * 获取指定token[临时过期]剩余有效时间 (单位: 秒)
     * @param tokenValue 指定token
     * @return token[临时过期]剩余有效时间
     */
    public long getTokenActivityTimeoutByToken(String tokenValue) {
        // 如果token为null , 则返回 -2
        if(tokenValue == null) {
            return SafeKeeperTokenRealm.NOT_VALUE_EXPIRE;
        }
        // 如果设置了永不过期, 则返回 -1
        if(getConfig().getActivityTimeout() == SafeKeeperTokenRealm.NEVER_EXPIRE) {
            return SafeKeeperTokenRealm.NEVER_EXPIRE;
        }
        // ------ 开始查询
        // 获取相关数据
        String keyLastActivityTime = splicingKeyLastActivityTime(tokenValue);
        String lastActivityTimeString = SafeKeeperManager.getSafeKeeperTokenRealm().get(keyLastActivityTime);
        // 查不到，返回-2
        if(lastActivityTimeString == null) {
            return SafeKeeperTokenRealm.NOT_VALUE_EXPIRE;
        }
        // 计算相差时间
        long lastActivityTime = Long.parseLong(lastActivityTimeString);
        long apartSecond = (System.currentTimeMillis() - lastActivityTime) / 1000;
        long timeout = getConfig().getActivityTimeout() - apartSecond;
        // 如果 < 0， 代表已经过期 ，返回-2
        if(timeout < 0) {
            return SafeKeeperTokenRealm.NOT_VALUE_EXPIRE;
        }
        return timeout;
    }


    /**
     * 指定账号id是否含有角色标识, 返回true或false
     * @param loginId 账号id
     * @param role 角色标识
     * @return 是否含有指定角色标识
     */
    public boolean hasRole(Object loginId, String role) {
        List<String> roleList = SafeKeeperManager.getSafeKeeperAuthorizationCallBack().getRoleList(loginId, loginType);
        return SafeKeeperManager.getSafeKeeperLogicAction().hasElement(roleList, role);
    }

    /**
     * 当前账号是否含有指定角色标识, 返回true或false
     * @param role 角色标识
     * @return 是否含有指定角色标识
     */
    public boolean hasRole(String role) {
        return hasRole(safeKeeperAuthenticationProcessor.getLoginId(), role);
    }

    /**
     * 当前账号是否含有指定角色标识, 如果验证未通过，则抛出异常: NotRoleException
     * @param role 角色标识
     */
    public void checkRole(String role) {
        if(!hasRole(role)) {
            SafeKeeperRoleException exception=new SafeKeeperRoleException.Builder().
                    setCode(safeKeeperCodeMsgConfiguration.getRolNotHas())
                    .setRole(role)
                    .setLoginType(loginType).build();
            throw exception;
        }
    }

    /**
     * 当前账号是否含有指定角色标识 [指定多个，必须全部验证通过]
     * @param roleArray 角色标识数组
     */
    public void checkRoleAnd(String... roleArray){
        Object loginId = safeKeeperAuthenticationProcessor.getLoginId();
        List<String> roleList = SafeKeeperManager.getSafeKeeperAuthorizationCallBack().getRoleList(loginId, loginType);
        for (String role : roleArray) {
            if(!SafeKeeperManager.getSafeKeeperLogicAction().hasElement(roleList, role)) {
                SafeKeeperRoleException exception=new SafeKeeperRoleException.Builder().
                        setCode(safeKeeperCodeMsgConfiguration.getRolNotHas())
                        .setRole(role)
                        .setLoginType(loginType).build();
                throw exception;
            }
        }
    }

    /**
     * 当前账号是否含有指定角色标识 [指定多个，只要其一验证通过即可]
     * @param roleArray 角色标识数组
     */
    public void checkRoleOr(String... roleArray){
        Object loginId = safeKeeperAuthenticationProcessor.getLoginId();
        List<String> roleList = SafeKeeperManager.getSafeKeeperAuthorizationCallBack().getRoleList(loginId, loginType);
        for (String role : roleArray) {
            if(SafeKeeperManager.getSafeKeeperLogicAction().hasElement(roleList, role)) {
                // 有的话提前退出
                return;
            }
        }
        if(roleArray.length > 0) {
            SafeKeeperRoleException exception=new SafeKeeperRoleException.Builder().
                    setCode(safeKeeperCodeMsgConfiguration.getRolNotHas())
                    .setRole(roleArray[0])
                    .setLoginType(loginType).build();
            throw exception;
        }
    }

    /**
     * 指定账号id是否含有指定权限, 返回true或false
     * @param loginId 账号id
     * @param permission 权限码
     * @return 是否含有指定权限
     */
    public boolean hasPermission(Object loginId, String permission) {
        List<String> permissionList = SafeKeeperManager.getSafeKeeperAuthorizationCallBack().getPermissionList(loginId, loginType);
        return SafeKeeperManager.getSafeKeeperLogicAction().hasElement(permissionList, permission);
    }

    /**
     * 当前账号是否含有指定权限, 返回true或false
     * @param permission 权限码
     * @return 是否含有指定权限
     */
    public boolean hasPermission(String permission) {
        return hasPermission(safeKeeperAuthenticationProcessor.getLoginId(), permission);
    }

    /**
     * 当前账号是否含有指定权限, 如果验证未通过，则抛出异常: NotPermissionException
     * @param permission 权限码
     */
    public void checkPermission(String permission) {
        if(hasPermission(permission) == false) {
            SafeKeeperPermissionException exception=new SafeKeeperPermissionException.Builder()
                    .setCode(safeKeeperCodeMsgConfiguration.getPermissionNotHas())
                    .setPermission(permission)
                    .setLoginType(loginType)
                    .build();
            throw exception;
        }
    }

    /**
     * 当前账号是否含有指定权限 [指定多个，必须全部验证通过]
     * @param permissionArray 权限码数组
     */
    public void checkPermissionAnd(String... permissionArray){
        Object loginId = safeKeeperAuthenticationProcessor.getLoginId();
        List<String> permissionList = SafeKeeperManager.getSafeKeeperAuthorizationCallBack().getPermissionList(loginId, loginType);
        for (String permission : permissionArray) {
            if(!SafeKeeperManager.getSafeKeeperLogicAction().hasElement(permissionList, permission)) {
                SafeKeeperPermissionException exception=new SafeKeeperPermissionException.Builder()
                        .setCode(safeKeeperCodeMsgConfiguration.getPermissionNotHas())
                        .setPermission(permission)
                        .setLoginType(loginType)
                        .build();
                throw exception;
            }
        }
    }

    /**
     * 当前账号是否含有指定权限 [指定多个，只要其一验证通过即可]
     * @param permissionArray 权限码数组
     */
    public void checkPermissionOr(String... permissionArray){
        Object loginId = safeKeeperAuthenticationProcessor.getLoginId();
        List<String> permissionList = SafeKeeperManager.getSafeKeeperAuthorizationCallBack().getPermissionList(loginId, loginType);
        for (String permission : permissionArray) {
            if(SafeKeeperManager.getSafeKeeperLogicAction().hasElement(permissionList, permission)) {
                // 有的话提前退出
                return;
            }
        }
        if(permissionArray.length > 0) {
            SafeKeeperPermissionException exception=new SafeKeeperPermissionException.Builder()
                    .setCode(safeKeeperCodeMsgConfiguration.getPermissionNotHas())
                    .setPermission(permissionArray[0])
                    .setLoginType(loginType)
                    .build();
            throw exception;
        }
    }


    /**
     * 获取指定账号id的tokenValue
     * 在配置为允许并发登录时，此方法只会返回队列的最后一个token，
     * 如果你需要返回此账号id的所有token，请调用 getTokenValueListByLoginId
     * @param loginId 账号id
     * @return token值
     */
    public String getTokenValueByLoginId(Object loginId) {
        return getTokenValueByLoginId(loginId, SafeKeeperConstant.DEFAULT_LOGIN_DEVICE);
    }

    /**
     * 获取指定账号id指定设备端的tokenValue
     * <p> 在配置为允许并发登录时，此方法只会返回队列的最后一个token，
     * 如果你需要返回此账号id的所有token，请调用 getTokenValueListByLoginId
     * @param loginId 账号id
     * @param device 设备标识
     * @return token值
     */
    public String getTokenValueByLoginId(Object loginId, String device) {
        List<String> tokenValueList = getTokenValueListByLoginId(loginId, device);
        return tokenValueList.size() == 0 ? null : tokenValueList.get(tokenValueList.size() - 1);
    }

    /**
     * 获取指定账号id的tokenValue集合
     * @param loginId 账号id
     * @return 此loginId的所有相关token
     */
    public List<String> getTokenValueListByLoginId(Object loginId) {
        return getTokenValueListByLoginId(loginId, SafeKeeperConstant.DEFAULT_LOGIN_DEVICE);
    }

    /**
     * 获取指定账号id指定设备端的tokenValue 集合
     * @param loginId 账号id
     * @param device 设备标识
     * @return 此loginId的所有相关token
     */
    public List<String> getTokenValueListByLoginId(Object loginId, String device) {
        // 如果session为null的话直接返回空集合
        SafeKeeperSession safeKeeperSession = getSessionByLoginId(loginId, false);
        if(safeKeeperSession == null) {
            return Collections.emptyList();
        }
        // 遍历解析
        List<SafeKeeperToken> safeKeeperTokenList = safeKeeperSession.getTokenList();
        List<String> tokenValueList = new ArrayList<>();
        for (SafeKeeperToken safeKeeperToken : safeKeeperTokenList) {
            if(safeKeeperToken.getDevice().equals(device)) {
                tokenValueList.add(safeKeeperToken.getValue());
            }
        }
        return tokenValueList;
    }

    /**
     * 返回当前会话的登录设备
     * @return 当前令牌的登录设备
     */
    public String getLoginDevice() {
        // 如果没有token，直接返回
        String tokenValue = getTokenValue();
        if(tokenValue == null) {
            return null;
        }
        // 如果还未登录，直接返回 null
        if(!isLogin()) {
            return null;
        }
        // 如果session为null的话直接返回 null
        SafeKeeperSession safeKeeperSession = getSessionByLoginId(safeKeeperAuthenticationProcessor.getLoginIdDefaultNull(), false);
        if(safeKeeperSession == null) {
            return null;
        }
        // 遍历解析
        List<SafeKeeperToken> safeKeeperTokenList = safeKeeperSession.getTokenList();
        for (SafeKeeperToken safeKeeperToken : safeKeeperTokenList) {
            if(safeKeeperToken.getValue().equals(tokenValue)) {
                return safeKeeperToken.getDevice();
            }
        }
        return null;
    }


    /**
     * 根据注解(@SafeKeeperHasLogin)鉴权
     * @param at 注解对象
     */
    public void checkByAnnotation(SafeKeeperHasLogin at) {
        this.checkLogin();
    }

    /**
     * 根据注解(@SafeKeeperHasRole)鉴权
     * @param at 注解对象
     */
    public void checkByAnnotation(SafeKeeperHasRole at) {
        String[] roleArray = at.roles();
        if(at.mode() == SafeKeeperCondition.AND) {
            this.checkRoleAnd(roleArray);
        } else {
            this.checkRoleOr(roleArray);
        }
    }

    /**
     * 根据注解(@SafeKeeperHasPermission)鉴权
     * @param at 注解对象
     */
    public void checkByAnnotation(SafeKeeperHasPermission at) {
        String[] permissionArray = at.permissions();
        if(at.mode() == SafeKeeperCondition.AND) {
            this.checkPermissionAnd(permissionArray);
        } else {
            this.checkPermissionOr(permissionArray);
        }
    }


    /**
     * 封禁指定账号
     * 此方法不会直接将此账号id踢下线
     * @param loginId 指定账号id
     * @param disableTime 封禁时间, 单位: 秒 （-1=永久封禁）
     */
    public void forbidden(Object loginId, long disableTime) {
        // 标注为已被封禁
        SafeKeeperManager.getSafeKeeperTokenRealm().set(safeKeeperAuthenticationProcessor.
                accountForbidden(loginId), SafeKeeperConstant.BE_VALUE, disableTime);
        SafeKeeperManager.getSafeKeeperTokenListener().doDisable(loginType, loginId, disableTime);
    }

    /**
     * 指定账号是否已被封禁 (true=已被封禁, false=未被封禁)
     * @param loginId 账号id
     * @return see note
     */
    public boolean isForbidden(Object loginId) {
        return SafeKeeperManager.getSafeKeeperTokenRealm().get(safeKeeperAuthenticationProcessor.accountForbidden(loginId)) != null;
    }

    /**
     * 获取指定账号剩余封禁时间，单位：秒（-1=永久封禁，-2=未被封禁）
     * @param loginId 账号id
     * @return see note
     */
    public long getForbiddenTime(Object loginId) {
        return SafeKeeperManager.getSafeKeeperTokenRealm().getTimeout(safeKeeperAuthenticationProcessor.accountForbidden(loginId));
    }

    /**
     * 解封指定账号
     * @param loginId 账号id
     */
    public void untieForbidden(Object loginId) {
        SafeKeeperManager.getSafeKeeperTokenRealm().delete(safeKeeperAuthenticationProcessor.accountForbidden(loginId));
        SafeKeeperManager.getSafeKeeperTokenListener().doUntieDisable(loginType, loginId);
    }

    /**
     * 临时切换身份为指定账号id
     * @param loginId 指定loginId
     */
    public void switchToTmp(Object loginId) {
        SafeKeeperManager.getSafeKeeperContext().getStorage().set(splicingKeySwitch(), loginId);
    }

    /**
     * 结束临时切换身份
     */
    public void endSwitchTmp() {
        SafeKeeperManager.getSafeKeeperContext().getStorage().delete(splicingKeySwitch());
    }

    /**
     * 在当前会话 开启二级认证
     * @param safeTime 维持时间 (单位: 秒)
     */
    public void openSecondSafe(long safeTime) {
        long eff = System.currentTimeMillis() + safeTime * 1000;
        getTokenSession().set(SafeKeeperConstant.SAFE_AUTH_SAVE_KEY, eff);
    }

    /**
     * 当前会话 是否处于二级认证时间内
     * @return true=二级认证已通过, false=尚未进行二级认证或认证已超时
     */
    public boolean isSecondSafe() {
        long eff = getTokenSession().get(SafeKeeperConstant.SAFE_AUTH_SAVE_KEY, 0L);
        if(eff == 0 || eff < System.currentTimeMillis()) {
            return false;
        }
        return true;
    }

    /**
     * 检查当前会话是否已通过二级认证，如未通过则抛出异常
     */
    public void checkSecondSafe() {
        if (!isSecondSafe()) {
            throw new SafeKeeperException();
        }
    }

    /**
     * 获取当前会话的二级认证剩余有效时间 (单位: 秒, 返回-2代表尚未通过二级认证)
     * @return 安全时间
     */
    public long getSecondSafeTime() {
        long eff = getTokenSession().get(SafeKeeperConstant.SAFE_AUTH_SAVE_KEY, 0L);
        if(eff == 0 || eff < System.currentTimeMillis()) {
            return SafeKeeperTokenRealm.NOT_VALUE_EXPIRE;
        }
        return (eff - System.currentTimeMillis()) / 1000;
    }

    /**
     * 在当前会话 结束二级认证
     */
    public void closeSecondSafe() {
        getTokenSession().delete(SafeKeeperConstant.SAFE_AUTH_SAVE_KEY);
    }

    /**
     * 拼接key： tokenValue 持久化 token-id
     * @param tokenValue token值
     * @return key
     */
    public String splicingKeyTokenValue(String tokenValue) {
        return getConfig().getSafeKeeperName() + ":" + loginType + ":token:" + tokenValue;
    }

    /**
     * 拼接key： Session 持久化
     * @param loginId 账号id
     * @return key
     */
    public String splicingKeySession(Object loginId) {
        return getConfig().getSafeKeeperName() + ":" + loginType + ":session:" + loginId;
    }

    /**
     * 拼接key： tokenValue的专属session
     * @param tokenValue token值
     * @return key
     */
    public String splicingKeyTokenSession(String tokenValue) {
        return getConfig().getSafeKeeperName() + ":" + loginType + ":token-session:" + tokenValue;
    }

    /**
     * 拼接key： 指定token的最后操作时间 持久化
     * @param tokenValue token值
     * @return key
     */
    public String splicingKeyLastActivityTime(String tokenValue) {
        return getConfig().getSafeKeeperName() + ":" + loginType + ":last-activity:" + tokenValue;
    }

    /**
     * 在进行身份切换时，使用的存储key
     * @return key
     */
    public String splicingKeySwitch() {
        return SafeKeeperConstant.SWITCH_TO_SAVE_KEY + loginType;
    }

    /**
     * 返回配置对象
     * @return 配置对象
     */
    public SafeKeeperConfiguration getConfig() {
        return SafeKeeperManager.getConfig();
    }
}
