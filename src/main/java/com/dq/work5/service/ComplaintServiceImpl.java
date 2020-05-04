package com.dq.work5.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dq.work5.mapper.ComplaintMapper;
import com.dq.work5.mapper.UserMapper;
import com.dq.work5.pojo.Complaint;
import com.dq.work5.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class ComplaintServiceImpl extends ServiceImpl<ComplaintMapper, Complaint> implements ComplaintService{
    @Autowired
    UserMapper userMapper;
    @Override
    public List<Complaint> getAll() {
        return baseMapper.getAll();
    }

    @Override
    public boolean updateById(Complaint entity) {
        if(entity.isBanned()){
            User user = userMapper.selectById(entity.getCid());
            user.setBanned(true);
            userMapper.updateById(user);
        }
        return baseMapper.updateById(entity)==1;
    }
}
