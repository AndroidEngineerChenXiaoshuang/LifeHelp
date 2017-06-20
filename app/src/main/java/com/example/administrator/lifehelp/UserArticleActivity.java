package com.example.administrator.lifehelp;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.lifehelp.db.UserInfo;
import com.example.administrator.lifehelp.util.PopupWindowUtil;

import org.litepal.crud.DataSupport;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Create by Jam 2017/6/12
 */

public class UserArticleActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView circleImageView ;
    private TextView userName ;
    private TextView startTimer ;
    private TextView articleInfo ;
    private TextView endTimer ;
    private TextView userMoney;
    private ImageButton imageBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_article);
        initInfo();
    }

    private void initInfo(){
        circleImageView = (CircleImageView) findViewById(R.id.user_img);
        userName = (TextView) findViewById(R.id.user_name);
        startTimer = (TextView) findViewById(R.id.start_timer);
        articleInfo = (TextView) findViewById(R.id.articleInfo);
        endTimer = (TextView) findViewById(R.id.end_time);
        userMoney = (TextView) findViewById(R.id.user_money);
        imageBack = (ImageButton) findViewById(R.id.title_return);
        imageBack.setOnClickListener(this);
        TextView titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("求助详情");
        Intent intent = getIntent();
        if(intent!=null){
            Glide.with(this)
                    .load(intent.getStringExtra("authorAvatar"))
                    .placeholder(R.drawable.ic_user)
                    .into(circleImageView);
            userName.setText(intent.getStringExtra("userName"));
            articleInfo.setText(intent.getStringExtra("articleContent"));
            userMoney.setText(intent.getStringExtra("article_reward"));
            setTimerfuncation(getIntent());
        }else{
            throw new NullPointerException("Intent is not null");
        }
    }

    private void setTimerfuncation(Intent intent){
        if(startTimer!=null&&endTimer!=null){
            Long startTime = (Long.parseLong(intent.getStringExtra("createTime"))*1000);//获取到开始创建的时间
            Long endTime = (Long.parseLong(intent.getStringExtra("articleValid"))*1000);//获取到截止的时间
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            endTimer.setText(simpleDateFormat.format(new Date(endTime))+" 前");//将截止的时间设置进去
            startTimer.setText(getStartForEndTimerString(startTime)+"前");
        }
    }

    private String getStartForEndTimerString(Long startTime){
        if(startTime>0){
            long nowTime = System.currentTimeMillis();
            long computerTime = nowTime-startTime;
            int secound = (int) (computerTime/1000);//换算成秒
            if(secound<60){
                return secound+"秒";
            }
            int minute = secound/60;
            if(minute<60){
                return minute+"分钟";
            }
            int hour = minute/60;
            if(hour<24){
                return hour+"小时";
            }
            int day = hour/24;
            if(day<32){
                return day+"天";
            }
            int month = day/31;
            if(month<12){
                return month+"月";
            }
            int year = month/12;
            return year+"年";
        }
        return null;
    }

    public void startWork(View view) {
        if(view.getId() == R.id.startWork_btn){
            Cursor cursor = DataSupport.findBySQL("select token,TlssToken from userinfo;");
            if(cursor.moveToNext()){
                String token = cursor.getString(cursor.getColumnIndex("token"));
                String tlsToken = cursor.getString(cursor.getColumnIndex("tlsstoken"));
                cursor.close();
            }else{
                PopupWindowUtil popupWindowUtil = new PopupWindowUtil(this);
                popupWindowUtil.show(1);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_return:
                finish();
                break;
        }
    }
}
