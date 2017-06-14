package com.example.administrator.lifehelp.db;

import org.litepal.crud.DataSupport;

/**
 * Created by zzz on 2017/6/12.
 */

public class UserInfo extends DataSupport {
    /**
     * Status : SignIn_Success
     * message : 登陆成功,userinfo中是用户信息
     * Code : 5754
     * token : ewoiZ3JhZGUiOiAiMiIsCiJzdGFydF90aW1lIjogIjE0OTcyNTM1NjUiLAoiZXhwX3RpbWUiOiAiMTQ5ODU0OTU2NSIsCiJpcCI6ICIxOTIuMTY4LjQzLjg1IiwKInVzZXJuYW1lIjogIjE1MTUxNTE1MTUxIiwKInVzZXJfaWQiOiAiMiIsCiJwcmltYXJ5X2tleSI6ICI4MDM2MTkzNjQiLAoic2lnbmF0dXJlX3NlcnZlciI6ICJodHRwOi8vMTkyLjE2OC40My42NyIKfQ==.05760e3bf450236a12a82bc66ea38a8b7f7fea284abb904069faefa4b6e7c0bd
     * TlssToken : eJxNjVFPgzAUhf9Ln42U1jLnG0wMDdNZx4IYkqZAuzVmrLIOUON-FwlTc9**755zPkGyXF*Ksjycasvtu5HgBkBwMWJdydpqpWUzQJf83qSFMbriwnLcVP9Sx*qVj*ondAUhxMjzzlL2RjeSC2WnUkLQ8DLZVjZHfagHgeCwgzCEf9LqvRwr5zNEMPHIeU9vB3wfZgvKbq8zm7plESR7OKdp7kTdE0tyJ0gTsgq9VnW587ZZ03DbqnjB6M5f7cpl-IC7njzXPmMfsZmFhYg2d1qdCtZHkvpu4Gbi8cWAr29lpVho
     * userinfo : {"nickname":"myb_nq8i5","avatar":"8","balance":0,"credit_grade":"4.50"}
     * nickname : myb_nq8i5
     * avatar : 8
     * balance : 0
     * credit_grade : 4.50
     */

    private String Status;
    private String message;
    private int Code;
    private String token;
    private String TlssToken;
    private String nickname;
    private String avatar;
    private double balance;
    private double credit_grade;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int Code) {
        this.Code = Code;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTlssToken() {
        return TlssToken;
    }

    public void setTlssToken(String TlssToken) {
        this.TlssToken = TlssToken;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getCredit_grade() {
        return credit_grade;
    }

    public void setCredit_grade(double credit_grade) {
        this.credit_grade = credit_grade;
    }
}
