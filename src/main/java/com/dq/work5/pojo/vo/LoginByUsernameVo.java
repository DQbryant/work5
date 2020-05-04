package com.dq.work5.pojo.vo;

import javax.validation.constraints.NotNull;

/**
 *
 */
public class LoginByUsernameVo {
    @NotNull
    String username;
    @NotNull
    String password;
    @NotNull
    String key;

    public LoginByUsernameVo() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public LoginByUsernameVo(@NotNull String username, @NotNull String password, @NotNull String key) {
        this.username = username;
        this.password = password;
        this.key = key;
    }
}
