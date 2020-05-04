package com.dq.work5.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dq.work5.pojo.User;

/**
 *
 */
public interface UserService extends IService<User>{
    User selectByUsername(String username);

    User selectByEmail(String email);


    User selectById(int id);

    int update(User user);

    int activate(int id);

    int inactivate(int id);

    int changeAccept(int id);

    String getRole(int id);
}
