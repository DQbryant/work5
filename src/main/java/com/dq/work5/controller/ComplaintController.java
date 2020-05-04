package com.dq.work5.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dq.work5.mapper.UserInfoMapper;
import com.dq.work5.pojo.*;
import com.dq.work5.service.ComplaintService;
import com.dq.work5.service.QuestionService;
import com.dq.work5.service.UserService;
import com.dq.work5.utils.JwtUtil;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 */
@RestController
public class ComplaintController {
    @Autowired
    QuestionService questionServiceImpl;
    @Autowired
    ComplaintService complaintServiceImpl;
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    UserService userServiceImpl;
    private static final Logger logger = LoggerFactory.getLogger(ComplaintController.class);
    /**
     * 举报用户
     * @param qid 提问的id
     * @param reason 举报理由
     * @param request
     * @return
     */
    @RequiresRoles("user")
    @PutMapping("/question/complain")
    public ResponseJson complain(int qid, String reason, HttpServletRequest request){
        int id = JwtUtil.getId(request.getHeader("Authorization"));
        Question question = questionServiceImpl.getById(qid);
        if(question==null||id!=question.getAid())return new ResponseJson(400,"参数错误!");//防止参数错误
        Complaint complaint = new Complaint(id,question.getUid(),question.getContent(),reason);
        if(complaintServiceImpl.save(complaint)){
            question.setDeleted(true);                                                                //如果举报成功要删除提问
            questionServiceImpl.update(question);
            logger.info("用户id为"+id+"的用户举报了用户id为"+question.getUid()+"的用户,举报原因为:"+reason);
            return new ResponseJson(200,"举报成功");
        }
        else return new ResponseJson(400,"举报失败");
    }

    /**
     * 获得举报的分页,每页8条数据
     * @param pageNum
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/admin/getComplaints")
    public IPage<Complaint> getComplaints(int pageNum){
        QueryWrapper<Complaint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_banned",false);                     //查找未被受理且未被删除的举报
        Page<Complaint> page = new Page<>(pageNum,8);
        IPage<Complaint> iPage = complaintServiceImpl.page(page,queryWrapper);
        return iPage;
    }

    /**
     * 处理举报,并封禁账号
     * @param cid 举报的id
     * @param result 举报的处理原因
     * @return
     */
    @RequiresRoles("admin")
    @PutMapping("/admin/handle")
    public ResponseJson handler(int cid,String result,HttpServletRequest request){
        Complaint complaint = complaintServiceImpl.getById(cid);
        if(complaint==null||result==null)return new ResponseJson(400,"参数错误!");          //防止参数错误
        complaint.setResult(result);
        complaint.setBanned(true);
        if (complaintServiceImpl.updateById(complaint)){        //处理举报会顺带封禁用户
            User user = userServiceImpl.getById(JwtUtil.getId(request.getHeader("Authorization")));
            logger.info("管理员"+user.getUsername()+"处理了举报,ID为"+cid+",处理理由为:"+result);
            return new ResponseJson(200,"封禁成功!");
        } else return new ResponseJson(400,"封禁失败!");
    }

    /**
     * 忽略(删除)举报
     * @param cid
     * @return
     */
    @RequiresRoles("admin")
    @DeleteMapping("/admin/unhandle")
    public ResponseJson delete(int cid){
        if(complaintServiceImpl.removeById(cid))return new ResponseJson(200,"举报忽略成功!");
        else  return new ResponseJson(400,"举报忽略失败!");
    }
}
