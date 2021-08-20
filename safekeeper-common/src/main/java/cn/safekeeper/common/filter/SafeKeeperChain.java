package cn.safekeeper.common.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 过滤器链路处理
 * @author skylark
 */
public class SafeKeeperChain implements SafeKeeperFilter{

    private static final Logger log= LoggerFactory.getLogger(SafeKeeperChain.class);

    private final List<SafeKeeperFilter> filters;

    public SafeKeeperChain(List<SafeKeeperFilter> filters){
        this.filters=filters;
    }

    @Override
    public boolean doFilter() {
        log.debug("SafeKeeperChain 调用....");
        log.debug("SafeKeeperChain 有【"+filters.size()+"】个过滤器需要进行校验");
        for(SafeKeeperFilter filter:filters){
            if(!filter.doFilter()){
                log.debug("SafeKeeperChain 中的filter【"+filter.getClass().getName()+"】过滤器校验失败");
                return false;
            }
        }
        log.debug("SafeKeeperChain 调用结束，完成过滤校验....");
        return true;
    }

    /**
     * 添加过滤器到链路中
     * @param filter 过滤器对象
     * @return 对象自己
     */
    public SafeKeeperChain addFilter(SafeKeeperFilter filter){
        log.debug("SafeKeeperChain addFilter调用....");
        if(filters!=null){
            filters.add(filter);
        }
        log.debug("SafeKeeperChain addFilter调用结束....");
        return this;
    }
}
