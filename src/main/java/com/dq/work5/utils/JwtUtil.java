package com.dq.work5.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    //过期时间
    private static final long EXPIRE_TIME = 30*60*1000;
    //私钥
    private static final String SECRET = "cHJpdmF0ZUtleTEyMw==";    //base64加密

    //生成签名
    public static String sign(int id,String currentTimeMillis){
        try {
            String secret = id+Base64Converter.decode(SECRET);
            Date date = new Date(System.currentTimeMillis()+EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            //设置头部信息
//            Map<String,Object> header = new HashMap<>();
//            header.put("Type","Jwt");
//            header.put("alg","hs256");
            return JWT.create()
//                    .withHeader(header)
                    .withClaim("id",id)
                    .withClaim("currentTimeMillis", currentTimeMillis)
                    .withExpiresAt(date)
                    .sign(algorithm);
        }catch (Exception e){
            logger.error("JWTToken加密出现UnsupportedEncodingException异常:{}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取token中的信息无需secret也能获得
     * @param token
     * @param claim
     * @return
     */
    public static String getClaim(String token, String claim) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            // 只能输出String类型，如果是其他类型返回null
            return jwt.getClaim(claim).asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }
    public static int getId(String token){
        try {
            DecodedJWT jwt = JWT.decode(token);
            // 只能输出String类型，如果是其他类型返回null
            return jwt.getClaim("id").asInt();
        } catch (JWTDecodeException e) {
            return 0;
        }
    }
    //检验token是否正确
    public static boolean verify(String token){
        try {
            String secret = getId(token)+Base64Converter.decode(SECRET);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
