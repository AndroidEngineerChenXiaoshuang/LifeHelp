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
import com.example.administrator.lifehelp.util.HttpRequest;
import com.example.administrator.lifehelp.util.ToastUtil;
import com.example.administrator.lifehelp.util.Utils;


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
    
    private final static String TAG = "jsone";

    private boolean isOpen;
    //这是显示倒计时的view
    private TextView txtView;
    //这是倒计时重发的按钮
    private Button user_end_time_button;
    //这里是4个输入框
    private View view1;
    private View view2;
    private View view3;
    private View view4;


    //  这是输入键盘

    private View include;
    private TextView user_phone_number;


    //  这是显示输入的数字

    private TextView view_text_1;
    private TextView view_text_2;
    private TextView view_text_3;
    private TextView view_text_4;

    private ImageButton user_btn_return;

    private Button keyBoard1;
    private Button keyBoard2;
    private Button keyBoard3;
    private Button keyBoard4;
    private Button keyBoard5;
    private Button keyBoard6;
    private Button keyBoard7;
    private Button keyBoard8;
    private Button keyBoard9;
    private Button keyBoard0;
    private ImageButton keyBoard_delete;

    //判断用户输入次数
    private int user_btn_click = 0;
    //这里是获取intent传输的手机号
    private String user_phone ;
    //这是用户输入的验证码
    private String user_input;
    //这是服务器传来的验证码
    private String serverVerification = "1234";
    //标准11位手机号
    private String userPhoneNumber;
    //临时token
    private String mTemporaryToken;

    private UserPhone userPhone = new UserPhone();

    private Toast toast;

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
       // UserPhone userPhone = new UserPhone();
       // userPhone.finish();
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
            case R.id.keyboard0:
                user_btn_click ++;
                if (user_btn_click == 1){
                    view_text_1.setText("0");
                    user_input =view_text_1.getText().toString();

                }else if (user_btn_click == 2){
                    view_text_2.setText("0");
                    user_input =user_input+view_text_2.getText().toString();

                }else if (user_btn_click == 3){
                    view_text_3.setText("0");
                    user_input =user_input+view_text_3.getText().toString();

                }else if (user_btn_click == 4){
                    view_text_4.setText("0");
                    user_input =user_input+view_text_4.getText().toString();


                }
                break;
            case R.id.keyboard1:
                user_btn_click ++;
                if (user_btn_click == 1){
                    view_text_1.setText("1");
                    user_input =view_text_1.getText().toString();
                }else if (user_btn_click == 2){
                    view_text_2.setText("1");
                    user_input =user_input+view_text_2.getText().toString();
                }else if (user_btn_click == 3){
                    view_text_3.setText("1");
                    user_input =user_input+view_text_3.getText().toString();
                }else if (user_btn_click == 4){
                    view_text_4.setText("1");
                    user_input =user_input+view_text_4.getText().toString();
                }
                break;
            case R.id.keyboard2:
                user_btn_click ++;
                if (user_btn_click == 1){
                    view_text_1.setText("2");
                    user_input =view_text_1.getText().toString();
                }else if (user_btn_click == 2){
                    view_text_2.setText("2");
                    user_input =user_input+view_text_2.getText().toString();
                }else if (user_btn_click == 3){
                    view_text_3.setText("2");
                    user_input =user_input+view_text_3.getText().toString();
                }else if (user_btn_click == 4){
                    view_text_4.setText("2");
                    user_input =user_input+view_text_4.getText().toString();
                }
                break;
            case R.id.keyboard3:
                user_btn_click ++;
                if (user_btn_click == 1){
                    view_text_1.setText("3");
                    user_input =view_text_1.getText().toString();
                }else if (user_btn_click == 2){
                    view_text_2.setText("3");
                    user_input =user_input+view_text_2.getText().toString();
                }else if (user_btn_click == 3){
                    view_text_3.setText("3");
                    user_input =user_input+view_text_3.getText().toString();
                }else if (user_btn_click == 4){
                    view_text_4.setText("3");
                    user_input =user_input+view_text_4.getText().toString();
                }
                break;
            case R.id.keyboard4:
                user_btn_click ++;
                if (user_btn_click == 1){
                    view_text_1.setText("4");
                    user_input =view_text_1.getText().toString();
                }else if (user_btn_click == 2){
                    view_text_2.setText("4");
                    user_input =user_input+view_text_2.getText().toString();
                }else if (user_btn_click == 3){
                    view_text_3.setText("4");
                    user_input =user_input+view_text_3.getText().toString();
                }else if (user_btn_click == 4){
                    view_text_4.setText("4");
                    user_input =user_input+view_text_4.getText().toString();
                }
                break;
            case R.id.keyboard5:
                user_btn_click ++;
                if (user_btn_click == 1){
                    view_text_1.setText("5");
                    user_input =view_text_1.getText().toString();
                }else if (user_btn_click == 2){
                    view_text_2.setText("5");
                    user_input =user_input+view_text_2.getText().toString();
                }else if (user_btn_click == 3){
                    view_text_3.setText("5");
                    user_input =user_input+view_text_3.getText().toString();
                }else if (user_btn_click == 4){
                    view_text_4.setText("5");
                    user_input =user_input+view_text_4.getText().toString();
                }
                break;
            case R.id.keyboard6:
                user_btn_click ++;
                if (user_btn_click == 1){
                    view_text_1.setText("6");
                    user_input =view_text_1.getText().toString();
                }else if (user_btn_click == 2){
                    view_text_2.setText("6");
                    user_input =user_input+view_text_2.getText().toString();
                }else if (user_btn_click == 3){
                    view_text_3.setText("6");
                    user_input =user_input+view_text_3.getText().toString();
                }else if (user_btn_click == 4){
                    view_text_4.setText("6");
                    user_input =user_input+view_text_4.getText().toString();
                }
                break;
            case R.id.keyboard7:
                user_btn_click ++;
                if (user_btn_click == 1){
                    view_text_1.setText("7");
                    user_input =view_text_1.getText().toString();
                }else if (user_btn_click == 2){
                    view_text_2.setText("7");
                    user_input =user_input+view_text_2.getText().toString();
                }else if (user_btn_click == 3){
                    view_text_3.setText("7");
                    user_input =user_input+view_text_3.getText().toString();
                }else if (user_btn_click == 4){
                    view_text_4.setText("7");
                    user_input =user_input+view_text_4.getText().toString();
                }
                break;
            case R.id.keyboard8:
                user_btn_click ++;
                if (user_btn_click == 1){
                    view_text_1.setText("8");
                    user_input =view_text_1.getText().toString();
                }else if (user_btn_click == 2){
                    view_text_2.setText("8");
                    user_input =user_input+view_text_2.getText().toString();
                }else if (user_btn_click == 3){
                    view_text_3.setText("8");
                    user_input =user_input+view_text_3.getText().toString();
                }else if (user_btn_click == 4){
                    view_text_4.setText("8");
                    user_input =user_input+view_text_4.getText().toString();
                }
                break;
            case R.id.keyboard9:
                user_btn_click ++;
                if (user_btn_click == 1){
                    view_text_1.setText("9");
                    user_input =view_text_1.getText().toString();
                }else if (user_btn_click == 2){
                    view_text_2.setText("9");
                    user_input =user_input+view_text_2.getText().toString();
                }else if (user_btn_click == 3){
                    view_text_3.setText("9");
                    user_input =user_input+view_text_3.getText().toString();
                }else if (user_btn_click == 4){
                    view_text_4.setText("9");
                    user_input =user_input+view_text_4.getText().toString();
                }
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
        if (user_btn_click == 4 && user_input.equals(serverVerification)){
            Log.i(TAG, "httpUrl: " + MyApplication.ServerUrl.LIFEHELP_SERVER_URL +
                    "v1/UserAction/verifySMS/" + userPhoneNumber +"/"+ serverVerification +"/"+ mTemporaryToken);
            HttpRequest.request(MyApplication.ServerUrl.LIFEHELP_SERVER_URL +
                    "v1/UserAction/verifySMS/" + userPhoneNumber +"/"+ serverVerification +"/"+ mTemporaryToken, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //String res = response.body().toString();
                    Log.i(TAG, "jsone Server: " + response.body().string());
                    //parseJSON(res);
                    finish();
                }
            });
            //让键盘消失。
            include.setVisibility(View.GONE);
            ToastUtil.showToast(MyApplication.getContext(),"验证成功，你以成功注册",3000);
            Intent intent = new Intent(UserVerification.this,MainActivity.class);
            startActivity(intent);
            finish();
        }else if (user_btn_click == 4 && !user_input.equals("1234") ){
            view_text_1.setText(" ");
            view_text_2.setText(" ");
            view_text_3.setText(" ");
            view_text_4.setText(" ");
            user_btn_click = 0;
            ToastUtil.showToast(MyApplication.getContext(),"您输入错误，请重新输入",3000);
        }
    }

    private void parseJSON(String res) {

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
