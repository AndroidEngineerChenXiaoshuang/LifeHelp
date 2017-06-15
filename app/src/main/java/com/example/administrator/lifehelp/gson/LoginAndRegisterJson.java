package com.example.administrator.lifehelp.gson;

/**
 * Created by zzz on 2017/6/7
 */

public class LoginAndRegisterJson {


    /**
     * Status : SignIn_Success
     * message : 注册成功,userinfo中是用户信息
     * Code : 5854
     * token : ewoiZ3JhZGUiOiAiMiIsCiJzdGFydF90aW1lIjogIjE0OTcyMjk3MzkiLAoiZXhwX3RpbWUiOiAiMTQ5ODUyNTczOSIsCiJpcCI6ICIxOTIuMTY4LjQzLjg1IiwKInVzZXJuYW1lIjogIjE4MTgxODE4MTgxIiwKInVzZXJfaWQiOiAiMyIsCiJwcmltYXJ5X2tleSI6ICI0NzQ3NDMyOTciLAoic2lnbmF0dXJlX3NlcnZlciI6ICJodHRwOi8vMTkyLjE2OC40My42NyIKfQ==.f7dd98b84508860b2281c031dedb3a7a9407b9cd31d0850fe4f36e2f9bd3af18
     * TlssToken : eJxNjVFvgjAUhf8Lr1tmWwRliU-qFoemEXCExKQpUPA6LV2pE7Lsv4*hbst9*75zzv20omX4wLOsOknDTKuE9Wgh677HkAtpoAChO4jHv3fVXCnIGTfM1vm-Vp2-sV79lIYIIZu47k2KRoEWjBfmMuo4DukiV-shdA2V7ARB2MHERuhPGjiKftIbEeKNbO-2D8oOr*ab6WI9bQNainOstoMknftjXVDcxOFhJnfeUcZh06azJosONHCj9aJ8TeC52PFNUQW0Bj9cnvZPMHxB9M5-T6MEZ6p09*ftQK4Umlhf36GmWeg_
     * userinfo : {"nickname":"myb_h6yfi","avatar":6,"balance":0,"credit_grade":4.5}
     */

    private String Status;
    private String message;
    private int Code;
    private String token;
    private String TlssToken;
    private UserinfoBean userinfo;

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

    public UserinfoBean getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserinfoBean userinfo) {
        this.userinfo = userinfo;
    }

    public static class UserinfoBean {
        /**
         * nickname : myb_h6yfi
         * avatar : 6
         * balance : 0
         * credit_grade : 4.5
         */

        private String nickname;
        private String avatar;
        private double balance;
        private double credit_grade;

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
}
