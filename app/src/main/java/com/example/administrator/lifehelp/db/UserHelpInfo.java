package com.example.administrator.lifehelp.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;
import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/1 0001.
 */

public class UserHelpInfo extends DataSupport implements Serializable{
    public String user_title;
    public String user_money;
    public String user_info;
    public String user_img;
    public String user_timer;
    public int id;

    public void setUser_title(String user_title){
        this.user_title = user_title;
    }

    public String getUser_title() {
        return user_title;
    }

    public void setUser_money(String user_money) {
        this.user_money = user_money;
    }

    public String getUser_money() {
        return user_money;
    }

    public void setUser_info(String user_info) {
        this.user_info = user_info;
    }

    public String getUser_info() {
        return user_info;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_timer(String user_timer) {
        this.user_timer = user_timer;
    }

    public String getUser_timer() {
        return user_timer;
    }
}
