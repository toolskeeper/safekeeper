package cn.safekeeper.core.logic;

/**
 * token创建方法
 * @author skylark
 */
public interface TokenCreator {
    /**
     * 根据id，维度进行创建token
     * @param loginId 业务登录id
     * @param loginType 登录的维度方式
     * @return token数据
     */
    String createToken(Object loginId, String loginType);
}
