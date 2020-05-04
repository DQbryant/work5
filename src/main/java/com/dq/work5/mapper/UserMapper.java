package com.dq.work5.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dq.work5.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 */
@Component
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from user where username = #{0}")
    User selectByUsername(String username);
    @Select("select * from user where email = #{0}")
    User selectByEmail(String email);
    @Select("select password from user where username = #{0}")
    String getPassword(String username);
    @Select("select role from user where id = #{0}")
    String getRole(int id);
    @Update("update user set is_active = true where id = #{0}")
    int activate(int id);
    @Update("update user set is_active = false where id = #{0}")
    int inactivate(int id);
    @Update("update user set is_accept = !is_accept where id = #{0}")
    int changeAccept(int id);
}
