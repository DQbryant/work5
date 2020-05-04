package com.dq.work5.pojo.vo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 *
 */
public class RegisterVo {
    @NotNull
    private String username;
    @NotNull
    @Email
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String key;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public RegisterVo() {
    }

    public RegisterVo(@NotNull String username, @NotNull @Email String email, @NotNull String password, @NotNull String key) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.key = key;
    }
}
