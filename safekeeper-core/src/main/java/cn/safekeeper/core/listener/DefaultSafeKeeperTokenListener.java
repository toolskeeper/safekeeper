package cn.safekeeper.core.listener;

import cn.safekeeper.common.model.SafeKeeperValueObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认监听实现
 * @author skylark
 */
public class DefaultSafeKeeperTokenListener implements SafeKeeperTokenListener{
    private static final Logger LOGGER= LoggerFactory.getLogger(DefaultSafeKeeperTokenListener.class);

    @Override
    public void doLogin(String loginType, Object loginId, SafeKeeperValueObject valueObject) {
        LOGGER.debug("SafeKeeper框架登录:loginId["+loginId+"],登录维度:["+loginType+"],登录数据["+valueObject+"]");
    }

    @Override
    public void doLogout(String loginType, Object loginId, String tokenValue) {
        LOGGER.debug("SafeKeeper框架注销登录:loginId["+loginId+"],登录维度:["+loginType+"],注销token数据["+tokenValue+"]");
    }

    @Override
    public void doLogoutByLoginId(String loginType, Object loginId, String tokenValue, String device) {
        LOGGER.debug("SafeKeeper框架注销登录:loginId["+loginId+"],登录维度:["+loginType+"],注销token数据["+tokenValue+"]");
    }

    @Override
    public void doReplaced(String loginType, Object loginId, String tokenValue, String device) {

    }

    @Override
    public void doDisable(String loginType, Object loginId, long disableTime) {
        LOGGER.debug("SafeKeeper禁用账号:账号id["+loginId+"],登录维度:["+loginType+"]");
    }

    @Override
    public void doUntieDisable(String loginType, Object loginId) {
        LOGGER.debug("SafeKeeper禁用账号:账号id["+loginId+"],登录维度:["+loginType+"]");
    }

    @Override
    public void doCreateSession(String id) {
        LOGGER.debug("SafeKeeper创建session:id["+id+"]");
    }

    @Override
    public void doLogoutSession(String id) {
        LOGGER.debug("SafeKeeper框架删除session["+id+"]");
    }
}
