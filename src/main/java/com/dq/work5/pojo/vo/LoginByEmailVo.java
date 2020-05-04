package com.dq.work5.pojo.vo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 *
 */
public class LoginByEmailVo {
    @NotNull
    @Email
    String email;
    @NotNull
    String password;
    @NotNull
    String key;

    public LoginByEmailVo() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public LoginByEmailVo(@NotNull String email, @NotNull String password, @NotNull String key) {
        this.email = email;
        this.password = password;
        this.key = key;
    }
}
