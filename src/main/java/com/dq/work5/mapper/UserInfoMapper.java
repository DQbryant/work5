package com.dq.work5.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dq.work5.pojo.UserInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfoVo> {
    @Select("select u.id uid,u.username,u.email,u.is_active,u.is_banned," +
            "count(if(q.aid = u.id,true,null)) questionedNum,count(if(q.uid = u.id,true,null)) questionsNum," +
            "count(if(q.aid = u.id and q.answer!='',true,null)) answerNum from user u,question q " +
            "where u.role != 'admin' group by u.id")
    IPage<UserInfoVo> selectPageVo(Page<?> page);

    @Select("select u.id uid,u.username,u.email,u.is_active,u.is_banned," +
            "count(if(q.aid = u.id,true,null)) questionedNum,count(if(q.uid = u.id,true,null)) questionsNum," +
            "count(if(q.aid = u.id and q.answer!='',true,null)) answerNum from user u,question q " +
            "where u.username like concat('%',#{username},'%') and u.role != 'admin' group by u.id")
    IPage<UserInfoVo> selectPageByUsername(@Param("page") Page<?> page,@Param("username") String username);
    @Select("select u.id uid,u.username,u.email,u.is_active,u.is_banned," +
            "count(if(q.aid = u.id,true,null)) questionedNum,count(if(q.uid = u.id,true,null)) questionsNum," +
            "count(if(q.aid = u.id and q.answer!='',true,null)) answerNum from user u,question q " +
            "where u.email = #{email} and u.role != 'admin' group by u.id")
    IPage<UserInfoVo> selectPageByEmail(@Param("page") Page<?> page,@Param("email") String email);
}
