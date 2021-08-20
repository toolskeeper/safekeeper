package cn.safekeeper.common.exception;

/**
 * 自定义角色异常类型
 * @author skylark
 */
public class SafeKeeperRoleException extends SafeKeeperException{

    private static final String ROLE_MESSAGE_TEMPLATE="SafeKeeper[%s]登录维度下无此角色[%s]";

    /**
     * 返回状态码
     */
    private int code;

    /**
     * 角色
     */
    private String role;

    /**
     * 登录类型
     */
    private String loginType;

    public SafeKeeperRoleException(int code,String role,String loginType){
        super(String.format(ROLE_MESSAGE_TEMPLATE, loginType, role));
        this.code=code;
        this.role=role;
        this.loginType=loginType;
    }


    /**
     * 获取角色
     * @return 角色
     */
    public String getRole(){
        return this.role;
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
        private String role;
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
        public Builder setRole(String role){
            this.role=role;
            return this;
        }
        public Builder setLoginType(String loginType){
            this.loginType=loginType;
            return this;
        }

        public SafeKeeperRoleException build(){
            return new SafeKeeperRoleException(this.code,this.role,this.loginType);
        }

    }



}
