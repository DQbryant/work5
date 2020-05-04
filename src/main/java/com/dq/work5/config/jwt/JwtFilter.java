package com.dq.work5.config.jwt;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.dq.work5.pojo.Constant;
import com.dq.work5.pojo.ResponseJson;
import com.dq.work5.utils.JwtUtil;
import com.dq.work5.utils.RedisUtil;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 */
public class JwtFilter extends BasicHttpAuthenticationFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    @Autowired
    RedisUtil redisUtil;
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        //判断请求的请求头是否带上 "Token",有的话就进行登录认证授权
        if (isLoginAttempt(request,response)) {
            try {
                executeLogin(request, response);
            } catch (Exception e) {
                //认证出现异常
                String msg = e.getMessage();
                Throwable throwable = e.getCause();
                if (throwable instanceof SignatureVerificationException) {
                    // 该异常为JWT的AccessToken认证失败(Token或者密钥不正确)
                    msg = "Token或者密钥不正确:" + throwable.getMessage();
                } else if (throwable instanceof TokenExpiredException) {
                    // 该异常为JWT的AccessToken已过期，判断RefreshToken未过期就进行AccessToken刷新
                    if (this.refreshToken(request, response)) {
                        return true;
                    } else {
                        msg = "Token已过期:" + throwable.getMessage();
                    }
                } else {    //如果还出现了其他异常
                    // 应用异常不为空
                    if (throwable != null) {
                        // 获取应用异常msg
                        msg = throwable.getMessage();
                    }
                }
                //token认证失败,返回Response信息
                this.responseError(response,msg);
                return false;
            }
        }else {
            //不携带token
            HttpServletRequest httpServletRequest = (HttpServletRequest)request;
            //获取请求类型
            String method = httpServletRequest.getMethod();
            //获取请求路径
            String uri = httpServletRequest.getRequestURI();
            logger.info("请求{} Authorization属性(Token)为空 请求类型{}",uri,method);
        }
        //如果请求头不存在 Token，则可能是执行登陆操作或者是游客状态访问，无需检查 token，直接返回 true
        return true;
    }
    //刷新token
    private boolean refreshToken(ServletRequest request, ServletResponse response) {
        //获取jwt
        String token = this.getAuthzHeader(request);
        //拿到账号信息
        int id = Integer.parseInt(JwtUtil.getClaim(token,"id"));
        /*ServletContext servletContext = request.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        RedisUtil redisUtil = ctx.getBean(RedisUtil.class);*/
        if(redisUtil.exists(Constant.PREFIX_SHIRO_REFRESH_TOKEN +id)){
            //获取token的时间戳
            String currentTimeMillis = redisUtil.getObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN+id).toString();
            // 获取当前AccessToken中的时间戳，与RefreshToken的时间戳对比，如果当前时间戳一致，进行AccessToken刷新
            if(JwtUtil.getClaim(token,Constant.CURRENT_TIME_MILLIS).equals(currentTimeMillis)){
                //获取当前的时间戳
                String currentTime = String.valueOf(System.currentTimeMillis());
                //存活时间
                String refreshTokenExpireTime = String.valueOf(30*60);
                //重新放进redis
                redisUtil.setObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN+id,currentTime,Integer.parseInt(refreshTokenExpireTime));
                token =JwtUtil.sign(id,currentTime);
                JwtToken jwtToken = new JwtToken(token);
                this.getSubject(request,response).login(jwtToken);
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.setHeader("Authorization", token);
                httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
                return true;
            }
        }
        return false;
    }

    //防止重复调用doGetAuthenticationInfo方法
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        this.sendChallenge(request,response);
        return false;
    }


    private void responseError(ServletResponse response, String msg) {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        try (PrintWriter out = httpServletResponse.getWriter()) {
            String data = JSONObject.toJSONString(new ResponseJson(HttpStatus.UNAUTHORIZED.value(), "无权访问(Unauthorized):" + msg, null));
            out.append(data);
        } catch (IOException e) {
            logger.error("直接返回Response信息出现IOException异常:{}", e.getMessage());
//            throw new CustomException("直接返回Response信息出现IOException异常:" + e.getMessage());
        }
    }
    /**
     * 检测Header里面是否包含Authorization字段，有就进行Token登录认证授权
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        String token = this.getAuthzHeader(request);
        return token!=null;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        //拿到token
        JwtToken jwtToken = new JwtToken(this.getAuthzHeader(request));
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        getSubject(request, response).login(jwtToken);
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }
}
