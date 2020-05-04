package com.dq.work5.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dq.work5.pojo.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 */
@Component
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    @Select("select * from question where aid = #{0}")
    List<Question> selectByUid(int aid);
    @Select("select * from question where uid = #{0} and answer != '' and deleted != true")
    List<Question> getAnsweredQuestions(int uid);
    @Select("select id,content,answer,aid,deleted from question where aid = #{0} and answer != '' and deleted != true")
    List<Question> getMyAskedQuestions(int aid);
    @Select("select id,content,answer,aid,deleted from question where aid = #{0} and answer = '' and deleted != true")
    List<Question> getUnansweredQuestions(int aid);
    @Select("select id,content,answer,aid,deleted from question where aid = #{0} and deleted = true")
    List<Question> getDeletedQuestions(int aid);
}
