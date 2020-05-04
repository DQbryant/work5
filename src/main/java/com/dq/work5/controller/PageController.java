package com.dq.work5.controller;

import com.dq.work5.pojo.Constant;
import com.dq.work5.pojo.User;
import com.dq.work5.service.UserService;
import com.dq.work5.utils.JwtUtil;
import com.dq.work5.utils.RamdomUtil;
import com.dq.work5.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@Controller
public class PageController {
    @Autowired
    UserService userServiceImpl;
    @Autowired
    RedisUtil redisUtil;

    /**
     * 根据邮件中的链接到达重置页面
     * @param token
     * @param request
     * @return
     */
    @RequestMapping("/forget/page")
    public String toResetPage(String token,HttpServletRequest request){
        if(!redisUtil.exists(Constant.PREFIX_RESETPASSRORD+token)) {//token应该是邮件里的,而且未过期
            request.setAttribute("key", RamdomUtil.generateString(16));     //带上加密私钥,可以给密码加密
            return "index";
        }
        request.setAttribute("token",token);                                    //将token设置进作用域中,重置时会跟随表单提交
        request.setAttribute("key", RamdomUtil.generateString(16));     //带上加密私钥,可以给密码加密
        return "resetPwd";
    }
    @RequestMapping({"/","/index"})
    public String toLogin(HttpServletRequest request){                              //主页面
        request.setAttribute("key", RamdomUtil.generateString(16));         //带上加密私钥,可以给密码加密
        return "index";
    }
    @RequestMapping("/page/resetUsername")
    public String resetUsername(){                                                  //来到重置密码页面
        return "resetUsername";
    }
    @RequestMapping("/page/resetEmail")
    public String resetEmail(HttpServletRequest request){                           //来到重置邮箱界面,由于要输入密码,一样会带上key
        request.setAttribute("key",RamdomUtil.generateString(16));
        return "resetEmail";
    }
    @RequestMapping("/page/resetPassword")
    public String resetPassword(HttpServletRequest request){                        //来到重置密码界面,由于要输入旧密码,一样会带上key
        request.setAttribute("key",RamdomUtil.generateString(16));
        return "resetPassword";
    }
    @RequestMapping("/page/manage")
    public String toManage(HttpServletRequest request, String token){               //来到用户管理页面,需要将用户token放入表单中提交
        int id = JwtUtil.getId(token);
        if(token==null||id==0){
            request.setAttribute("key",RamdomUtil.generateString(16));
            return "index";
        }
        User user = userServiceImpl.getById(id);
        user.setPassword(null);
        request.setAttribute("user",user);
        return "manage";
    }
    @RequestMapping("/main")
    public String toMain(HttpServletRequest request,String token){                  //来到用户管理页面,需要将用户token放入表单中提交
        if(token==null){
            request.setAttribute("user",User.defaultUser);                      //游客身份
            return "main";
        }
        int id = JwtUtil.getId(token);
        if(id==0){
            request.setAttribute("key",RamdomUtil.generateString(16));
            return "index";
        }
        User user = userServiceImpl.getById(id);
        user.setPassword(null);
        request.setAttribute("user", user);                                     //将用户放入作用域中
        if(user.getRole().equals("user")) {
            return "main";
        }else if(user.getRole().equals("admin")){                                   //如果是管理员会来到管理员页面
            return "admin";
        }
        request.setAttribute("key",RamdomUtil.generateString(16));
        return "index";
    }
}
