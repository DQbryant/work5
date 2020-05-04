package com.dq.work5.pojo;

/**
 *
 */
public class UserInfoVo {
    private int uid;
    private String username;
    private String email;
    private boolean isActive = false;
    private boolean isBanned = false;
    private int questionedNum = 0;
    private int questionsNum = 0;
    private int answerNum = 0;

    @Override
    public String toString() {
        return "UserInfoVo{" +
                "uid=" + uid +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", isActive=" + isActive +
                ", isBanned=" + isBanned +
                ", questionedNum=" + questionedNum +
                ", questionsNum=" + questionsNum +
                ", answerNum=" + answerNum +
                '}';
    }

    public UserInfoVo() {
    }


    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public int getQuestionedNum() {
        return questionedNum;
    }

    public void setQuestionedNum(int questionedNum) {
        this.questionedNum = questionedNum;
    }

    public int getQuestionsNum() {
        return questionsNum;
    }

    public void setQuestionsNum(int questionsNum) {
        this.questionsNum = questionsNum;
    }

    public int getAnswerNum() {
        return answerNum;
    }

    public void setAnswerNum(int answerNum) {
        this.answerNum = answerNum;
    }

    public UserInfoVo(int uid, String username, String email, boolean isActive, boolean isBanned, int questionedNum, int questionsNum, int answerNum) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.isActive = isActive;
        this.isBanned = isBanned;
        this.questionedNum = questionedNum;
        this.questionsNum = questionsNum;
        this.answerNum = answerNum;
    }
}
