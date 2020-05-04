package com.dq.work5.controller;

import com.alibaba.fastjson.JSONObject;
import com.dq.work5.config.jwt.JwtToken;
import com.dq.work5.pojo.Constant;
import com.dq.work5.pojo.ResponseJson;
import com.dq.work5.pojo.User;
import com.dq.work5.pojo.vo.LoginByEmailVo;
import com.dq.work5.pojo.vo.LoginByUsernameVo;
import com.dq.work5.service.UserService;
import com.dq.work5.utils.AesUtil;
import com.dq.work5.utils.JwtUtil;
import com.dq.work5.utils.RedisUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 *
 */
@RestController
public class LoginController {
    private int refreshTokenExpireTime = 30*60;
    private final UserService userServiceImpl;
    private final RedisUtil redisUtil;
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    public LoginController(UserService userServiceImpl, RedisUtil redisUtil) {
        this.userServiceImpl = userServiceImpl;
        this.redisUtil = redisUtil;
    }

    /**
     * 用户名登录
     * @param loginByUsernameVo
     * @param httpServletResponse
     * @return
     */
    @PostMapping("/user/login/username")//非空控制
    public ResponseJson loginByUsername(@Valid LoginByUsernameVo loginByUsernameVo, HttpServletResponse httpServletResponse){
        String username = loginByUsernameVo.getUsername();
        User user = userServiceImpl.selectByUsername(username);
        if(null==user)return new ResponseJson(400,"用户名不存在");                               //用户不存在
        String password = AesUtil.decrypt(loginByUsernameVo.getPassword(),loginByUsernameVo.getKey());      //根据传递给前端的加密私钥解密
        if(password==null)return new ResponseJson(400,"发送的参数错误!");                        //私钥错误(非法操作)
        password = AesUtil.encrypt(password);                                                               //再次加密
        if(password.equals(user.getPassword())){
            String currentTimeMills = String.valueOf(System.currentTimeMillis());                           //获得当前时间的字符串
            String token = JwtUtil.sign(user.getId(),currentTimeMills);                                     //生成token
            redisUtil.setObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN+user.getId(),currentTimeMills,refreshTokenExpireTime);//把token存放进redis中,并设置有效时间
            httpServletResponse.setHeader("Authorization", token);                                       //将token设置进响应头中
            httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
            logger.info("用户"+username+"登录了,用户ID:"+user.getId());
            return new ResponseJson(HttpStatus.OK.value(), "登录成功");
        }else return new ResponseJson(400,"密码错误");
    }

    /**
     * 邮箱登录
     * @param loginByEmailVo
     * @param response
     * @return
     */
    @PostMapping("/user/login/email")//非空和邮箱格式控制
    public ResponseJson loginByEmail(@Valid LoginByEmailVo loginByEmailVo, HttpServletResponse response){
        User user = userServiceImpl.selectByEmail(loginByEmailVo.getEmail());
        if(null==user)return new ResponseJson(400,"邮箱未被使用");                        //用户不存在
        String password = AesUtil.decrypt(loginByEmailVo.getPassword(),loginByEmailVo.getKey());     //根据传递给前端的加密私钥解密
        if(password==null)return new ResponseJson(400,"发送的参数错误!");                 //私钥错误(非法操作)
        password = AesUtil.encrypt(password);                                                        //再次加密
        if(password.equals(user.getPassword())){
            String currentTimeMills = String.valueOf(System.currentTimeMillis());
            String token = JwtUtil.sign(user.getId(),currentTimeMills);
            redisUtil.setObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN+user.getId(),currentTimeMills,refreshTokenExpireTime);
            response.setHeader("Authorization", token);
            response.setHeader("Access-Control-Expose-Headers", "Authorization");
            logger.info("用户"+user.getUsername()+"登录了,用户ID:"+user.getId());
            return new ResponseJson(HttpStatus.OK.value(), "登录成功");
        }else return new ResponseJson(400,"密码错误");
    }
}
