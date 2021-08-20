package cn.safekeeper.core.logic;

public class TokenCreatorFactory {

    /**
     * 获取token生产对象
     * @return TokenCreator对象
     */
    public static TokenCreator getTokenCreator(){
        return new RandomTokenCreator();
    }
}
