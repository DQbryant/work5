package com.dq.work5.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dq.work5.mapper.QuestionMapper;
import com.dq.work5.mapper.UserInfoMapper;
import com.dq.work5.mapper.UserMapper;
import com.dq.work5.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class UserServiceImpl  extends ServiceImpl<UserMapper,User> implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    QuestionMapper questionMapper;
    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public User selectByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User selectByEmail(String email) {
        return userMapper.selectByEmail(email);
    }
    

    @Override
    public User selectById(int id) {
        return userMapper.selectById(id);
    }

    @Override
    public int update(User user) {
        return userMapper.updateById(user);
    }

    @Override
    public int activate(int id) {
        return userMapper.activate(id);
    }

    @Override
    public int inactivate(int id) {
        return userMapper.inactivate(id);
    }

    @Override
    public int changeAccept(int id) {
        return userMapper.changeAccept(id);
    }

    @Override
    public String getRole(int id) {
        return userMapper.getRole(id);
    }

}
