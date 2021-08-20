package cn.safekeeper.common.exception;

/**
 * 自定义权限异常类型
 * @author skylark
 */
public class SafeKeeperPermissionException extends SafeKeeperException{

    private static final String ROLE_MESSAGE_TEMPLATE="SafeKeeper[%s]登录维度下无此权限:[%s]";

    /**
     * 返回状态码
     */
    private int code;

    /**
     * 角色
     */
    private String permission;

    /**
     * 登录类型
     */
    private String loginType;

    public SafeKeeperPermissionException(int code, String permission, String loginType){
        super(String.format(ROLE_MESSAGE_TEMPLATE, loginType, permission));
        this.code=code;
        this.permission=permission;
        this.loginType=loginType;
    }


    /**
     * 获取权限
     * @return 权限
     */
    public String getPermission(){
        return this.permission;
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
        private String permission;
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
        public Builder setPermission(String permission){
            this.permission=permission;
            return this;
        }
        public Builder setLoginType(String loginType){
            this.loginType=loginType;
            return this;
        }

        public SafeKeeperPermissionException build(){
            return new SafeKeeperPermissionException(this.code,this.permission,this.loginType);
        }

    }



}
