package com.dq.work5;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dq.work5.mapper.QuestionVoMapper;
import com.dq.work5.mapper.UserInfoMapper;
import com.dq.work5.pojo.Question;
import com.dq.work5.pojo.QuestionVo;
import com.dq.work5.pojo.ResponseJson;
import com.dq.work5.pojo.UserInfoVo;
import com.dq.work5.service.UserService;
import com.dq.work5.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

@SpringBootTest
class Work5ApplicationTests {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    @Autowired
    UserService userServiceImpl;
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    QuestionVoMapper questionVoMapper;
    @Test
    void contextLoads() {
        Page<QuestionVo> page = new Page<>(1,8);
        IPage<QuestionVo> iPage = questionVoMapper.getSendedQuestions(page,1);
        for(QuestionVo userInfoVo:iPage.getRecords()){
            System.out.println(userInfoVo);
        }
    }

}
