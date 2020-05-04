package com.dq.work5.pojo;

/**
 *
 */
public class User {
    private int id;
    private String username;
    private String email;
    private boolean isActive = false;
    private boolean isBanned = false;
    private String password;
    private String role = "user";
    private boolean isAccept = false;
    private String headPath = "default.png";
    public static User defaultUser = new User(0,"游客","无",false,null,"游客",false,"default.png");

    public String getHeadPath() {
        return headPath;
    }

    public void setHeadPath(String headPath) {
        this.headPath = headPath;
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(int id, String username, String email, boolean isActive, String password, String role, boolean isAccept, String headPath) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.isActive = isActive;
        this.password = password;
        this.role = role;
        this.isAccept = isAccept;
        this.headPath = headPath;
    }

    public User() {
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAccept() {
        return isAccept;
    }

    public void setAccept(boolean accept) {
        isAccept = accept;
    }
}
