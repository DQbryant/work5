package com.dq.work5.config;

import com.dq.work5.config.jwt.JwtToken;
import com.dq.work5.mapper.UserMapper;
import com.dq.work5.pojo.Constant;
import com.dq.work5.pojo.User;
import com.dq.work5.service.UserService;
import com.dq.work5.utils.JwtUtil;
import com.dq.work5.utils.RedisUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
public class UserRealm extends AuthorizingRealm {
    @Autowired
    private UserService userServiceImpl;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 大坑,不然会报错
     * @param token
     * @return
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        int id = JwtUtil.getId(principalCollection.toString());
        //查询角色
        String role = userServiceImpl.getRole(id);
        info.addRole(role);
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String)authenticationToken.getCredentials();
        int id = JwtUtil.getId(token);
        if (id == 0) {
            throw new AuthenticationException("账号为空");
        }
        User user = userServiceImpl.selectById(id);
        if(null==user)throw new AuthenticationException("用户不存在");
        //认证token
        if (JwtUtil.verify(token) && redisUtil.exists(Constant.PREFIX_SHIRO_REFRESH_TOKEN + id)) {
            // 获取RefreshToken的时间戳
            String currentTimeMillis = redisUtil.getObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN + id).toString();
            // 获取AccessToken时间戳，与RefreshToken的时间戳对比
            if (JwtUtil.getClaim(token, Constant.CURRENT_TIME_MILLIS).equals(currentTimeMillis)) {
                return new SimpleAuthenticationInfo(token, token,getName());
            }else throw  new AuthenticationException("Token已失效,请重新登录");
        }
        throw new AuthenticationException("Token已过期");
    }
}
