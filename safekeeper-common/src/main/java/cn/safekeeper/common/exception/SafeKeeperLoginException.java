package cn.safekeeper.common.exception;

import java.util.Arrays;
import java.util.List;

/**
 * 自定义登录异常类型
 * @author skylark
 */
public class SafeKeeperLoginException extends SafeKeeperException{
    private static final String LOGIN_MESSAGE_TEMPLATE="SafeKeeper[%s]维度下抛出异常:[%s]";


    /** 表示未提供token */
    public static final String NOT_TOKEN = "-1";
    public static final String NOT_TOKEN_MESSAGE = "未提供token";

    /** 表示token无效 */
    public static final String INVALID_TOKEN = "-2";
    public static final String INVALID_TOKEN_MESSAGE = "token无效";

    /** 表示token已过期 */
    public static final String TOKEN_TIMEOUT = "-3";
    public static final String TOKEN_TIMEOUT_MESSAGE = "token已过期";

    /** 表示token已被顶下线 */
    public static final String BE_REPLACED = "-4";
    public static final String BE_REPLACED_MESSAGE = "token已被顶下线";

    /** 表示token已被踢下线 */
    public static final String KICK_OUT = "-5";
    public static final String KICK_OUT_MESSAGE = "token已被踢下线";

    /** 默认的提示语 */
    public static final String DEFAULT_MESSAGE = "当前会话未登录";


    /**
     * 代表异常token的标志集合
     */
    public static final List<String> ABNORMAL_LIST = Arrays.asList(NOT_TOKEN, INVALID_TOKEN, TOKEN_TIMEOUT, BE_REPLACED, KICK_OUT);

    /**
     * 返回状态码
     */
    private int code;

    /**
     * 消息
     */
    private String message;

    /**
     * 登录类型
     */
    private String loginType;

    public SafeKeeperLoginException(int code, String message, String loginType){
        super(String.format(LOGIN_MESSAGE_TEMPLATE, loginType, message));
        this.code=code;
        this.message=message;
        this.loginType=loginType;
    }


    /**
     * 获取角色
     * @return 角色
     */
    public String getMessage(){
        return this.message;
    }

    /**
     * 响应状态码
     * @return 状态码
     */
    public int getCode(){
        return code;
    }


    /**
     * 获取登录维度
     * @return 登录方式
     */
    public String getLoginType(){
        return this.loginType;
    }
    /**
     * 静态构造类
     */
    public static class Builder {

        /**
         * 状态码
         */
        private int code;
        /**
         * 角色
         */
        private String message;
        /**
         * 消息数据
         */
        private String loginType;

        /**
         * 构造角色实例方法
         * @return 返回角色异常对象
         */
        public static Builder builder(){
            return new Builder();
        }

        public Builder setCode(int code){
            this.code=code;
            return this;
        }
        public Builder setMessage(String message){
            this.message=message;
            return this;
        }
        public Builder setLoginType(String loginType){
            this.loginType=loginType;
            return this;
        }

        public SafeKeeperLoginException build(){
            return new SafeKeeperLoginException(this.code,this.message,this.loginType);
        }

    }



}
