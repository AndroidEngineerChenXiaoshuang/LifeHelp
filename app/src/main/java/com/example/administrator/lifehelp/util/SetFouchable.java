package com.example.administrator.lifehelp.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.administrator.lifehelp.application.MyApplication;

/**
 * 关闭软件盘
 */

public class SetFouchable {
    public static void setFouch(View childView){
        InputMethodManager inputMethodManager = (InputMethodManager) MyApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(childView.getWindowToken(),0);
    }
}

