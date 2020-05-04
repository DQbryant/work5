package com.dq.work5.pojo.vo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 *
 */
public class ResetEmailVo {
    @NotNull
    String password;
    @NotNull
    @Email
    String email;
    @NotNull
    String key;

    public ResetEmailVo() {
    }

    public ResetEmailVo(@NotNull String password, @NotNull @Email String email, @NotNull String key) {
        this.password = password;
        this.email = email;
        this.key = key;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
