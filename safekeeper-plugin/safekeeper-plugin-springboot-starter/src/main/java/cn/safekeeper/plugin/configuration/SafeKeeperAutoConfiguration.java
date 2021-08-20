package cn.safekeeper.plugin.configuration;

import cn.safekeeper.common.configuration.SafeKeeperCodeMsgConfiguration;
import cn.safekeeper.common.configuration.SafeKeeperConfiguration;
import cn.safekeeper.common.exception.SafeKeeperException;
import cn.safekeeper.common.model.SafeKeeperAuthorizationCallBack;
import cn.safekeeper.common.model.SafeKeeperContext;
import cn.safekeeper.common.utils.SafeKeeperUtils;
import cn.safekeeper.core.SafeKeeper;
import cn.safekeeper.core.listener.SafeKeeperTokenListener;
import cn.safekeeper.core.manager.SafeKeeperManager;
import cn.safekeeper.core.manager.SafeKeeperTokenRealm;
import cn.safekeeper.plugin.aop.SafeKeeperCheckAspect;
import cn.safekeeper.plugin.context.SpringSafeKeeperContext;
import cn.safekeeper.plugin.filter.SafeKeeperFilter;
import cn.safekeeper.plugin.redis.config.SafeRedisProperties;
import cn.safekeeper.plugin.redis.dao.SafeKeeperTokenRealmRedis;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import javax.servlet.http.HttpServletRequest;

/**
 * SafeKeeper配置启动类
 * @author skylark
 */
@Configuration
public class SafeKeeperAutoConfiguration {

    /**
     * 切面初始化
     */
    @Bean
    public SafeKeeperCheckAspect getSafeKeeperCheckAspect(){
        return new SafeKeeperCheckAspect();
    }

    /**
     * context初始化
     */
    @Bean
    public SafeKeeperContext getSafeKeeperContext(){
        return new SpringSafeKeeperContext();
    }

    @Bean
    @ConfigurationProperties(prefix ="spring.safekeeper")
    public SafeKeeperConfiguration getSafeKeeperConfiguration(){
        return new SafeKeeperConfiguration();
    }


    /**
     * SafeKeeperManager 注入 SafeKeeperConfiguration
     * 配置文件 Bean
     */
    @Autowired
    public void setConfig(SafeKeeperConfiguration config){
        SafeKeeperManager.setConfig(config);
    }

    @Bean
    public SafeRedisProperties getSafeRedisProperties(){
        return new SafeRedisProperties();
    }

    /**
     * 初始化持久化SafeKeeperTokenRealm接口实现
     * @param cfg redis配置对象
     * @return 实现结果
     */
    @Bean
    public SafeKeeperTokenRealm init(SafeRedisProperties cfg){
        // 1. Redis配置
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(cfg.getHost());
        redisConfig.setPort(cfg.getPort());
        redisConfig.setDatabase(cfg.getDatabase());
        redisConfig.setPassword(RedisPassword.of(cfg.getPassword()));

        // 2. 连接池配置
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        // pool配置
        RedisProperties.Lettuce lettuce = cfg.getLettuce();
        if(lettuce.getPool() != null) {
            RedisProperties.Pool pool = cfg.getLettuce().getPool();
            // 连接池最大连接数
            poolConfig.setMaxTotal(pool.getMaxActive());
            // 连接池中的最大空闲连接
            poolConfig.setMaxIdle(pool.getMaxIdle());
            // 连接池中的最小空闲连接
            poolConfig.setMinIdle(pool.getMinIdle());
            // 连接池最大阻塞等待时间（使用负值表示没有限制）
            poolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
        }
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
        // timeout
        if(cfg.getTimeout() != null) {
            builder.commandTimeout(cfg.getTimeout());
        }
        // shutdownTimeout
        if(lettuce.getShutdownTimeout() != null) {
            builder.shutdownTimeout(lettuce.getShutdownTimeout());
        }
        // 创建Factory对象
        LettuceClientConfiguration clientConfig = builder.poolConfig(poolConfig).build();
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfig, clientConfig);
        factory.afterPropertiesSet();
        return new SafeKeeperTokenRealmRedis(factory);
    }

    /**
     * SafeKeeperManager SafeKeeperTokenDaoStorage
     * 持久化Dao Bean
     */
    @Autowired
    public void setSafeKeeperTokenRealm(SafeKeeperTokenRealm safeKeeperTokenRealm) {
        SafeKeeperManager.setSafeKeeperTokenRealm(safeKeeperTokenRealm);
    }

    /**
     *
     * SafeKeeperManager SafeKeeperAuthorizationCallBack
     * 权限认证 Bean
     */
    @Autowired(required = false)
    public void setStpInterface(SafeKeeperAuthorizationCallBack authorizationCallBack) {
        SafeKeeperManager.setSafeKeeperAuthorizationCallBack(authorizationCallBack);
    }

    /**
     * 容器操作 Bean
     */
    @Autowired
    public void setSafeKeeperContext(SafeKeeperContext safeKeeperContext) {
        SafeKeeperManager.setSafeKeeperContext(safeKeeperContext);
    }

    /**
     * SafeKeeperManager SafeKeeperTokenListener
     * 侦听器 Bean
     */
    @Autowired(required = false)
    public void setSafeKeeperTokenListener(SafeKeeperTokenListener tokenListener) {
        SafeKeeperManager.setSafeKeeperTokenListener(tokenListener);
    }

    /**
     * SafeKeeperManager SafeKeeperCodeMsgConfiguration
     * 状态消息配置 Bean
     */
    @Autowired(required = false)
    public void setSafeKeeperCodeMsgConfiguration(SafeKeeperCodeMsgConfiguration safeKeeperCodeMsgConfiguration) {
        SafeKeeperManager.setSafeKeeperCodeMsgConfiguration(safeKeeperCodeMsgConfiguration);
    }

    @Bean
    public SafeKeeperFilter getSaServletFilter(SafeKeeperConfiguration config) {
        return new SafeKeeperFilter()
                // 指定 拦截路由 与 放行路由
                .addInclude("/**").addExclude("/favicon.ico").setIncludeList(config.getIncludeList())
                .addExclude("/login").setExcludeList(config.getExcludeList())
                // 认证函数: 每次请求执行
                .setAuth(r -> {
                    HttpServletRequest request=(HttpServletRequest)r;
                    //获取登录的方式
                    String loginType = request.getHeader("loginType");
                    //如果header中的数据为空，尝试去参数中获取。
                    if(SafeKeeperUtils.isEmpty(loginType)){
                        loginType=request.getParameter("loginType");
                    }
                    //假如都是没有传入数据，就直接给予明确的提示
                    if(SafeKeeperUtils.isEmpty(loginType)){
                        throw new SafeKeeperException("正在请求受到SafeKeeper保护的资源，header或者参数中没有loginType字段数据，禁止访问！");
                    }
                    SafeKeeper.safeLogic(loginType).checkLogin();
                })
                // 异常处理函数：每次认证函数发生异常时执行此函数
                .setError(Throwable::getMessage)
                // 前置函数：在每次认证函数之前执行
                .setBeforeAuth(r -> {
                    //安全响应头
                    SafeKeeperManager.getSafeKeeperContext().getResponse()
                            .setServer("safekeeper-server")
                            .setHeader("X-XSS-Protection", "1; mode=block")
                            // 禁用浏览器内容嗅探
                            .setHeader("X-Content-Type-Options", "nosniff")
                    ;
                });
    }
}
