package com.example.administrator.lifehelp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.administrator.lifehelp.application.MyApplication;
import com.example.administrator.lifehelp.db.UserInfo;
import com.example.administrator.lifehelp.gson.InitArticleInfo;
import com.example.administrator.lifehelp.util.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Create by Jam 2017/6/12
 */

public class UserArticleActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private CircleImageView circleImageView ;
    private TextView userName ;
    private TextView startTimer ;
    private TextView articleInfo ;
    private TextView endTimer ;
    private TextView userMoney;
    private TextView userSpeak;
    private ImageButton imageBack;
    private SwipeRefreshLayout refreshInfo;
    //请求文章详细信息url地址
    private String url = "http://192.168.43.67/v1/articleoperation/getcontent/";
    private InitArticleInfo initArticleInfo;
    private Intent intent;

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
        userSpeak = (TextView) findViewById(R.id.user_speak);
        refreshInfo = (SwipeRefreshLayout) findViewById(R.id.refreshArticle);
        imageBack = (ImageButton) findViewById(R.id.title_return);
        imageBack.setOnClickListener(this);
        TextView titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("求助详情");
        intent = getIntent();
        setTimerfuncation(intent);
        url += intent.getStringExtra("articleId");
        refreshInfo.setColorSchemeColors(MyApplication.Color.PRIMARYCOLOR);
        refreshInfo.setRefreshing(true);
        refreshInfo.setOnRefreshListener(this);
        requestArticleInfo();
    }

    private void requestArticleInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpRequest.request(url, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Jam", String.valueOf(Thread.currentThread().getId()));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("Jam", String.valueOf(Thread.currentThread().getId()));
                                Toast.makeText(UserArticleActivity.this,"请求服务器失败",Toast.LENGTH_SHORT).show();
                                refreshInfo.setRefreshing(false);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();   //异步获取到服务器返回的数据
                        Gson gson = new Gson();
                        initArticleInfo = gson.fromJson(result,InitArticleInfo.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                userName.setText(initArticleInfo.articleAuthor);
                                articleInfo.setText(initArticleInfo.articleContent);
                                userMoney.setText(initArticleInfo.articleReward);
                                refreshInfo.setRefreshing(false);
                                userSpeak.setText(initArticleInfo.articleSignature);
                                if(initArticleInfo.articleAvatar.lastIndexOf("h")!=-1){
                                    Glide.with(UserArticleActivity.this).load(initArticleInfo.articleAvatar).into(circleImageView);
                                }else{
                                    circleImageView.setBackgroundResource(R.drawable.ic_user);
                                }
                            }
                        });
                    }
                });
            }
        }).start();
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
            List<UserInfo> userInfos = DataSupport.select("username","token_base64","user_id").find(UserInfo.class);
            //获取当前登录的账号
            String userAccount = userInfos.get(0).getUsername();
            String userId = userInfos.get(0).getUser_id();
            String token = userInfos.get(0).getToken_base64();
            if(userAccount!=null&&userAccount.length()>0) {
                String requestUrl = "http://192.168.43.67/v1/useraction/getuserproperty/";
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(requestUrl).append(userId+"/").append(token.replace("\"",""));
                Log.d("Jam",stringBuilder.toString());
                HttpRequest.request(stringBuilder.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserArticleActivity.this,"请求服务器失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String business = jsonObject.getString("business");
                            if(business.equals("1")){
                                //如果是接单员的话就开始我们的接单处理业务逻辑

                            }else{

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }else{
                //用户没有登录状态下的结果
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

    @Override
    public void onRefresh() {
        Log.d("Jam","what");
        requestArticleInfo();
    }

}
