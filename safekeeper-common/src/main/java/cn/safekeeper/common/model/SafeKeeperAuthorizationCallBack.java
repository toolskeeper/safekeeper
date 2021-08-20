package cn.safekeeper.common.model;

import java.util.List;

/**
 * 业务权限认证回调接口
 * 每个业务的权限和角色都是有定制化的，所以需要业务自身来实现
 * 框架只是回调你实现的的角色和权限来判断是否正确即可
 * @author skylark
 */
public interface SafeKeeperAuthorizationCallBack {
    /**
     * 返回指定账号id所拥有的权限码集合
     * @param loginId  账号id
     * @param loginType 账号类型
     * @return 该账号id具有的权限码集合
     */
    List<String> getPermissionList(Object loginId, String loginType);

    /**
     * 返回指定账号id所拥有的角色标识集合
     * @param loginId  账号id
     * @param loginType 账号类型
     * @return 该账号id具有的角色标识集合
     */
    List<String> getRoleList(Object loginId, String loginType);
}
