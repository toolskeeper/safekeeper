package cn.safekeeper.core.manager;

import cn.safekeeper.common.configuration.SafeKeeperConfiguration;
import cn.safekeeper.common.constant.SafeKeeperConstant;
import cn.safekeeper.common.model.SafeKeeperRequest;
import cn.safekeeper.common.model.SafeKeeperResponse;
import cn.safekeeper.common.model.SafeKeeperStorage;
import cn.safekeeper.common.utils.SafeKeeperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * token处理组件
 * @author skylark
 */
public class SafeKeeperTokenProcessor {
    private static final Logger LOGGER= LoggerFactory.getLogger(SafeKeeperTokenProcessor.class);

    private String loginType;

    public SafeKeeperTokenProcessor(String loginType){
        this.loginType=loginType;
    }

    /**
     * 在当前会话中加入token数据
     * @param tokenValue token
     * @param cookieTimeout 存活时间(秒)
     */
    public void setSafeKeeperToken(String tokenValue, int cookieTimeout){
        SafeKeeperConfiguration config = getConfig();
        LOGGER.debug("tokenValue值【{}】,cookie失效时间【{}】",tokenValue,cookieTimeout);
        //根据上下文获取存储对象
        SafeKeeperStorage storage = SafeKeeperManager.getSafeKeeperContext().getStorage();
        //判断是否配置了token前缀
        String tokenPrefix = config.getTokenPrefix();
        LOGGER.debug("tokenPrefix前缀【{}】",tokenPrefix);
        if(SafeKeeperUtils.isEmpty(tokenPrefix)) {
            LOGGER.info("currentCreatedContactKey:【{}】",currentCreatedContactKey());
            storage.set(currentCreatedContactKey(), tokenValue);

        } else {
            // 如果配置了token前缀，则拼接上前缀一起写入
            storage.set(currentCreatedContactKey(), tokenPrefix + SafeKeeperConstant.TOKEN_CONNECTOR_CHAT + tokenValue);
            LOGGER.info("前缀写入的数据是{}",tokenPrefix + SafeKeeperConstant.TOKEN_CONNECTOR_CHAT + tokenValue);
        }
        //响应给客户端cookie
        if(config.isReadFromCookie()){
            SafeKeeperResponse response = SafeKeeperManager.getSafeKeeperContext().getResponse();
            response.addCookie(getSafeKeeperTokenName(), tokenValue, "/", config.getCookieDomain(), cookieTimeout);
            LOGGER.info("完成cookie的生成，名称{},值{},域范围{},失效时间{}",getSafeKeeperTokenName(),tokenValue,config.getCookieDomain(),
                    cookieTimeout);
        }
    }

    /**
     * 创建token
     * @param loginId 登录成功后的业务id
     * @return 返回token
     */
    public String createKeeperToken(Object loginId) {
        return SafeKeeperManager.getSafeKeeperLogicAction().createToken(loginId, loginType);
    }

    /**
     * 获取当前TokenValue
     * @return 当前tokenValue
     */
    public String getTokenValue(){
        SafeKeeperStorage storage = SafeKeeperManager.getSafeKeeperContext().getStorage();
        SafeKeeperRequest request = SafeKeeperManager.getSafeKeeperContext().getRequest();
        SafeKeeperConfiguration config = getConfig();
        String keyTokenName = getSafeKeeperTokenName();
        LOGGER.info("keyTokenName名称{}",keyTokenName);
        String tokenValue = null;
        // 1. 尝试从Storage里读取
        if(storage.get(currentCreatedContactKey()) != null) {
            tokenValue = String.valueOf(storage.get(currentCreatedContactKey()));
            LOGGER.info("storage里读取值{}",tokenValue);
        }
        // 2. 尝试从请求体里面读取
        if(tokenValue == null && config.isReadFromBody()){
            tokenValue = request.getParameter(keyTokenName);
            LOGGER.info("request参数里读取值{}",tokenValue);
        }
        // 3. 尝试从header里读取
        if(tokenValue == null && config.isReadFromHead()){
            tokenValue = request.getHeader(keyTokenName);
            LOGGER.info("request头部里读取值{}",tokenValue);
        }
        // 4. 尝试从cookie里读取
        if(tokenValue == null && config.isReadFromCookie()){
            tokenValue = request.getCookieValue(keyTokenName);
            LOGGER.info("request的cookie里读取值{}",tokenValue);
        }
        return tokenValue;
    }

    /**
     * 获取token名称
     * @return token
     */
    public String getSafeKeeperTokenName() {
        return getTokenNameFromConfig();
    }

    /**
     * 通过配置获取token的名称
     * @return token名称
     */
    public String getTokenNameFromConfig() {
        return getConfig().getSafeKeeperName();
    }
    /**
     * 返回配置对象
     * @return 配置对象
     */
    public SafeKeeperConfiguration getConfig() {
        return SafeKeeperManager.getConfig();
    }

    /**
     * 如果token为本次请求新创建的，则以此字符串为key存储在当前request中
     * @return key
     */
    public String currentCreatedContactKey() {
        return SafeKeeperConstant.CURRENT_CREATED_KEY + loginType;
    }

    /**
     * 获取当前登录维度类型
     * @return 类型数据
     */
    public String getLoginType(){
        return loginType;
    }

}
