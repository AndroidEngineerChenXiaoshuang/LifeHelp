package com.example.administrator.lifehelp.gson;

/**
 * Created by zzz on 2017/6/5
 */

public class VerficationJson {

    /**
     * Status : error
     * Code : 1588
     * Message : 当前IP请求次数过多,需要验证码
     * verifyImg : 携带base64的字符编码
     * time : 1496799530
     */

    private String Status;
    private int Code;
    private String Message;
    private String verifyImg;
    private int time;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int Code) {
        this.Code = Code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public String getVerifyImg() {
        return verifyImg;
    }

    public void setVerifyImg(String verifyImg) {
        this.verifyImg = verifyImg;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
