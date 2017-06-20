package com.example.administrator.lifehelp.gson;

/**
 * Created by zzz on 2017/6/5
 */

public class VerficationJson {


    /**
     * Message : 验证码正确,并返回验证码Token
     * VerifyToken : 192-168-43-85-VerifyToken-feQaGfwhGtYc
     * Code : 1006
     * VerifyImg ：ew
     */

    private String VerifyImg;
    private String Message;
    private String VerifyToken;
    private int Code;

    public String getVerifyImg() {
        return VerifyImg;
    }

    public void setVerifyImg(String VerifyImg) {
        this.VerifyImg = VerifyImg;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public String getVerifyToken() {
        return VerifyToken;
    }

    public void setVerifyToken(String VerifyToken) {
        this.VerifyToken = VerifyToken;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int Code) {
        this.Code = Code;
    }
}
