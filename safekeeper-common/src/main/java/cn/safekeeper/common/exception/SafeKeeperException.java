package cn.safekeeper.common.exception;

/**
 * 自定义异常类
 * @author skylark
 */
public class SafeKeeperException extends RuntimeException {

    public SafeKeeperException(){
        super();
    }

    public SafeKeeperException(String message){
        super(message);
    }

    public SafeKeeperException(Throwable cause){
        super(cause);
    }

    public SafeKeeperException(String message, Throwable cause){
        super(message,cause);
    }

}
