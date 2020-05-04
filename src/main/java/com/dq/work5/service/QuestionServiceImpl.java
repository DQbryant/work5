package com.dq.work5.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dq.work5.mapper.ComplaintMapper;
import com.dq.work5.mapper.QuestionMapper;
import com.dq.work5.mapper.UserMapper;
import com.dq.work5.pojo.Complaint;
import com.dq.work5.pojo.Question;
import com.dq.work5.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {
    @Autowired
    QuestionMapper questionMapper;
    @Autowired
    ComplaintMapper complaintMapper;
    @Override
    public List<Question> getAllQuestion(int aid) {
        return questionMapper.selectByUid(aid);
    }

    @Override
    public Question selectById(int id) {
        return questionMapper.selectById(id);
    }

    @Override
    public int update(Question question) {
        return questionMapper.updateById(question);
    }

    @Override
    public List<Question> getMyAskedQuestions(int aid) {
        return questionMapper.getMyAskedQuestions(aid);
    }

    @Override
    public List<Question> getUnansweredQuestions(int aid) {
        return questionMapper.getUnansweredQuestions(aid);
    }

    @Override
    public List<Question> getDeletedQuestions(int aid) {
        return questionMapper.getDeletedQuestions(aid);
    }

    @Override
    public int complain(Complaint complaint) {
        return complaintMapper.insert(complaint);
    }

    @Override
    public List<Question> getAnsweredQuestions(int uid) {
        return questionMapper.getAnsweredQuestions(uid);
    }
}
