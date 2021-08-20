package cn.safekeeper.simple.web.auth;

import cn.safekeeper.common.model.SafeKeeperAuthorizationCallBack;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class AuthorizationForPermissionAndRoles implements SafeKeeperAuthorizationCallBack {
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        //在某个维度下面进行权限回调
        //这里省略业务逻辑
        List<String> permissions=new ArrayList<>();
        permissions.add("user:usermanager:view");
        permissions.add("user:usermanager:add");
        permissions.add("order:usermanager:view");
        permissions.add("order:usermanager:del");
        permissions.add("order:usermanager:save");
        return permissions;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        //在某个维度下面进行权限回调
        //这里省略业务逻辑
        List<String> roles=new ArrayList<>();
        roles.add("user");
        roles.add("order");
        return roles;
    }
}
