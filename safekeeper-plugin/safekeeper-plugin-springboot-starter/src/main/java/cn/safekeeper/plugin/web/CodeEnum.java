package cn.safekeeper.plugin.web;

/**
 * 默认的状态码
 * @author skylark
 */
public enum CodeEnum {
    SUCCESS(0),
    ERROR(1),
    OTHER(2);

    private Integer code;
    CodeEnum(Integer code){
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
