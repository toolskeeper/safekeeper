package cn.safekeeper.core.manager;

import cn.safekeeper.common.configuration.SafeKeeperCodeMsgConfiguration;
import cn.safekeeper.common.configuration.SafeKeeperConfiguration;
import cn.safekeeper.common.constant.SafeKeeperConstant;
import cn.safekeeper.common.exception.SafeKeeperLockedException;
import cn.safekeeper.common.exception.SafeKeeperLoginException;
import cn.safekeeper.common.model.SafeKeeperStorage;
import cn.safekeeper.common.model.SafeKeeperValueObject;
import cn.safekeeper.core.session.SafeKeeperSession;
import cn.safekeeper.core.session.SafeKeeperToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 认证相关的组件处理器
 * @author skylark
 */
public class SafeKeeperAuthenticationProcessor {

    private static final Logger LOGGER= LoggerFactory.getLogger(SafeKeeperAuthenticationProcessor.class);

    /**
     * safekeeper多维度
     */
    public String loginType;
    /**
     * 禁用标志拼接
     */
    private String forbidden_flag="forbidden";

    /**
     * token处理组件
     */
    private SafeKeeperTokenProcessor safeKeeperTokenProcessor;

    /**
     * 消息状态配置类
     */
    private SafeKeeperCodeMsgConfiguration safeKeeperCodeMsgConfiguration= SafeKeeperManager.getSafeKeeperCodeMsg();


    public SafeKeeperAuthenticationProcessor(String loginType,SafeKeeperTokenProcessor safeKeeperTokenProcessor){
        this.loginType=loginType;
        this.safeKeeperTokenProcessor=safeKeeperTokenProcessor;
    }

    /**
     * 安全登录入口
     */
    public void login(Object id) {
        login(id, new SafeKeeperValueObject());
    }


    /**
     * 安全登录入口
     * @param id 业务id
     * @param device 设备标识
     */
    public void login(Object id, String device) {
        login(id, new SafeKeeperValueObject().setDevice(device));
    }

    /**
     * 会话登录【记住模式】
     * @param id 业务id
     * @param isPersistent 持久化
     */
    public void login(Object id, boolean isPersistent) {
        login(id, new SafeKeeperValueObject().setIsPersistent(isPersistent));
    }

    /**
     * 指定账号是否已被封禁 (true=已被封禁, false=未被封禁)
     * @param loginId 账号id
     * @return see note
     */
    public boolean isDisable(Object loginId) {
        return SafeKeeperManager.getSafeKeeperTokenRealm().get(accountForbidden(loginId)) != null;
    }

    /**
     * 账号封禁key
     * @param loginId 账号id
     * @return key
     */
    public String accountForbidden(Object loginId) {
        return forbidden_flag+":"+SafeKeeperManager.getConfig().getSafeKeeperName() + ":" + loginType + ":" + loginId;
    }

    /**
     * 获取指定账号剩余封禁时间，单位：秒（-1=永久封禁，-2=未被封禁）
     * @param loginId 账号id
     * @return see note
     */
    public long getDisableTime(Object loginId) {
        return SafeKeeperManager.getSafeKeeperTokenRealm().getTimeout(accountForbidden(loginId));
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
     * 获取指定账号id的Session, 如果Session尚未创建，isCreate=是否新建并返回
     * @param loginId 账号id
     * @param isCreate 是否新建
     * @return Session对象
     */
    public SafeKeeperSession getSessionByLoginId(Object loginId, boolean isCreate) {
        return getSessionBySessionId(splicingKeySession(loginId), isCreate);
    }

    /**
     * 拼接key
     * @param loginId 账号id
     * @return key
     */
    public String splicingKeySession(Object loginId) {
        return SafeKeeperManager.getConfig().getSafeKeeperName() + ":" + loginType + ":session:" + loginId;
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
            SafeKeeperManager.getSafeKeeperTokenRealm().setSession(safeKeeperSession, SafeKeeperManager.getConfig().getTimeout());
        }
        return safeKeeperSession;
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
     * 拼接key： tokenValue 持久化 token-id
     * @param tokenValue token值
     * @return key
     */
    public String splicingKeyTokenValue(String tokenValue) {
        return SafeKeeperManager.getConfig().getSafeKeeperName() + ":" + loginType + ":token:" + tokenValue;
    }

    /**
     * 清除指定token的 [最后操作时间]
     * @param tokenValue 指定token
     */
    protected void clearLastActivity(String tokenValue) {
        // 如果token == null 或者 设置了[永不过期], 则立即返回
        if(tokenValue == null || SafeKeeperManager.getConfig().getActivityTimeout() == SafeKeeperTokenRealm.NEVER_EXPIRE) {
            return;
        }
        // 删除[最后操作时间]
        SafeKeeperManager.getSafeKeeperTokenRealm().delete(splicingKeyLastActivityTime(tokenValue));
        // 清除标记
        SafeKeeperManager.getSafeKeeperContext().getStorage().delete((SafeKeeperConstant.TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY));
    }

    /**
     * 拼接key： 指定token的最后操作时间 持久化
     * @param tokenValue token值
     * @return key
     */
    public String splicingKeyLastActivityTime(String tokenValue) {
        return SafeKeeperManager.getConfig().getSafeKeeperName() + ":" + loginType + ":last-activity:" + tokenValue;
    }

    /**
     * 安全登录业务逻辑
     * @param id 业务id
     * @param valueObject 登录值对象
     */
    public void login(Object id, SafeKeeperValueObject valueObject) {
        if(isDisable(id)) {
            LOGGER.warn("账号禁用,业务id{}",id);
            throw new SafeKeeperLockedException(SafeKeeperManager.getSafeKeeperCodeMsg().getLockTheUser()
                    ,SafeKeeperManager.getSafeKeeperCodeMsg().getLockTheUserMessage()+""+getDisableTime(id)+"",loginType);
        }
        SafeKeeperConfiguration config = SafeKeeperManager.getConfig();
        SafeKeeperTokenRealm dao = SafeKeeperManager.getSafeKeeperTokenRealm();
        valueObject.build(config);
        LOGGER.debug("SafeKeeperValueObject数据:{}",valueObject);
        String tokenValue = null;
        // 并发登录
        if(config.isConcurrent()) {
            // 如果配置为共享token, 则尝试从Session签名记录里取出token
            if(config.isShare()) {
                //则尝试从Session签名记录里取出token
                tokenValue = getTokenValueByLoginId(id, valueObject.getDevice());
            }
        } else {
            // 不允许并发登录
            // 此时session不为null，说明此账号在其他地方正在登录，现在需要先把其它地的同设备token标记为被顶下线
            SafeKeeperSession safeKeeperSession = getSessionByLoginId(id, false);
            if(safeKeeperSession != null) {
                List<SafeKeeperToken> safeKeeperTokenList = safeKeeperSession.getTokenList();
                for (SafeKeeperToken safeKeeperToken : safeKeeperTokenList) {
                    if(safeKeeperToken.getDevice().equals(valueObject.getDevice())) {
                        //将此token标记顶替
                        dao.update(splicingKeyTokenValue(safeKeeperToken.getValue()),
                                String.valueOf(SafeKeeperManager.getSafeKeeperCodeMsg().getAuthenticationBeReplaced()));
                        // 清理掉[token-最后操作时间]
                        clearLastActivity(safeKeeperToken.getValue());
                        // 清理session上的token签名记录
                        safeKeeperSession.removeTokenSign(safeKeeperToken.getValue());
                        SafeKeeperManager.getSafeKeeperTokenListener().doReplaced(loginType, id, safeKeeperToken.getValue(), safeKeeperToken.getDevice());
                    }
                }
            }
        }
        // 如果仍未成功创建tokenValue, 则开始生成一个全新的
        if(tokenValue == null) {
            tokenValue = safeKeeperTokenProcessor.createKeeperToken(id);
        }

        // 获取Session(如果还没有创建session, 则新建, 如果已经创建，则续期)
        SafeKeeperSession safeKeeperSession = getSessionByLoginId(id, false);
        if(safeKeeperSession == null) {
            safeKeeperSession = getSessionByLoginId(id,true);
            LOGGER.debug("新建session:{}",safeKeeperSession);
        } else {
            safeKeeperSession.updateMinTimeout(valueObject.getTimeout());
        }
        // 在session上记录token签名
        safeKeeperSession.addTokenSign(new SafeKeeperToken(tokenValue, valueObject.getDevice()));
        // 持久化其它数据 token -> id
        dao.set(splicingKeyTokenValue(tokenValue), String.valueOf(id), valueObject.getTimeout());
        // 写入最后操作时间
        setLastActivityToNow(tokenValue);
        // 在当前会话写入当前tokenValue
        safeKeeperTokenProcessor.setSafeKeeperToken(tokenValue, valueObject.getCookieTimeout());
        LOGGER.info("完成session创建");
        SafeKeeperManager.getSafeKeeperTokenListener().doLogin(loginType, id, valueObject);
    }



    /**
     * 写入指定token的 [最后操作时间] 为当前时间戳
     * @param tokenValue 指定token
     */
    protected void setLastActivityToNow(String tokenValue) {
        // 如果token == null 或者 设置了[永不过期], 则立即返回
        if(tokenValue == null || SafeKeeperManager.getConfig().getActivityTimeout() == SafeKeeperTokenRealm.NEVER_EXPIRE) {
            return;
        }
        // 将[最后操作时间]标记为当前时间戳
        SafeKeeperManager.getSafeKeeperTokenRealm().set(splicingKeyLastActivityTime(tokenValue), String.valueOf(System.currentTimeMillis()),
                SafeKeeperManager.getConfig().getTimeout());
    }

    /**
     * 会话注销
     */
    public void logout() {
        // 如果连token都没有，那么无需执行任何操作
        String tokenValue = safeKeeperTokenProcessor.getTokenValue();
        if(tokenValue == null) {
            return;
        }
        // 如果打开了cookie模式，第一步，先把cookie清除掉
        if(SafeKeeperManager.getConfig().isReadFromCookie()){
            SafeKeeperManager.getSafeKeeperContext().getResponse().deleteCookie(safeKeeperTokenProcessor.getSafeKeeperTokenName());
        }
        logoutByTokenValue(tokenValue);
    }

    /**
     * 会话注销，根据指定Token
     * @param tokenValue 指定token
     */
    public void logoutByTokenValue(String tokenValue) {
        // 1. 清理掉[token-最后操作时间]
        clearLastActivity(tokenValue);

        // 2. 清理Token-Session
        SafeKeeperManager.getSafeKeeperTokenRealm().delete(splicingKeyTokenSession(tokenValue));

        // 3. 尝试清除token-id键值对 (先从db中获取loginId值，如果根本查不到loginId，那么无需继续操作 )
        String loginId = getLoginIdNotHandle(tokenValue);
        if(loginId == null || SafeKeeperLoginException.ABNORMAL_LIST.contains(loginId)) {
            return;
        }
        SafeKeeperManager.getSafeKeeperTokenRealm().delete(splicingKeyTokenValue(tokenValue));
        //知监听器
        SafeKeeperManager.getSafeKeeperTokenListener().doLogout(loginType, loginId, tokenValue);
        //尝试清理Session上的token签名 (如果为null或已被标记为异常, 那么无需继续执行 )
        SafeKeeperSession safeKeeperSession = getSessionByLoginId(loginId, false);
        if(safeKeeperSession == null) {
            return;
        }
        safeKeeperSession.removeTokenSign(tokenValue);
    }

    /**
     * 拼接key
     * @param tokenValue token值
     * @return key
     */
    public String splicingKeyTokenSession(String tokenValue) {
        return SafeKeeperManager.getConfig().getSafeKeeperName() + ":" + loginType + ":token-session:" + tokenValue;
    }
    /**
     * 获取指定Token对应的账号id
     * @param tokenValue token值
     * @return 账号id
     */
    public String getLoginIdNotHandle(String tokenValue) {
        return SafeKeeperManager.getSafeKeeperTokenRealm().get(splicingKeyTokenValue(tokenValue));
    }

    /**
     * 会话注销，根据账号id设备标识 （踢人下线）
     * 当对方再次访问系统时，会抛出异常
     * @param loginId 账号id
     * @param device 设备标识 (填null代表所有注销设备)
     */
    public void logoutByLoginId(Object loginId, String device) {
        // 先获取这个账号的session, 如果为null，则不执行任何操作
        SafeKeeperSession safeKeeperSession = getSessionByLoginId(loginId, false);
        if(safeKeeperSession == null) {
            return;
        }

        // 循环token签名列表，开始删除相关信息
        List<SafeKeeperToken> safeKeeperTokenList = safeKeeperSession.getTokenList();
        for (SafeKeeperToken safeKeeperToken : safeKeeperTokenList) {
            if(device == null || safeKeeperToken.getDevice().equals(device)) {
                // 获取token
                String tokenValue = safeKeeperToken.getValue();
                // 清理掉token-最后操作时间
                clearLastActivity(tokenValue);
                // 标记：已被踢下线
                SafeKeeperManager.getSafeKeeperTokenRealm().update(splicingKeyTokenValue(tokenValue),
                        String.valueOf(safeKeeperCodeMsgConfiguration.getAuthenticationKickOut()));
                // 清理账号session上的token签名
                safeKeeperSession.removeTokenSign(tokenValue);
                // 通知监听器
                SafeKeeperManager.getSafeKeeperTokenListener().doLogoutByLoginId(loginType, loginId, tokenValue, safeKeeperToken.getDevice());
            }
        }
    }

    /**
     * 获取当前会话账号id, 如果未登录，则抛出异常
     * @return 账号id
     */
    public Object getLoginId() {
        // 如果获取不到token，则抛出: 无token
        String tokenValue = safeKeeperTokenProcessor.getTokenValue();
        if(tokenValue == null) {
            SafeKeeperLoginException exception=new SafeKeeperLoginException.
                    Builder().
                    setCode(safeKeeperCodeMsgConfiguration.getAuthenticationNotToken())
                    .setMessage(safeKeeperCodeMsgConfiguration.getAuthenticationNotTokenMessage())
                    .setLoginType(loginType).build();
            throw exception;
        }
        // 查找此token对应loginId, 如果找不到则抛出：无效token
        String loginId = getLoginIdNotHandle(tokenValue);
        if(loginId == null) {
            SafeKeeperLoginException exception=new SafeKeeperLoginException.
                    Builder().
                    setCode(safeKeeperCodeMsgConfiguration.getAuthenticationInvalidToken())
                    .setMessage(safeKeeperCodeMsgConfiguration.getAuthenticationInvalidTokenMessage())
                    .setLoginType(loginType).build();
            throw exception;
        }
        // 如果是已经过期，则抛出已经过期
        if(loginId.equals(String.valueOf(safeKeeperCodeMsgConfiguration.getAuthenticationTokenTimeout()))) {
            SafeKeeperLoginException exception=new SafeKeeperLoginException.
                    Builder().
                    setCode(safeKeeperCodeMsgConfiguration.getAuthenticationTokenTimeout())
                    .setMessage(safeKeeperCodeMsgConfiguration.getAuthenticationTokenTimeoutMessage())
                    .setLoginType(loginType).build();
            throw exception;
        }
        // 如果是已经被顶替下去了, 则抛出：已被顶下线
        if(loginId.equals(String.valueOf(safeKeeperCodeMsgConfiguration.getAuthenticationBeReplaced()))) {
            SafeKeeperLoginException exception=new SafeKeeperLoginException.
                    Builder().
                    setCode(safeKeeperCodeMsgConfiguration.getAuthenticationBeReplaced())
                    .setMessage(safeKeeperCodeMsgConfiguration.getAuthenticationBeReplacedMessage())
                    .setLoginType(loginType).build();
            throw exception;
        }
        // 如果是已经被踢下线了, 则抛出：已被踢下线
        if(loginId.equals(String.valueOf(safeKeeperCodeMsgConfiguration.getAuthenticationKickOut()))) {
            SafeKeeperLoginException exception=new SafeKeeperLoginException.
                    Builder().
                    setCode(safeKeeperCodeMsgConfiguration.getAuthenticationKickOut())
                    .setMessage(safeKeeperCodeMsgConfiguration.getAuthenticationKickOutMessage())
                    .setLoginType(loginType).build();
            throw exception;
        }
        // 检查是否已经 [临时过期]
        checkActivityTimeout(tokenValue);
        // 如果配置了自动续签, 则: 更新[最后操作时间]
        if(SafeKeeperManager.getConfig().isAutoRenew()) {
            updateLastActivityToNow(tokenValue);
        }
        // 至此，返回loginId
        return loginId;
    }

    /**
     * 续签指定token：(将 [最后操作时间] 更新为当前时间戳)
     * @param tokenValue 指定token
     */
    public void updateLastActivityToNow(String tokenValue) {
        // 如果token == null 或者 设置了[永不过期], 则立即返回
        if(tokenValue == null || SafeKeeperManager.getConfig().getActivityTimeout() == SafeKeeperTokenRealm.NEVER_EXPIRE) {
            return;
        }
        SafeKeeperManager.getSafeKeeperTokenRealm().update(splicingKeyLastActivityTime(tokenValue), String.valueOf(System.currentTimeMillis()));
    }

    /**
     * 检查指定token 是否已经[临时过期]，如果已经过期则抛出异常
     * @param tokenValue 指定token
     */
    public void checkActivityTimeout(String tokenValue) {
        // 如果token == null 或者 设置了[永不过期], 则立即返回
        if(tokenValue == null || SafeKeeperManager.getConfig().getActivityTimeout() == SafeKeeperTokenRealm.NEVER_EXPIRE) {
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
     * 获取当前会话账号id, 如果未登录，则返回null
     * @return 账号id
     */
    public Object getLoginIdDefaultNull() {
        // 如果连token都是空的，则直接返回
        String tokenValue = safeKeeperTokenProcessor.getTokenValue();
        if(tokenValue == null) {
            return null;
        }
        // loginId为null或者在异常项里面，均视为未登录, 返回null
        Object loginId = getLoginIdNotHandle(tokenValue);
        if(loginId == null || SafeKeeperLoginException.ABNORMAL_LIST.contains(loginId)) {
            return null;
        }
        // 如果已经[临时过期]
        if(getTokenActivityTimeoutByToken(tokenValue) == SafeKeeperTokenRealm.NOT_VALUE_EXPIRE) {
            return null;
        }
        // 执行到此，证明loginId已经是个正常的账号id了
        return loginId;
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
        if(SafeKeeperManager.getConfig().getActivityTimeout() == SafeKeeperTokenRealm.NEVER_EXPIRE) {
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
        long timeout = SafeKeeperManager.getConfig().getActivityTimeout() - apartSecond;
        // 如果 < 0， 代表已经过期 ，返回-2
        if(timeout < 0) {
            return SafeKeeperTokenRealm.NOT_VALUE_EXPIRE;
        }
        return timeout;
    }

}
