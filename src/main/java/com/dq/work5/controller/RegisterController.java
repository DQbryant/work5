package com.dq.work5.controller;

import com.alibaba.fastjson.JSONObject;
import com.dq.work5.pojo.ResponseJson;
import com.dq.work5.pojo.User;
import com.dq.work5.pojo.vo.RegisterVo;
import com.dq.work5.service.UserService;
import com.dq.work5.utils.AesUtil;
import com.dq.work5.utils.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Email;

/**
 *
 */
@RestController
public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    @Autowired
    UserService userServiceImpl;

    /**
     * 注册控制器,需要传入用户名,邮箱,密码,和加密使用的key
     * @param registerVo
     * @return
     */
    @PostMapping("/user/register")//非空控制,简单的邮箱格式控制
    public ResponseJson register(@Valid RegisterVo registerVo){
        String email = registerVo.getEmail();
        User user = userServiceImpl.selectByEmail(email);
        if (user!=null){
            return new ResponseJson(400,"该邮箱已被注册",null);                 //判断邮箱是否被使用
        }else {
            String username = registerVo.getUsername();
            user = userServiceImpl.selectByUsername(username);
            if(user!=null){
                return new ResponseJson(400,"该用户名已经被使用",null);          //判断用户名是否被使用
            }else {
                String password = AesUtil.decrypt(registerVo.getPassword(),registerVo.getKey());  //解密
                String result = PasswordUtil.check(password);                                     //判断密码是否为6-20位,且不能为纯数字
                if(result!=null)return new ResponseJson(400,result);
                password = AesUtil.encrypt(password);                                              //加密
                userServiceImpl.save(new User(username,email,password));
                logger.info("用户名为:"+username+",邮箱为:"+email+"的用户注册成功");
                return new ResponseJson(200,"注册成功");
            }
        }
    }
}
