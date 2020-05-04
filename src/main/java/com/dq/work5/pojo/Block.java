package com.dq.work5.pojo;

/**
 *
 */
public class Block {
    private int id;
    private int uid;
    private int bid;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Block(int uid, int bid, String content) {
        this.uid = uid;
        this.bid = bid;
        this.content = content;
    }

    public Block() {
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

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public Block(int id, int uid, int bid) {
        this.id = id;
        this.uid = uid;
        this.bid = bid;
    }

    public Block(int uid, int bid) {
        this.uid = uid;
        this.bid = bid;
    }
}
