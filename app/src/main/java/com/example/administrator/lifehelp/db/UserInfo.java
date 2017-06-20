package com.example.administrator.lifehelp.db;

import org.litepal.crud.DataSupport;

/**
 * Created by zzz on 2017/6/12.
 */

public class UserInfo extends DataSupport {

    /**
     * token_base64 : ewoiZ3JhZGUiOiAiMiIsCiJzdGFydF90aW1lIjogIjE0OTc4NzMyNzgiLAoiZXhwX3RpbWUiOiAiMTQ5OTE2OTI3OCIsCiJpcCI6ICIxOTIuMTY4LjQzLjg1IiwKInVzZXJuYW1lIjogIjE4ODg4ODg4ODg4IiwKInVzZXJfaWQiOiAiMyIsCiJwcmltYXJ5X2tleSI6ICItNTIwMTg2ODU5IiwKInNpZ25hdHVyZV9zZXJ2ZXIiOiAiaHR0cDovLzE5Mi4xNjguNDMuNjciLAoiVGVuQ2VudFRva2VuIjogImVKeE5qVjFQZ3pBWWhmOUxielhhai1FeGsxMHd3YmxzWElnRXBqRnBHbHBxUjJTMTFKWEYqTjlGdHFudjVmT2NjOTVQa0s4ZnIxaFY3VDVhUyoxQkMzQURJTGdjc2VLaXRhcFd3Z3dRaGI5MzBreHJ4U216bEJqKnI5WHhobzdxcHpTQkVCTHMqMmNwZXEyTW9LeTJ4MUhQOC1BUU9kbTlNSjNhdFlQQUVIa0lFd2otcEZWdllweWNCbUZBY0JDYy15azU0RFI1dUYxR3IyV3o2T0hFcGVqbCp2NDVRY3daWHZTWlcyRzhLQzgyMi16SkZLVWZhdW0ya1pvbjgxWnJNMTFsMXEtaTktVmQxWFdreVBaeEwyVjhnTDdvOGhvdm15aVZrWnZOd05jM0dwUll2QV9fIgp9.fd5e91639240c438e105c3f34dbba75a4b2b2634b06e9f7ade343e137e003012
     * grade : 2
     * start_time : 1497873278
     * exp_time : 1499169278
     * ip : 192.168.43.85
     * username : 18888888888
     * user_id : 3
     * primary_key : -520186859
     * signature_server : http://192.168.43.67
     * TenCentToken : eJxNjV1PgzAYhf9LbzXaj-Exk10wwblsXIgEpjFpGlpqR2S11JXF*N9Ftqnv5fOcc95PkK8fr1hV7T5aS*1BC3ADILgcseKitapWwgwQhb930kxrxSmzlBj*r9Xxho7qpzSBEBLs*2cpeq2MoKy2x1HP8-AQOdm9MJ3atYPAEHkIEwj-pFVvYpycBmFAcBCc-yk54DR5uF1Gr2Wz6OHEpejl*v45QcwZXvSZW2G8KC822-zJFKUfaum2kZon81ZrM11l1q-i9-Vd1XWkyPZxL2V8gL7o8hovmyiVkZvNwNc3GpRYvA__
     */
    private String token_base64;
    private String grade;
    private String start_time;
    private String exp_time;
    private String ip;
    private String username;
    private String user_id;
    private String primary_key;
    private String signature_server;
    private String TenCentToken;

    public String getToken_base64() {
        return token_base64;
    }

    public void setToken_base64(String token_base64) {
        this.token_base64 = token_base64;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getExp_time() {
        return exp_time;
    }

    public void setExp_time(String exp_time) {
        this.exp_time = exp_time;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPrimary_key() {
        return primary_key;
    }

    public void setPrimary_key(String primary_key) {
        this.primary_key = primary_key;
    }

    public String getSignature_server() {
        return signature_server;
    }

    public void setSignature_server(String signature_server) {
        this.signature_server = signature_server;
    }

    public String getTenCentToken() {
        return TenCentToken;
    }

    public void setTenCentToken(String TenCentToken) {
        this.TenCentToken = TenCentToken;
    }
}
