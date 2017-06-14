package com.example.administrator.lifehelp.gson;

import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by zzz on 2017/6/2
 */

public class ParseJson {



    public final static String TAG = "jsone";

    public static UserActionJson userActionJson;

    //解析出服务器返回的临时token
    public static String getOnlyPhoneToken(String onlyPhoneTokent) {
        Gson gson = new Gson();
        TokenJson token = gson.fromJson(onlyPhoneTokent,TokenJson.class);
        String tok = token.getToken();
        Log.i(TAG, "parseJSONByGSON: ." + token.getToken());
        return tok;
    }

}
