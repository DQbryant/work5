package com.dq.work5.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dq.work5.mapper.BlockMapper;
import com.dq.work5.mapper.QuestionMapper;
import com.dq.work5.pojo.*;
import com.dq.work5.service.BlockService;
import com.dq.work5.service.QuestionService;
import com.dq.work5.service.UserService;
import com.dq.work5.service.UserServiceImpl;
import com.dq.work5.utils.JwtUtil;
import org.apache.ibatis.annotations.Delete;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 */
@RestController
public class QuestionController {
    @Autowired
    QuestionService questionServiceImpl;
    @Autowired
    UserService userServiceImpl;
    @Autowired
    BlockService blockServiceImpl;
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
    /**
     * 根据用户名发送提问
     * @param content   提问内容
     * @param username  答复者用户名
     * @param request
     * @return
     */
    @RequiresRoles("user")
    @PostMapping("/question/ask/username")
    public ResponseJson askByUsername(String content, String username, HttpServletRequest request){
        if(content==null||username==null||content.equals(""))return new ResponseJson(200,"参数错误");//非空控制,提问内容不应为空
        User user = userServiceImpl.selectByUsername(username);                                                   //根据用户名查找用户
        if(user==null)return new ResponseJson(400,"提问的用户不存在!");                                 //提问的用户不存在
        String token = request.getHeader("Authorization");
        int id = JwtUtil.getId(token);                                                                             //从token中获取提问者ID
        User user1 = userServiceImpl.getById(id);
        if(user1.isBanned())return new ResponseJson(400,"账号已被封禁,不能发起提问");
        if(!user1.isActive())return new ResponseJson(400,"请先激活账号");
        if(!user1.isAccept())return new ResponseJson(400,"请先开启提问箱");
        if(id==user.getId())return new ResponseJson(400,"不能给自己发送提问!");
        if(user.isBanned()||!user.isAccept()||!user.isActive())return new ResponseJson(400,"提问失败!您所提问的用户可能未激活、未开启提问箱或已被封号");
        Block block = new Block(user.getId(),id);
        if(blockServiceImpl.exists(block))return new ResponseJson(400,"提问失败!对方已将您拉黑");
        Question question = new Question(id,content,user.getId());
        if(questionServiceImpl.save(question)) {               //存入提问
            logger.info("用户ID为"+id+"的用户向ID为"+user.getId()+"的用户发送了提问,问题为"+content);
            return new ResponseJson(200,"提问成功!");
        }
        return new ResponseJson(400,"提问失败!");
    }

    /**
     * 根据用户邮箱发送提问
     * @param content   提问内容
     * @param email     答复者邮箱
     * @param request
     * @return
     */
    @RequiresRoles("user")
    @PostMapping("/question/ask/email")
    public ResponseJson askByEmail(String content,String email,HttpServletRequest request){
        if(content==null||email==null||content.equals(""))return new ResponseJson(200,"参数错误");//同上
        User user = userServiceImpl.selectByEmail(email);                                                       //根据邮箱查找用户
        if(user==null)return new ResponseJson(400,"提问的用户不存在!");
        String token = request.getHeader("Authorization");
        int id = JwtUtil.getId(token);
        User user1 = userServiceImpl.getById(id);
        if(user1.isBanned())return new ResponseJson(400,"账号已被封禁,不能发起提问");
        if(!user1.isActive())return new ResponseJson(400,"请先激活账号");
        if(!user1.isAccept())return new ResponseJson(400,"请先开启提问箱");
        if(id==user.getId())return new ResponseJson(400,"不能给自己发送提问!");
        if(user.isBanned()||!user.isAccept()||!user.isActive())return new ResponseJson(400,"提问失败!您所提问的用户可能未激活、未开启提问箱或已被封号");
        Block block = new Block(user.getId(),id);
        if(blockServiceImpl.exists(block))return new ResponseJson(400,"提问失败!对方已将您拉黑");
        Question question = new Question(id,content,user.getId());
        if(questionServiceImpl.save(question)) {
            logger.info("用户ID为"+id+"的用户向ID为"+user.getId()+"的用户发送了提问,问题为"+content);
            return new ResponseJson(200,"提问成功!");
        }
        return new ResponseJson(400,"提问失败!");
    }

    /**
     * 改变提问的回答
     * @param qid       问题id
     * @param answer    问题回答
     * @param request
     * @return
     */
    @RequiresRoles("user")
    @PostMapping("/question/changeAnswer")
    public ResponseJson change(int qid,String answer,HttpServletRequest request){
        Question question = questionServiceImpl.selectById(qid);                                //查询提问
        if(question==null||answer==null)return new ResponseJson(400,"参数错误!");   //非空控制
        int id = JwtUtil.getId(request.getHeader("Authorization"));
        if(question.getAid()!=id)return new ResponseJson(400,"不能为其他用户回答!"); //身份确认
        User user1 = userServiceImpl.getById(id);
        if(user1.isBanned())return new ResponseJson(400,"账号已被封禁,不能回复提问");
        if(!user1.isActive())return new ResponseJson(400,"请先激活账号");
        if(!user1.isAccept())return new ResponseJson(400,"请先开启提问箱");
        question.setAnswer(answer);
        if(questionServiceImpl.update(question)==1){
            logger.info("用户ID为"+id+"的用户修改了提问ID为"+qid+"的提问的回答为:"+answer);
            return new ResponseJson(200,"操作成功");
        }else return new ResponseJson(400,"操作失败");
    }

    /**
     * 删除提问
     * @param qid       提问id
     * @param request
     * @return
     */
    @RequiresRoles("user")
    @DeleteMapping("/question/delete")
    public ResponseJson delete(int qid,HttpServletRequest request){
        int id = JwtUtil.getId(request.getHeader("Authorization"));
        Question question = questionServiceImpl.getById(qid);
        if(question==null)return new ResponseJson(400,"参数错误");                      //防止随意传参
        if(question.getAid()!=id) return new ResponseJson(400,"不能为其他用户删除提问!");//身份确认
        User user1 = userServiceImpl.getById(id);
        if(user1.isBanned())return new ResponseJson(400,"账号已被封禁,不能删除提问");
        if(!user1.isActive())return new ResponseJson(400,"请先激活账号");
        if(!user1.isAccept())return new ResponseJson(400,"请先开启提问箱");
        question.setDeleted(true);
        if(questionServiceImpl.update(question)==1){
            logger.info("用户ID为"+id+"的用户删除了提问ID为"+qid+"的提问");
            return new ResponseJson(200,"操作成功");
        }else return new ResponseJson(400,"操作失败");
    }

    /**
     * 恢复删除的提问
     * @param qid       提问id
     * @param request
     * @return
     */
    @RequiresRoles("user")
    @PostMapping("/question/recover")
    public ResponseJson recover(int qid,HttpServletRequest request){
        int id = JwtUtil.getId(request.getHeader("Authorization"));
        Question question = questionServiceImpl.getById(qid);
        if(question==null)return new ResponseJson(400,"参数错误");                      //防止随意传参
        if(question.getAid()!=id) return new ResponseJson(400,"不能为其他用户恢复删除!");//身份确认
        User user1 = userServiceImpl.getById(id);
        if(user1.isBanned())return new ResponseJson(400,"账号已被封禁,不能恢复被删除的提问");
        if(!user1.isActive())return new ResponseJson(400,"请先激活账号");
        if(!user1.isAccept())return new ResponseJson(400,"请先开启提问箱");
        question.setDeleted(false);
        if(questionServiceImpl.update(question)==1){
            logger.info("用户ID为"+id+"的用户恢复了提问ID为"+qid+"的提问");
            return new ResponseJson(200,"操作成功");
        }else return new ResponseJson(400,"操作失败");
    }

    /**
     * 拉黑提问用户
     * @param qid      提问id
     * @param request
     * @return
     */
    @RequiresRoles("user")
    @PutMapping("/question/block")
    public ResponseJson block(int qid,HttpServletRequest request){
        int id = JwtUtil.getId(request.getHeader("Authorization"));
        Question question = questionServiceImpl.getById(qid);
        if(question==null||id!=question.getAid())return new ResponseJson(400,"发送的参数有误");//参数控制和身份确认
        Block block = new Block(id,question.getUid(),question.getContent());
        if(blockServiceImpl.exists(block))return new ResponseJson(400,"请勿重复拉黑");        //不能重复拉黑
        if(blockServiceImpl.save(block)){
            question.setDeleted(true);                                                                    //拉黑要删除提问
            questionServiceImpl.update(question);
            logger.info("用户ID为"+id+"的用户拉黑了ID为"+question.getUid()+"的用户,并删除了ID为"+qid+"的提问");
            return new ResponseJson(200,"拉黑成功");
        } else return new ResponseJson(400,"拉黑失败!");
    }

    /**
     * 解除拉黑
     * @param bid       黑名id
     * @param request
     * @return
     */
    @RequiresRoles("user")
    @DeleteMapping("/question/unblock")
    public ResponseJson unblock(int bid,HttpServletRequest request){
        int id = JwtUtil.getId(request.getHeader("Authorization"));
        Block block = blockServiceImpl.getById(bid);
        if(block==null||id!=block.getUid())return new ResponseJson(400,"参数错误!");        //参数控制和身份确认
        if(blockServiceImpl.removeById(bid)){                                   //解除拉黑,但是不会恢复被删除提问
            logger.info("用户ID为"+id+"的用户解除了对ID为"+block.getBid()+"的用户的拉黑");
            return new ResponseJson(200,"解除拉黑成功");
        }
        return new ResponseJson(400,"拉黑失败");
    }

    /**
     * 查看用户已被回复的提问的分页,每页8条数据
     * @param pageNum
     * @param request
     * @return
     */
    @RequiresRoles("user")
    @PostMapping("/question/getAnswered")
    public IPage<Question> getAnsweredQuestions(int pageNum,HttpServletRequest request){
        int id = JwtUtil.getId(request.getHeader("Authorization"));
        User user = userServiceImpl.getById(id);
        if(!user.isActive()||!user.isAccept()||user.isBanned())return null;                              //如果用户未激活、未开启提问箱、已封禁不会返回任何数据
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("uid",id).ne("answer","").eq("deleted",false);//查找内容改用户发起的,不为空,且未被删除的提问
        Page<Question> page = new Page<>(pageNum,3);
        IPage<Question> iPage = questionServiceImpl.page(page,questionQueryWrapper);
        for(Question question:iPage.getRecords()){                                                         //提问ID不能返回
            question.setUid(0);
        }
        return iPage;
    }

    /**
     * 获得用户已经回复了的提问的分页
     * @param pageNum
     * @param request
     * @return
     */
    @RequiresRoles("user")
    @PostMapping("/question/getMyAnswered")
    public IPage<Question> getMyAnswered(int pageNum,HttpServletRequest request){
        int id = JwtUtil.getId(request.getHeader("Authorization"));
        User user = userServiceImpl.getById(id);
        if(!user.isActive()||!user.isAccept()||user.isBanned())return null;                              //如果用户未激活、未开启提问箱、已封禁不会返回任何数据
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("aid",id).ne("answer","").eq("deleted",false);//查找用户未回复的,未被删除的提问
        Page<Question> page = new Page<>(pageNum,8);
        IPage<Question> iPage = questionServiceImpl.page(page,questionQueryWrapper);
        for(Question question:iPage.getRecords()){                                                      //返回的数据不能包含提问者id
            question.setUid(0);
        }
        return iPage;
    }

    /**
     * 获得删除的提问的分页
     * @param pageNum
     * @param request
     * @return
     */
    @RequiresRoles("user")
    @PostMapping("/question/getDeleted")
    public IPage<Question> getDeleted(int pageNum,HttpServletRequest request){
        int id = JwtUtil.getId(request.getHeader("Authorization"));
        User user = userServiceImpl.getById(id);
        if(!user.isActive()||!user.isAccept()||user.isBanned())return null;                              //如果用户未激活、未开启提问箱、已封禁不会返回任何数据
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("aid",id).eq("deleted",true);                       //查找删除的提问
        Page<Question> page = new Page<>(pageNum,8);
        IPage<Question> iPage = questionServiceImpl.page(page,questionQueryWrapper);
        for(Question question:iPage.getRecords()){                                                      //返回的数据不能包含提问者id
            question.setUid(0);
        }
        return iPage;
    }

    /**
     * 获得未回复的提问
     * @param pageNum
     * @param request
     * @return
     */
    @RequiresRoles("user")
    @PostMapping("/question/getUnanswered")
    public IPage<Question> getUnanswered(int pageNum,HttpServletRequest request){
        int id = JwtUtil.getId(request.getHeader("Authorization"));
        User user = userServiceImpl.getById(id);
        if(!user.isActive()||!user.isAccept()||user.isBanned())return null;                              //如果用户未激活、未开启提问箱、已封禁不会返回任何数据
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("aid",id).eq("answer","").eq("deleted",false);//查找内容为空,且未被删除的提问
        Page<Question> page = new Page<>(pageNum,8);
        IPage<Question> iPage = questionServiceImpl.page(page,questionQueryWrapper);
        for(Question question:iPage.getRecords()){                                                      //返回的数据不能包含提问者id
            question.setUid(0);
        }
        return iPage;
    }

    /**
     * 获得我的黑名单的分页,仍然不携带提问者id
     * @param pageNum
     * @param request
     * @return
     */
    @RequiresRoles("user")
    @PostMapping("/question/getBlocks")
    public IPage<Block> getMyBlocks(int pageNum ,HttpServletRequest request){
        int id = JwtUtil.getId(request.getHeader("Authorization"));             //获得用户id
        QueryWrapper<Block> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",id);                                          //查找改用户id的黑名单,
        Page<Block> page = new Page<>(pageNum,8);
        IPage<Block> iPage = blockServiceImpl.page(page,queryWrapper);
        for(Block b:iPage.getRecords()){                                            //返回的数据不能包含提问者id
            b.setBid(0);
        }
        return iPage;
    }
}
