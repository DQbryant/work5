package com.dq.work5.utils;

/**
 *
 */
public class PasswordUtil {
    public static String check(String password){
        int length = password.length();
        if(length<6||length>20)return "密码太短或太长";
        if(password.matches("[0-9]+"))return "密码不能为全数字";
        return null;
    }
}
