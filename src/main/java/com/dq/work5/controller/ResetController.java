package com.dq.work5.controller;

import com.alibaba.fastjson.JSONObject;
import com.dq.work5.pojo.Constant;
import com.dq.work5.pojo.Question;
import com.dq.work5.pojo.ResponseJson;
import com.dq.work5.pojo.User;
import com.dq.work5.service.QuestionService;
import com.dq.work5.service.UserService;
import com.dq.work5.service.UserServiceImpl;
import com.dq.work5.utils.*;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 *
 */
@RestController
public class ResetController {
    @Autowired
    private UserService userServiceImpl;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private RedisUtil redisUtil;
    @Value("${spring.mail.username}")
    private String from;
    private static final Logger logger = LoggerFactory.getLogger(ResetController.class);
    /**
     * 发送重置密码链接
     * @param email     用户邮箱
     * @param request
     * @return
     */
    @PostMapping("/forget")
    public ResponseJson forgetPwd(String email, HttpServletRequest request){
        if(redisUtil.exists(Constant.PREFIX_RESETPASSRORD+email))return new ResponseJson(400,"请勿重复重置密码找回密码");//30分钟内不允许再次发送重置链接
        User user = userServiceImpl.selectByEmail(email);
        if(user==null){
            return new ResponseJson(401,"该邮箱未被注册",null);        //邮箱未被注册
        }else{
            //生成唯一token
            String token = UUID.randomUUID().toString();                                //生成唯一标识对应一个邮箱
            String appUrl = request.getScheme() + "://" + request.getServerName()+":8848";//生成重置链接地址
            try {
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
                helper.setFrom(from);
                helper.setTo(email);
                helper.setSubject("点击下方链接重置密码：");
                redisUtil.setObject(Constant.PREFIX_RESETPASSRORD+token,email,30*60);//设置重置连接的有效期,同样是30分钟
                String url = appUrl+"/forget/page";
                helper.setText("链接30分钟有效:<a target=\"_blank\" href=\""+url+"?token="+token+"\">"+url+"</a>",true);
                javaMailSender.send(mimeMessage);
                redisUtil.setObject(Constant.PREFIX_RESETPASSRORD+email,email,30*60);               //设置每个邮箱的找回密码间隔,30分钟
                logger.info("邮箱为:"+email+"的用户申请了重置密码");
                return new ResponseJson(200,"重置链接发送成功,请到邮箱中查看,有效期:30分钟");
            } catch (MessagingException e) {
                e.printStackTrace();
                return new ResponseJson(403,"重置链接发送失败");
            }
        }
    }

    /**
     * 重置密码的控制器,
     * @param token     邮件中带的临时token
     * @param key       加密key所用的
     * @param password  新密码
     * @param httpServletResponse
     * @return
     */
    @PostMapping("/forget/reset")
    public ResponseJson resetPwdByEmail(String token,String key,String password,HttpServletResponse httpServletResponse){
        if(redisUtil.exists(Constant.PREFIX_RESETPASSRORD+token)){                                                      //该临时token必须是邮件中的
            User user = userServiceImpl.selectByEmail((String)redisUtil.getObject(Constant.PREFIX_RESETPASSRORD+token));
            password = AesUtil.decrypt(password,key);   //解密
            if(password==null) return new ResponseJson(400,"参数错误");                                             //防止随意传参
            String result = PasswordUtil.check(password);
            if(result!=null)return new ResponseJson(400,result);                                                        //新密码格式控制
            password = AesUtil.encrypt(password);           //加密
            user.setPassword(password);
            userServiceImpl.update(user);
            redisUtil.delete(Constant.PREFIX_RESETPASSRORD+token);                                                      //重置完密码后,重置链接失效
            String currentTimeMills = String.valueOf(System.currentTimeMillis());
            String jwttoken = JwtUtil.sign(user.getId(),currentTimeMills);
            redisUtil.setObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN+user.getId(),currentTimeMills,30*60);   //签发新的token,由于签发时间的不同,非法用户的token将会失效
            httpServletResponse.setHeader("Authorization", jwttoken);
            httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
            logger.info("邮箱为:"+user.getEmail()+"的用户成功重置了密码");
            return new ResponseJson(200,"密码重置成功!");
        }else {
            return  new ResponseJson(403,"token错误或重置链接已经失效!");
        }
    }
}
