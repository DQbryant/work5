package com.dq.work5.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dq.work5.pojo.Complaint;
import com.dq.work5.pojo.Question;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 *
 */
public interface QuestionService extends IService<Question> {
    List<Question> getAllQuestion(int aid);
    Question selectById(int id);
    int update(Question question);
    List<Question> getMyAskedQuestions(int aid);
    List<Question> getUnansweredQuestions(int aid);
    List<Question> getDeletedQuestions(int aid);
    int complain(Complaint complaint);
    List<Question> getAnsweredQuestions(int uid);
}
