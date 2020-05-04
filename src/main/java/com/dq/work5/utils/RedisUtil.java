package com.dq.work5.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate redisTemplate;

    public Object getObject(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public  void setObject(String key,Object value){
        redisTemplate.opsForValue().set(key,value);
    }
    public  void setObject(String key,Object value,int expireTime){
        redisTemplate.opsForValue().set(key,value,expireTime,TimeUnit.SECONDS);
    }

    public  boolean delete(String key){
        return redisTemplate.delete(key);
    }

    public  boolean exists(String key){
        return redisTemplate.hasKey(key);
    }

    /**
     * 获得键的存活时间,返回-2表示没有该值,返回-1表示没有设置过期时间
     * @param key
     * @return
     */
    public long ttl(String key){
        return redisTemplate.opsForValue().getOperations().getExpire("key");
    }
}
