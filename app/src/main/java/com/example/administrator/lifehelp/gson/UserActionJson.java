package com.example.administrator.lifehelp.gson;

/**
 * Created by zzz on 2017/5/25.
 */

public class UserActionJson {

    /**
     * Status : error
     * Code : 1505
     * Message : IP请求次数过多,并且生成新验证码失败
     * time : 1495698436
     * verifyImg : base64
     */

    private String Status;
    private int Code;
    private String Message;
    private int time;
    private String verifyImg;

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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getVerifyImg(){
        return verifyImg;
    }

    public void setVerifyImg(String verifyImg){
        this.verifyImg = verifyImg;
    }
}
