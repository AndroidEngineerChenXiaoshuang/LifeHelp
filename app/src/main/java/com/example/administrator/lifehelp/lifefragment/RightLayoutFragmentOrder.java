package com.example.administrator.lifehelp.lifefragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.lifehelp.MainActivity;
import com.example.administrator.lifehelp.R;
import com.example.administrator.lifehelp.adapter.ListBaseAdapter;
import com.example.administrator.lifehelp.application.MyApplication;
import com.example.administrator.lifehelp.util.AnimationUtil;
import com.example.administrator.lifehelp.util.Utils;

import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 该类继承的是support v4包中,为了兼容低版本的android版本,该类用于加载主界面的第二个滑动界面
 * 主要操作筛选类
 */

public class RightLayoutFragmentOrder extends Fragment implements View.OnClickListener,AdapterView.OnItemClickListener{

    public View isShow;
    public TextView className;
    public ListView listView;

    //获得上一层布局的阴影back
    public void setIsShow(View isShow,TextView className){
        this.isShow = isShow;
        this.className = className;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.right_layout_two,container,false);
        //注册点击事件
        if(Build.VERSION.SDK_INT>=21){
            //添加paddingTop
            root.setPadding(0, Utils.getStatusHeight(),0,0);
        }
        root.findViewById(R.id.back_layout).setOnClickListener(this);
        root.findViewById(R.id.right_layout_two_back).setOnClickListener(this);
        listView = (ListView) root.findViewById(R.id.list_help_class);
        ListBaseAdapter listBaseAdapter = new ListBaseAdapter(MyApplication.Type.MAINACTIVITY_RIGHT_LIST);
        listView.setAdapter(listBaseAdapter);
        listView.setOnItemClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_layout:

                break;
            case R.id.right_layout_two_back:
                onBackFragment();
                break;
        }
    }

    //让当前所在的碎片返回到上一层的碎片
    public void onBackFragment(){
        getFragmentManager().popBackStack();
        if(isShow!=null){
            AlphaAnimation alphaAnimation = new AlphaAnimation(1,0);
            AnimationUtil.setAccelerateInterpolator(isShow,alphaAnimation,MyApplication.Type.ALPHANIMATION,200);
            isShow.setVisibility(View.GONE);
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setOpenDrawerOfRight(false,isShow);
        }
    }

    public boolean isItemClick = false;

    //listView每一项item的点击事件回调方法
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!isItemClick){
            isItemClick = true;
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MyApplication.Type.SHARED_USER_LIKE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(MyApplication.Type.SHARED_USER_HELPCLASS_SELECTOR,position);
            editor.apply();

            ListBaseAdapter listBaseAdapter = new ListBaseAdapter(MyApplication.Type.SETHOUR);
            listView.setAdapter(listBaseAdapter);

            //设置上一层布局的className
            className.setText(MyApplication.Info.HELP_CLASS_NAMES[sharedPreferences.getInt(MyApplication.Type.SHARED_USER_HELPCLASS_SELECTOR,0)]);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onBackFragment();
                        }
                    });
                }
            }).start();
        }
    }
}
