package cn.safekeeper.common.annotations;

/**
 * 条件表达式
 * @author skylark
 */
public enum SafeKeeperCondition {
    AND("&"),
    OR("|"),
    EQUALS("=");
    private String value;
    SafeKeeperCondition(String value){
        this.value=value;
    }

    public String getValue(){
        return value;
    }


}
