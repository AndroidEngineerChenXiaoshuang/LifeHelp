package com.example.administrator.lifehelp;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.administrator.lifehelp.application.MyApplication;
import com.example.administrator.lifehelp.db.UserHelpInfo;
import com.example.administrator.lifehelp.db.UserInfo;
import com.example.administrator.lifehelp.gson.LoginAndRegisterJson;
import com.example.administrator.lifehelp.util.HttpRequest;
import com.example.administrator.lifehelp.util.ToastUtil;
import com.example.administrator.lifehelp.util.Utils;
import com.google.gson.Gson;


import org.json.JSONArray;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 这里是让用户输入手机验证码的界面
 */
public class UserVerification extends AppCompatActivity implements View.OnClickListener{

    public final static String TAG = "jsone";

    public boolean isOpen;
    //这是显示倒计时的view
    public TextView txtView;
    //这是倒计时重发的按钮
    public Button user_end_time_button;
    //这里是4个输入框
    public View view1;
    public View view2;
    public View view3;
    public View view4;
    //  这是输入键盘
    public View include;
    public TextView user_phone_number;
    //  这是显示输入的数字
    public TextView view_text_1;
    public TextView view_text_2;
    public TextView view_text_3;
    public TextView view_text_4;

    public ImageButton user_btn_return;

    public Button keyBoard1;
    public Button keyBoard2;
    public Button keyBoard3;
    public Button keyBoard4;
    public Button keyBoard5;
    public Button keyBoard6;
    public Button keyBoard7;
    public Button keyBoard8;
    public Button keyBoard9;
    public Button keyBoard0;
    public ImageButton keyBoard_delete;
    //判断用户输入次数
    public int user_btn_click = 0;
    //这里是获取intent传输的手机号
    public String user_phone ;
    //这是用户输入的验证码
    public String user_input;
    //这是服务器传来的验证码
    public String serverVerification = "1234";
    //标准11位手机号
    public String userPhoneNumber;
    //临时token
    public String mTemporaryToken;

    public Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_phone_interface);

        Intent intent = getIntent();
        user_phone = intent.getStringExtra("user_phone");
        userPhoneNumber = intent.getStringExtra("userPhoneNumber");
        mTemporaryToken = intent.getStringExtra("temporaryToken");
        Log.i(TAG, "userPhoneNumber: " + userPhoneNumber + user_phone + mTemporaryToken);
        initControl();
    }

    /**
     * 初始化控件
     */
    private void initControl() {
        //这是用户号显示
        user_phone_number = (TextView) findViewById(R.id.user_phone_number);
        user_phone_number.setText(user_phone);
        //这是倒计时
        txtView = (TextView) findViewById(R.id.user_end_time);
        //进行倒计时
        timers.start();
        include = findViewById(R.id.keyboard);
        //这是输入的内容
        view_text_1 = (TextView) findViewById(R.id.view_text_1);
        view_text_2 = (TextView) findViewById(R.id.view_text_2);
        view_text_3 = (TextView) findViewById(R.id.view_text_3);
        view_text_4 = (TextView) findViewById(R.id.view_text_4);

        //这是输入的密码显示黑点
        view1 = findViewById(R.id.fore);
        view2 = findViewById(R.id.hour);
        view3 = findViewById(R.id.second);
        view4 = findViewById(R.id.minute);
        view1.setOnClickListener(this);
        view2.setOnClickListener(this);
        view3.setOnClickListener(this);
        view4.setOnClickListener(this);
        //这是键盘
        keyBoard0 = (Button) findViewById(R.id.keyboard0);
        keyBoard1 = (Button) findViewById(R.id.keyboard1);
        keyBoard2 = (Button) findViewById(R.id.keyboard2);
        keyBoard3 = (Button) findViewById(R.id.keyboard3);
        keyBoard4 = (Button) findViewById(R.id.keyboard4);
        keyBoard5 = (Button) findViewById(R.id.keyboard5);
        keyBoard6 = (Button) findViewById(R.id.keyboard6);
        keyBoard7 = (Button) findViewById(R.id.keyboard7);
        keyBoard8 = (Button) findViewById(R.id.keyboard8);
        keyBoard9 = (Button) findViewById(R.id.keyboard9);
        //删除键
        keyBoard_delete = (ImageButton) findViewById(R.id.keyboard_delete);
        keyBoard_delete.setOnClickListener(this);
        user_btn_return = (ImageButton) findViewById(R.id.user_btn_return);
        user_btn_return.setOnClickListener(this);
        keyBoard0.setOnClickListener(this);
        keyBoard1.setOnClickListener(this);
        keyBoard2.setOnClickListener(this);
        keyBoard3.setOnClickListener(this);
        keyBoard4.setOnClickListener(this);
        keyBoard5.setOnClickListener(this);
        keyBoard6.setOnClickListener(this);
        keyBoard7.setOnClickListener(this);
        keyBoard8.setOnClickListener(this);
        keyBoard9.setOnClickListener(this);
        user_end_time_button = (Button) findViewById(R.id.user_end_time_button);
        //初始化一个toast
        toast = Toast.makeText(MyApplication.getContext(),"再按一次退出",Toast.LENGTH_LONG);

    }

    /**
     * 这是发送验证码的倒计时
     * @return
     */

    private CountDownTimer timers = new CountDownTimer(60000,1000) {
        @Override
        public void onTick(long l) {
            txtView.setText((l/1000) + "后重新发送");
        }

        @Override
        public void onFinish() {
            txtView.setVisibility(View.GONE);
            user_end_time_button.setVisibility(View.VISIBLE);
            user_end_time_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loginAndRegisterRequest();
                    user_end_time_button.setVisibility(View.GONE);
                    txtView.setVisibility(View.VISIBLE);
                    timers.start();
                }
            });
        }
    };

    /**
     * 这是所有的点击事件
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //每个点击都会打开键盘
            case R.id.fore:
                include.setVisibility(View.VISIBLE);
                break;
            case R.id.hour:
                include.setVisibility(View.VISIBLE);
                break;
            case R.id.second:
                include.setVisibility(View.VISIBLE);
                break;
            case R.id.minute:
                include.setVisibility(View.VISIBLE);
                break;
            case R.id.user_btn_return:
                include.setVisibility(View.GONE);
                break;
            //这是键盘输入
            case R.id.keyboard1:
                getTextViewContext(view_text_1,view_text_2,view_text_3,view_text_4,"1");
                break;
            case R.id.keyboard2:
                getTextViewContext(view_text_1,view_text_2,view_text_3,view_text_4,"2");
                break;
            case R.id.keyboard3:
                getTextViewContext(view_text_1,view_text_2,view_text_3,view_text_4,"3");
                break;
            case R.id.keyboard4:
                getTextViewContext(view_text_1,view_text_2,view_text_3,view_text_4,"4");
                break;
            case R.id.keyboard5:
                getTextViewContext(view_text_1,view_text_2,view_text_3,view_text_4,"5");
                break;
            case R.id.keyboard6:
                getTextViewContext(view_text_1,view_text_2,view_text_3,view_text_4,"6");
                break;
            case R.id.keyboard7:
                getTextViewContext(view_text_1,view_text_2,view_text_3,view_text_4,"7");
                break;
            case R.id.keyboard8:
                getTextViewContext(view_text_1,view_text_2,view_text_3,view_text_4,"8");
                break;
            case R.id.keyboard9:
                getTextViewContext(view_text_1,view_text_2,view_text_3,view_text_4,"9");
                break;
            case R.id.keyboard0:
                getTextViewContext(view_text_1,view_text_2,view_text_3,view_text_4,"0");
                break;
//            这是删除键
            case R.id.keyboard_delete:
                if (user_btn_click == 1){
                    view_text_1.setText(" ");
                    user_btn_click = 0;
                }else if (user_btn_click == 2){
                    view_text_2.setText(" ");
                    user_btn_click = 1;
                }else if (user_btn_click == 3){
                    view_text_3.setText(" ");
                    user_btn_click = 2;
                }else if (user_btn_click == 4){
                    user_btn_click = 3;
                    view_text_4.setText(" ");
                }
                break;
        }
        //这里进行判断如果用户输入的验证码正确就注册成功并进入主界面
        if (user_btn_click == 4 ){
            loginAndRegisterRequest();
        }
     }
    //登陆和注册请求
    public void loginAndRegisterRequest() {
        Log.i(TAG, "httpUrl: " + MyApplication.ServerUrl.LIFEHELP_SERVER_URL +
                "v1/UserAction/verifySMS/" + userPhoneNumber +"/"+ user_input +"/"+ mTemporaryToken);
        HttpRequest.request(MyApplication.ServerUrl.LIFEHELP_SERVER_URL +
                "v1/UserAction/verifySMS/" + userPhoneNumber +"/"+ user_input +"/"+ mTemporaryToken, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.i(TAG, "onResponse: " + res);
                Gson gson = new Gson();
                LoginAndRegisterJson loginAndRegisterJson = gson.fromJson(res,LoginAndRegisterJson.class);
                if (loginAndRegisterJson.getCode() == 5854 || loginAndRegisterJson.getCode() == 5754){
                    UserInfo userInfo = new UserInfo();
                    userInfo.setStatus(loginAndRegisterJson.getStatus());
                    userInfo.setMessage(loginAndRegisterJson.getMessage());
                    userInfo.setCode(loginAndRegisterJson.getCode());
                    userInfo.setToken(loginAndRegisterJson.getToken());
                    userInfo.setTlssToken(loginAndRegisterJson.getTlssToken());
                    userInfo.setNickname(loginAndRegisterJson.getUserinfo().getNickname());
                    userInfo.setAvatar(loginAndRegisterJson.getUserinfo().getAvatar());
                    userInfo.setBalance(loginAndRegisterJson.getUserinfo().getBalance());
                    userInfo.setCredit_grade(loginAndRegisterJson.getUserinfo().getCredit_grade());
                    userInfo.save();
                    //让键盘消失。
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            include.setVisibility(View.GONE);
                            ToastUtil.showToast(MyApplication.getContext(),"验证成功，你以成功注册",3000);
                            Intent intent = new Intent(UserVerification.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }else if (loginAndRegisterJson.getCode() == 5756){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view_text_1.setText(" ");
                            view_text_2.setText(" ");
                            view_text_3.setText(" ");
                            view_text_4.setText(" ");
                            user_btn_click = 0;
                            ToastUtil.showToast(MyApplication.getContext(),"您输入错误，请重新输入",3000);
                        }
                    });
                }
            }
        });
    }

    //判断当前显示为第几个textView并且获取textView显示的内容
    public void getTextViewContext(TextView view_text_1, TextView view_text_2, TextView view_text_3, TextView view_text_4, String s) {
        if (user_btn_click == 0){
            view_text_1.setText(s);
            user_input =view_text_1.getText().toString();
            user_btn_click++;
        }else if (user_btn_click == 1){
            view_text_2.setText(s);
            user_input = user_input + view_text_2.getText().toString();
            user_btn_click++;
        }else if (user_btn_click == 2){
            view_text_3.setText(s);
            user_input = user_input + view_text_3.getText().toString();
            user_btn_click++;
        }else if (user_btn_click == 3){
            view_text_4.setText(s);
            user_input = user_input + view_text_4.getText().toString();
            user_btn_click++;
        }
        Log.i(TAG, "getTextViewContext: " + user_input);
    }

    @Override
    public void onBackPressed() {
        //证明用户第一次点击处于栈顶,并且第一次或者状态消灭时点击
        if(!isOpen){
            isOpen = true;
            Utils.setToastDuration(3000,toast,this);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    isOpen = false;
                }
            },3500);
        }else{
            toast.cancel();
            finish();
        }

    }
}
