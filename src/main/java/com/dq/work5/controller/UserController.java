package com.dq.work5.controller;

import com.dq.work5.pojo.Constant;
import com.dq.work5.pojo.ResponseJson;
import com.dq.work5.pojo.User;
import com.dq.work5.pojo.vo.ResetEmailVo;
import com.dq.work5.service.UserService;
import com.dq.work5.utils.*;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 *
 */
@RestController
public class UserController {
    private final UserService userServiceImpl;
    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userServiceImpl, JavaMailSender javaMailSender, RedisUtil redisUtil) {
        this.userServiceImpl = userServiceImpl;
        this.javaMailSender = javaMailSender;
        this.redisUtil = redisUtil;
    }

    @Value("${spring.mail.username}")
    private String from;

    /**
     *
     * @param request
     * @param old_pwd           旧密码
     * @param new_pwd           新密码
     * @param httpServletResponse
     * @param key               加密私钥
     * @return
     */
    @RequiresRoles("user")
    @PostMapping("/user/resetPwd")
    public ResponseJson resetPwd(HttpServletRequest request, String old_pwd, String new_pwd, HttpServletResponse httpServletResponse,String key){
        if(old_pwd==null||new_pwd==null||key==null)return new ResponseJson(400,"参数错误");
        String pwd_old = AesUtil.decrypt(old_pwd,key);
        String pwd_new = AesUtil.decrypt(new_pwd,key);
        if(pwd_old==null||pwd_new==null)return new ResponseJson(400,"参数错误");                    //解密出现错误
        User user = userServiceImpl.selectById(JwtUtil.getId(request.getHeader("Authorization")));
        if(user.isBanned())return new ResponseJson(400,"账号已被封禁,不能修改密码");
        pwd_old = AesUtil.encrypt(pwd_old);
        if(!pwd_old.equals(user.getPassword()))return new ResponseJson(400,"密码错误!",null);//旧密码是否正确
        String check = PasswordUtil.check(pwd_new);                                                             //新密码不能为简单密码
        if(check!=null)return new ResponseJson(400,check);
        pwd_new = AesUtil.encrypt(pwd_new);
        user.setPassword(pwd_new);
        if(userServiceImpl.update(user)==1){                                                                    //重置密码成功,签发新token
            String currentTimeMills = String.valueOf(System.currentTimeMillis());
            String token = JwtUtil.sign(user.getId(),currentTimeMills);
            redisUtil.setObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN+user.getId(),currentTimeMills,30*60);
            httpServletResponse.setHeader("Authorization", token);
            httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
            logger.info("用户ID为"+user.getId()+"的用户修改了密码");
            return new ResponseJson(200,"密码修改成功!");
        }return new ResponseJson(400,"密码修改失败!");
    }

    /**
     * 重置邮箱
     * @param request
     * @param resetEmailVo
     * @return
     */
    @RequiresRoles("user")
    @PostMapping("/user/resetEmail")
    public ResponseJson resetEmail(HttpServletRequest request, @Valid ResetEmailVo resetEmailVo){//非空控制,简单的邮件控制
        User user1 = userServiceImpl.getById(JwtUtil.getId(request.getHeader("Authorization")));
        if(user1.isBanned())return new ResponseJson(400,"账号已被封禁,不能重置邮箱");
        String password = AesUtil.decrypt(resetEmailVo.getPassword(),resetEmailVo.getKey());
        if(password==null)return new ResponseJson(400,"参数错误!");
        password = AesUtil.encrypt(password);
        User user = userServiceImpl.selectByEmail(resetEmailVo.getEmail());
        if(user!=null)return new ResponseJson(400,"该邮箱已经被使用!");                 //邮箱不能被使用
        user = userServiceImpl.selectById(JwtUtil.getId(request.getHeader("Authorization")));
        if(!password.equals(user.getPassword()))return new ResponseJson(400,"旧密码错误");//旧密码错误
        user.setEmail(resetEmailVo.getEmail());
        user.setActive(false);
        if(userServiceImpl.update(user)==1){
            logger.info("用户"+user.getUsername()+",ID为"+user.getId()+"的用户重置了邮箱,新邮箱为:"+user.getEmail());
            return new ResponseJson(200,"邮箱修改成功!");
        }return new ResponseJson(400,"邮箱修改失败!");
    }

    /**
     * 申请激活邮箱,将会发送激活链接
     * @param request
     * @return
     */
    @RequiresRoles("user")
    @PostMapping("/user/activate")
    public ResponseJson activate(HttpServletRequest request){
        User user = userServiceImpl.getById(JwtUtil.getId(request.getHeader("Authorization")));
        if(user.isBanned())return new ResponseJson(400,"账号已被封禁,不能激活账户");
        String email = user.getEmail();
        String token = UUID.randomUUID().toString();                                //生成唯一token
        String appUrl = request.getScheme() + "://" + request.getServerName()+":8848";
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject("点击下方链接激活账户");
            redisUtil.setObject(Constant.PREFIX_ACTIVE+token,email,60*30);//token对应邮箱,链接有效期30分钟
            helper.setText("链接30分钟有效:<a target=\"_blank\" href=\""+appUrl+"/activate?token="+token+"\""+">"+appUrl+"/activate</a>",true);
            javaMailSender.send(mimeMessage);
            logger.info("用户ID为"+user.getId()+"的用户申请激活邮箱,邮箱为:"+user.getEmail());
            return new ResponseJson(200,"激活邮件发送成功,请尽快前往邮箱验证,链接有效期:30分钟");
        } catch (MessagingException e) {
            e.printStackTrace();
            return new ResponseJson(400,"激活邮件发送失败:"+e.getMessage());
        }
    }

    /**
     * 激活账户,需要使用链接生成的临时token认证身份
     * @param token
     * @return
     */
    @GetMapping("/activate")
    public ResponseJson activate(String token){
        if(token==null)return new ResponseJson(400,"参数错误");
        if(redisUtil.exists(Constant.PREFIX_ACTIVE+token)){                             //链接未失效
            String email = (String)redisUtil.getObject(Constant.PREFIX_ACTIVE+token);   //找到token对应的邮箱
            User user = userServiceImpl.selectByEmail(email);
            int result = userServiceImpl.activate(user.getId());
            if(result==1){
                redisUtil.delete(Constant.PREFIX_ACTIVE+token);                         //激活邮件失效
                logger.info("用户邮箱为"+email+"的用户激活成功!");
                return new  ResponseJson(200,"账户激活成功");
            }else{
                return new ResponseJson(400,"账户激活失败");
            }
        }else{
            return new ResponseJson(400,"激活链接已过期,请重新请求发送激活链接!");
        }
    }

    /**
     * 重置用户名
     * @param username
     * @param request
     * @return
     */
    @RequiresRoles("user")
    @PostMapping("/user/resetUsername")
    public ResponseJson resetUsername(String username,HttpServletRequest request){
        User user = userServiceImpl.selectByUsername(username);
        if(user!=null)return new ResponseJson(400,"该用户名已经被使用");
        user = userServiceImpl.getById(JwtUtil.getId(request.getHeader("Authorization")));
        if(user.isBanned())return new ResponseJson(400,"账号已被封禁,不能修改用户名");
        if(!user.isActive())return new ResponseJson(400,"请先激活账号");
        user.setUsername(username);
        if(userServiceImpl.updateById(user)){
            logger.info("用户ID为"+user.getId()+"的用户修改了用户名为:"+username);
            return new ResponseJson(200,"用户名修改成功!");
        }
        else return new ResponseJson(400,"用户名修改失败");
    }
    @RequiresRoles("user")
    @PostMapping("/user/setHead")
    public ResponseJson setHead(MultipartFile file,HttpServletRequest request){
        int id = JwtUtil.getId(request.getHeader("Authorization"));
        User user = userServiceImpl.getById(id);
        if(user.isBanned())return new ResponseJson(400,"账号已被封禁,不能设置头像");
        if(!user.isActive())return new ResponseJson(400,"请先激活账号");
        String fileName = file.getOriginalFilename();
        String type = fileName.substring(fileName.lastIndexOf('.')+1);
        String realType = FileUtil.getFileType(file);
        if(!type.equals(realType))return new ResponseJson(400,"文件的类型错误!");
        if(!(realType.equals("jpg")||realType.equals("png")||realType.equals("gif")||realType.equals("tif")||realType.equals("bmp"))){
            return new ResponseJson(400,"发送的文件格式错误");
        }
        String filePath = null;
        try {
            File directory = new File("src/head");
            String courseFile = directory.getCanonicalPath();
            filePath = courseFile+"/user-"+id+"."+realType;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(),new File(filePath));              //复制完虽然本地图片更换成功,但是前端不能实时更新头像,不知道为什么
            user.setHeadPath("user-"+id+"."+realType);
            userServiceImpl.update(user);
            logger.info("用户ID为"+id+"的用户更换了头像");
            return new ResponseJson(200,"头像设置成功!",String.valueOf(id));
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseJson(400,"头像设置失败!");
        }
    }

    /**
     * 获取用户提问箱开启状态
     * @param request
     * @return
     */
    @RequestMapping("/user/getAccept")
    public ResponseJson getAccept(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        User user = userServiceImpl.getById(JwtUtil.getId(token));
        if(user.isAccept())return new ResponseJson(200,"1");
        else return new ResponseJson(200,"0");
    }

    /**
     * 改变用户提问箱状态
     * @param request
     * @return
     */
    @RequiresRoles("user")
    @PostMapping("/user/changeAccept")
    public ResponseJson changeAccept(HttpServletRequest request){
        User user = userServiceImpl.getById(JwtUtil.getId(request.getHeader("Authorization")));
        if(user.isBanned())return new ResponseJson(400,"账号已被封禁,不能改变提问箱状态");
        if(!user.isActive())return new ResponseJson(400,"请先激活账号");
        if(1==userServiceImpl.changeAccept(user.getId())){
            logger.info("用户ID为"+user.getId()+"的用户改变了提问箱状态为:"+(user.isAccept()?"关闭":"开启"));
            return new ResponseJson(200,user.isAccept()?"0":"1");
        }
        else return new ResponseJson(400,"修改提问箱状态失败!");
    }

    /**
     * 获得用户头像
     * @param request
     * @return
     */
    @RequestMapping("/user/getHead")
    public ResponseJson getHead(HttpServletRequest request){
        User user = userServiceImpl.getById(JwtUtil.getId(request.getHeader("Authorization")));
        if(user==null)return new ResponseJson(400,"身份错误!");
        return new ResponseJson(200,String.valueOf(user.getId()));
    }
    @GetMapping("/user/download/{id}")
    public void download(HttpServletResponse response,@PathVariable int id){
        User user = userServiceImpl.getById(id);
        if(user==null)return;
        try {
            response.addHeader("content-Disposition","attachment;fileName="+user.getHeadPath());
            response.addHeader("Cache-Control","no-store,no-cache");
            File directory = new File("src/head");
            String courseFile = directory.getCanonicalPath();
            OutputStream os = response.getOutputStream();
            os.write(FileUtils.readFileToByteArray(new File(courseFile+"/"+user.getHeadPath())));
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
