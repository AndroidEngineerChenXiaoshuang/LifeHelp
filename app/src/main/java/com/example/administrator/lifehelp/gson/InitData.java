package com.example.administrator.lifehelp.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 当app启动的时候初始化的json映射的数据
 */

public class InitData {
    @SerializedName("client_version")
    public String version;

    @SerializedName("need_update")
    public int is_update;

    public String apk_link;

}
