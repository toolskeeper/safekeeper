package cn.safekeeper.common.exception;

/**
 * 自定义登录异常类型
 * @author skylark
 */
public class SafeKeeperLockedException extends SafeKeeperException{
    private static final String LOGIN_MESSAGE_TEMPLATE="SafeKeeper[%s]维度下账号锁定异常:[%s]";
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

    public SafeKeeperLockedException(int code, String message, String loginType){
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
    static class Builder {

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

        public SafeKeeperLockedException build(){
            return new SafeKeeperLockedException(this.code,this.message,this.loginType);
        }

    }



}
