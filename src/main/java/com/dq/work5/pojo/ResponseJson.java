package com.dq.work5.pojo;

/**
 *
 */
public class ResponseJson {
    //状态码
    private Integer code;
    //返回信息
    private String msg;
    //返回的数据
    private String data;

    public ResponseJson() {
    }

    public ResponseJson(Integer code, String msg, String data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseJson(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
