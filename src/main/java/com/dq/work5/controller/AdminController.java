package com.dq.work5.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dq.work5.mapper.ComplaintMapper;
import com.dq.work5.mapper.QuestionVoMapper;
import com.dq.work5.mapper.UserInfoMapper;
import com.dq.work5.pojo.*;
import com.dq.work5.service.ComplaintService;
import com.dq.work5.service.UserService;
import com.dq.work5.utils.JwtUtil;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.Time;

/**
 *
 */
@RequiresRoles("admin")
@RestController
public class AdminController {
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    QuestionVoMapper questionVoMapper;
    @Autowired
    UserService userServiceImpl;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);


    /**
     * 获得用户总览的分页,每页8条数据
     * @param pageNum
     * @return
     */
    @GetMapping("/admin/getUserInfos")
    public IPage<UserInfoVo> getUserInfos(int pageNum){
        Page<UserInfoVo> page = new Page<>(pageNum,8);
        return  userInfoMapper.selectPageVo(page);
    }

    /**
     * 根据用户名查找用户的分页,每页3条数据
     * @param pageNum
     * @param username
     * @return
     */
    @GetMapping("/admin/getByUsername")
    public IPage<UserInfoVo> getByUsername(int pageNum,String username){
        Page<UserInfoVo> page = new Page<>(pageNum,3);
        return userInfoMapper.selectPageByUsername(page,username);
    }

    /**
     * 根据用户邮箱查找用户的分页,每页3条数据
     * @param pageNum
     * @param email
     * @return
     */
    @GetMapping("/admin/getByEmail")
    public IPage<UserInfoVo> getByEmail(int pageNum,String email){
        Page<UserInfoVo> page = new Page<>(pageNum,3);
        return userInfoMapper.selectPageByEmail(page,email);
    }

    /**
     * 查找用户所有收到的提问的分页,每页3条数据
     * @param pageNum
     * @param id
     * @return
     */
    @GetMapping("/admin/getQuestions")
    public IPage<QuestionVo> getQuestions(int pageNum,int id){
        Page<QuestionVo> page = new Page<>(pageNum,3);
        return questionVoMapper.getAllQuestions(page,id);
    }

    /**
     * 查找用户回复的提问的分页,每页3条数据
     * @param pageNum
     * @param id
     * @return
     */
    @GetMapping("/admin/getAnswered")
    public IPage<QuestionVo> getAnswered(int pageNum,int id){
        Page<QuestionVo> page = new Page<>(pageNum,3);
        return questionVoMapper.getAnsweredQuestions(page,id);
    }

    /**
     * 查找用户发起的提问的分页,每页3条数据
     * @param pageNum
     * @param id
     * @return
     */
    @GetMapping("/admin/getSended")
    public IPage<QuestionVo> getSended(int pageNum,int id){
        Page<QuestionVo> page = new Page<>(pageNum,3);
        return questionVoMapper.getSendedQuestions(page,id);
    }

    /**
     * 封禁账户
     * @param uid
     * @return
     */
    @PostMapping("/admin/ban")
    public ResponseJson ban(int uid, HttpServletRequest request){
        User user = userServiceImpl.getById(uid);
        if(user==null)return new ResponseJson(400,"参数错误!");         //确定参数正确
        if(user.isBanned())return new ResponseJson(400,"请勿重复封禁!");   //不能重复封禁
        user.setBanned(true);
        if(userServiceImpl.update(user)==1){
            User user1 = userServiceImpl.getById(JwtUtil.getId(request.getHeader("Authorization")));//获得管理员身份
            logger.info("管理员"+user1.getUsername()+"封禁了用户:"+user.getUsername()+",用户id:"+uid);
            return new ResponseJson(200,"封禁成功");
        }
        else return new ResponseJson(400,"封禁失败!");
    }

    /**
     * 解封账户
     * @param uid
     * @return
     */
    @PostMapping("/admin/unban")
    public ResponseJson unban(int uid,HttpServletRequest request){
        User user = userServiceImpl.getById(uid);
        if(user==null)return new ResponseJson(400,"参数错误!");             //确定参数正确
        if(!user.isBanned())return new ResponseJson(400,"请勿重复解封!");   //不能重复解封
        user.setBanned(false);
        if(userServiceImpl.update(user)==1) {
            User user1 = userServiceImpl.getById(JwtUtil.getId(request.getHeader("Authorization")));//获得管理员身份
            logger.info("管理员"+user1.getUsername()+"解封了"+user.getUsername()+",用户id:"+uid);
            return new ResponseJson(200, "解封成功");
        }
        else return new ResponseJson(400,"解封失败!");
    }
}
