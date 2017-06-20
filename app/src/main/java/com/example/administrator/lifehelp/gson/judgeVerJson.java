package com.example.administrator.lifehelp.gson;

/**
 * Created by zzz on 2017/6/19.
 */

public class JudgeVerJson {

    /**
     * Message : 当前IP不需要进行验证
     * Code : 1001
     */

    private String Message;
    private int Code;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int Code) {
        this.Code = Code;
    }
}
