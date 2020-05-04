package com.dq.work5.controller;

import com.dq.work5.pojo.ResponseJson;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *
 */
@RestControllerAdvice
public class ErrorController {
    @ExceptionHandler(AuthorizationException.class)     //没有权限统一返回
    public ResponseJson NotAuthorized(){
        return new ResponseJson(400,"没有权限!");
    }
}
