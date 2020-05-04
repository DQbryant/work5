package com.dq.work5.pojo;

/**
 *
 */
public class Question {
    private int id;
    private int uid;
    private String content;
    private String answer;
    private int aid;
    private boolean deleted = false;
    public Question() {
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Question(int id, int uid, String content, String answer, int aid) {
        this.id = id;
        this.uid = uid;
        this.content = content;
        this.answer = answer;
        this.aid = aid;
    }

    public Question(int uid, String content, int aid) {
        this.uid = uid;
        this.content = content;
        this.aid = aid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }
}
