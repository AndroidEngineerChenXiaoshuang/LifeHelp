package com.example.administrator.lifehelp.util;

import android.util.Log;

import com.example.administrator.lifehelp.application.MyApplication;
import com.example.administrator.lifehelp.gson.TokenJson;
import com.example.administrator.lifehelp.gson.UserActionJson;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zzz on 2017/5/31
 */

public class UserLangUtil {



    private static String test;
   // public String test;
    private static String ponse;

    private final static String TAG = "jsone";

    public static String getTemporaryToken(String onlyPhoneId) {

        final String temToken = null;
        HttpRequest.request(MyApplication.ServerUrl.LIFEHELP_SERVER_URL + "v1/Signature/" + onlyPhoneId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "Internet: " + "没有网络");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                test = getParseServer(response.body().string());
                Log.i(TAG, "onlyPhoneToken: " + test);
            }
        });
        Log.i(TAG, "onlyPhoneToken test: " + test);
        return test;
    }

    private static String getParseServer(String onlyPhoneTokent) {
        Gson gson = new Gson();
        TokenJson token = gson.fromJson(onlyPhoneTokent,TokenJson.class);
        String tok = token.getToken();
        Log.i(TAG, "parseJSONByGSON: ." + token.getToken());
        return tok;
    }


    public static String serverRequest(String onlyPhoneToken,String userPhone,String onlyPhoneId) {
        if (onlyPhoneToken == null) {
            //getTemporaryToken();
            onlyPhoneToken = getTemporaryToken(onlyPhoneId);
        }

        Log.i(TAG,"serverUrl:Util " + MyApplication.ServerUrl.LIFEHELP_SERVER_URL +
                "v1/UserAction/inorup/" + userPhone + "/" + onlyPhoneToken);
        HttpRequest.request(MyApplication.ServerUrl.LIFEHELP_SERVER_URL +
                "v1/UserAction/inorup/" + userPhone + "/" + onlyPhoneToken, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "Internet: " + "没有网络");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ponse = response.body().string();
                //

            }
        });

        return ponse;
    }

    private static int parse(String ponse) {
        Gson gson = new Gson();
        UserActionJson userActionJson = gson.fromJson(ponse,UserActionJson.class);

        return userActionJson.getCode();
    }

}
