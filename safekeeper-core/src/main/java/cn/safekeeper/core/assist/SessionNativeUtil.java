package cn.safekeeper.core.assist;

import cn.safekeeper.core.manager.SafeKeeperManager;
import cn.safekeeper.core.session.SafeKeeperSession;

/**
 * session辅助工具
 * @author skylark
 */
public class SessionNativeUtil {
    /**
     * 添加前缀
     */
    public static String sessionKey = "safe";

    /**
     * 自定义Session的Id
     * @param sessionId 会话id
     * @return sessionId
     */
    public static String assemblySessionKey(String sessionId) {
        return SafeKeeperManager.getConfig().getSafeKeeperName() + ":" + sessionKey + ":session:" + sessionId;
    }

    /**
     * 指定key的Session是否存在
     * @param sessionId Session的id
     * @return 是否存在
     */
    public static boolean isExists(String sessionId) {
        return SafeKeeperManager.getSafeKeeperTokenRealm().getSession(assemblySessionKey(sessionId)) != null;
    }

    /**
     * 获取指定key的Session
     * @param sessionId key
     * @param isCreate  如果此Session尚未在DB创建，是否新建并返回
     * @return Session
     */
    public static SafeKeeperSession getSessionById(String sessionId, boolean isCreate) {
        SafeKeeperSession safeKeeperSession = SafeKeeperManager.getSafeKeeperTokenRealm().getSession(assemblySessionKey(sessionId));
        if (safeKeeperSession == null && isCreate) {
            safeKeeperSession = SafeKeeperManager.getSafeKeeperLogicAction().createSession(assemblySessionKey(sessionId));
            SafeKeeperManager.getSafeKeeperTokenRealm().setSession(safeKeeperSession, SafeKeeperManager.getConfig().getTimeout());
        }
        return safeKeeperSession;
    }

    /**
     * 获取指定key的Session, 如果此Session尚未在DB创建，则新建并返回
     * @param sessionId key
     * @return session对象
     */
    public static SafeKeeperSession getSessionById(String sessionId) {
        return getSessionById(sessionId, true);
    }

    /**
     * 删除指定key的Session
     * @param sessionId 指定key
     */
    public static void deleteSessionById(String sessionId) {
        SafeKeeperManager.getSafeKeeperTokenRealm().deleteSession(assemblySessionKey(sessionId));
    }
}
