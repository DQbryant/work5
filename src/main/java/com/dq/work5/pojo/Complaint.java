package com.dq.work5.pojo;

/**
 *
 */
//举报
public class Complaint {
    private int id;
    private int uid;//举报者id
    private int cid;//被举报者id
    private String question;//问题内容
    private String reason;//举报理由
    private String result = "";//处理理由
    private boolean isBanned = false;//处理结果

    public Complaint() {
    }

    public Complaint(int uid, int cid, String question, String reason) {
        this.uid = uid;
        this.cid = cid;
        this.question = question;
        this.reason = reason;
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

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }
}
