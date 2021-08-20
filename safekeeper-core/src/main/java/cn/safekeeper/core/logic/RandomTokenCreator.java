package cn.safekeeper.core.logic;

import cn.safekeeper.common.utils.SafeKeeperUtils;

import java.util.UUID;

/**
 * 随机创建token
 * @author skylark
 */
public class RandomTokenCreator implements TokenCreator{

    @Override
    public String createToken(Object loginId, String loginType) {
        //获取16位随机字符串
        String randomString = SafeKeeperUtils.getRandomString(16);
        if(!SafeKeeperUtils.isEmpty(loginType)&&
                !SafeKeeperUtils.isEmpty(String.valueOf(loginId))&&
        !SafeKeeperUtils.isEmpty(randomString)){
            return loginId.toString().concat(":"+randomString).concat(":"+loginType);
        }
        return String.valueOf(loginId).concat(":"+UUID.randomUUID().toString()).concat(":"+loginType);
    }
}
