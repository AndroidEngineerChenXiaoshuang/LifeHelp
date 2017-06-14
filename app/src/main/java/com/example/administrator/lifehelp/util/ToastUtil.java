package com.example.administrator.lifehelp.util;

import android.content.Context;
import android.widget.Toast;


/**
 * Created by zzz on 2017/5/22.
 * 避免toast被多次触发造成的重复显示。
 */
public class ToastUtil {

    //用于记录每次toast的内容
    private static String oldMsg;
    //判断时间
    private static long time;

    public static void showToast(Context context, String msg, int duration){
        //判断是否为一样内容的toast，如果是就进行显示
        if (!msg.equals(oldMsg)) {
            Toast.makeText(context,msg,duration).show();
            time = System.currentTimeMillis();//获取时间
        } else if (System.currentTimeMillis() - time > duration){ //如果内容一样只会在toast显示完之后再次显示
            Toast.makeText(context,msg,duration).show();
            time = System.currentTimeMillis();
        }
        oldMsg = msg;
    }
}