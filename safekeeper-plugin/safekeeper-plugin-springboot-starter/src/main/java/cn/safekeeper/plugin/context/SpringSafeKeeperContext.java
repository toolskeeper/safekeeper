package cn.safekeeper.plugin.context;

import cn.safekeeper.common.model.SafeKeeperContext;
import cn.safekeeper.common.model.SafeKeeperRequest;
import cn.safekeeper.common.model.SafeKeeperResponse;
import cn.safekeeper.common.model.SafeKeeperStorage;
import cn.safekeeper.plugin.model.SafeKeeperRequestForServlet;
import cn.safekeeper.plugin.model.SafeKeeperResponseForServlet;
import cn.safekeeper.plugin.model.SafeKeeperStorageForServlet;

/**
 * 上下文实现类
 * @author skylark
 */
public class SpringSafeKeeperContext implements SafeKeeperContext {
    @Override
    public SafeKeeperRequest getRequest() {
        return new SafeKeeperRequestForServlet(SpringMvcUtils.getRequest());
    }

    @Override
    public SafeKeeperResponse getResponse() {
        return new SafeKeeperResponseForServlet(SpringMvcUtils.getResponse());
    }

    @Override
    public SafeKeeperStorage getStorage() {
        return new SafeKeeperStorageForServlet(SpringMvcUtils.getRequest());
    }

    @Override
    public boolean matchPath(String pattern, String path) {
        return SafeKeeperPathMatcherHolder.getPathMatcher().match(pattern,path);
    }
}
