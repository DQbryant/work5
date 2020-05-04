package com.dq.work5.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dq.work5.pojo.Question;
import com.dq.work5.pojo.QuestionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@Mapper
public interface QuestionVoMapper {
    @Select("select u1.username,u1.email,u2.username ausername,u2.email aemail,q.content,q.answer " +
            "from user u1,user u2,question q where u1.id=q.uid and u2.id=q.aid and q.aid=#{id} group by q.id")
    IPage<QuestionVo> getAllQuestions(@Param("page") Page<?> page,@Param("id") int id);
    @Select("select u1.username,u1.email,u2.username ausername,u2.email aemail,q.content,q.answer " +
            "from user u1,user u2,question q where u1.id=q.uid and u2.id=q.aid and q.aid=#{id} and q.answer!= '' group by q.id")
    IPage<QuestionVo> getAnsweredQuestions(@Param("page") Page<?> page,@Param("id") int id);
    @Select("select u1.username,u1.email,u2.username ausername,u2.email aemail,q.content,q.answer " +
            "from user u1,user u2,question q where u1.id=q.uid and u2.id=q.aid and q.uid=#{id} group by q.id")
    IPage<QuestionVo> getSendedQuestions(@Param("page") Page<?> page,@Param("id") int id);
}
