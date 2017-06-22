package com.example.administrator.lifehelp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.administrator.lifehelp.application.MyApplication;
import com.example.administrator.lifehelp.db.UserInfo;
import com.example.administrator.lifehelp.gson.JudgeVerJson;
import com.example.administrator.lifehelp.util.HttpRequest;
import com.example.administrator.lifehelp.util.ToastUtil;
import com.example.administrator.lifehelp.util.Utils;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 这里是用户输入手机号的界面
 */
public class UserPhone extends AppCompatActivity {

    private final static String TAG = "jsone";
    private final static int SHOWINTERNET = 1;

    public boolean t = true;
    //手机号码格式
    private String telRegex = "[1][34578][0-9][ ]\\d{4}[ ]\\d{4}";
    //下一步按钮
    private Button user_btn_next;
    //输入区域
    private EditText user_edit_phone;
    //判断长度
    private CharSequence textLength;
    //跳过按钮
    private Button user_btn_skip;
    //判断当前状态
    public boolean isOpen;
    public Toast toast;
    //判断在那个页面
    public boolean isEdit = true;
    public RelativeLayout user_phone_back;
    //拥挤手机号
    public String userPhone;

    public HandlerInfo handlerInfo = new HandlerInfo();
    TextChange textChange = new TextChange();

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        List<UserInfo> userInfo = DataSupport.findAll(UserInfo.class);
        for (UserInfo user : userInfo){
            if (user.getUsername() != null){
                Log.i(TAG, "onCreate: " + user.getToken_base64());
                Log.i(TAG, "onCreate: " + user.getUsername());
                Log.i(TAG, "onCreate: " + user.getUser_id());
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        setContentView(R.layout.user_phone_first);
        //初始化控件
        initControl();
        //用户手机格式
        userPhoneFormat();
        //截取英文开头到.的字符串
        //String str = string.substring(string.indexOf("\\W") + 1,string.indexOf("."));
    }

    /**
     * 对输入的手机号格式进行判断
     */
    public void userPhoneFormat() {
        //user_edit_phone.addTextChangedListener(textChange);
        user_edit_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
//          这里是手机代码格式例如 888 8888 8888
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int i2) {
                textLength = s;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < s.length(); i++) {
                    sb.append(s.charAt(i));
                    if ((sb.length() == 4 || sb.length() == 9)
                            && sb.charAt(sb.length() - 1) != ' ') {
                        sb.insert(sb.length() - 1, ' ');
                        String temp = s.toString();
                        user_edit_phone.setText(sb.toString());
                        user_edit_phone.setSelection(temp.length() +1);
                    }
                }
            }

            /**
             *这里是进行对输入的判断
             */
            @Override
            public void afterTextChanged(Editable s) {

                String text = textLength.toString() + ' ';
                int phone_length = text.length();
                if (phone_length == 14) {
                    /**
                     * 这里让输入完后失去输入焦点
                     */
                    user_edit_phone.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            user_edit_phone.setFocusable(true);
                            user_edit_phone.setFocusableInTouchMode(true);
                            return false;
                        }
                    });
                    user_edit_phone.clearFocus();
                    user_edit_phone.setFocusable(false);
                    //让键盘消失
                    InputMethodManager imm = (InputMethodManager) MyApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(user_edit_phone.getWindowToken(),0);
                    //改变按钮为高亮
                    user_btn_next.setTextColor(Color.rgb(231, 188, 17));
                    /**
                     * 这里是进行跳转到下一个界面，并且将手机号发送过去
                     */
                    user_btn_next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!user_edit_phone.getText().toString().matches(telRegex)){
                                ToastUtil.showToast(MyApplication.getContext(),"你输入的格式错误",3000);
                            }else {
                                userPhone = user_edit_phone.getText().toString();
                                t = true;
                                serverRequest();
                                judgeActivity();
                            }
                        }
                    });
                }
            }
        });
    }

    private void judgeActivity() {
        HttpRequest.request(MyApplication.ServerUrl.TIANHUAN_TEST_URL + "verifyIP", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.obj = "你没有网络";
                message.what = SHOWINTERNET;
                handlerInfo.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.i(TAG, "verifyIP " + res);
                judgeStartActivity(res);
            }
        });
    }

    //解析数据并且判断用户是否频繁操作
    private void judgeStartActivity(String judgeOne) {
        Intent intent = new Intent(UserPhone.this,UserVerification.class);
        Intent picIntent = new Intent(UserPhone.this,UserPicVerification.class);
        Gson gson = new Gson();
        JudgeVerJson judgeVerJson = gson.fromJson(judgeOne,JudgeVerJson.class);
        int judgeCode = judgeVerJson.getCode();
        Log.i(TAG, "judgeJsone " + judgeVerJson.getMessage() +"||"+ judgeCode);
        intent.putExtra("user_phone",userPhone);
        intent.putExtra("userPhoneNumber", Utils.getPhoneNumber(userPhone));
        picIntent.putExtra("user_phone",userPhone);
        picIntent.putExtra("userPhoneNumber", Utils.getPhoneNumber(userPhone));
        if (judgeCode == 1001 || judgeCode == 1002 ){
            Log.i(TAG, "judgeStartActivity: " + "test");
            if (t){
                startActivity(intent);
                finish();
            }else {
                UiThread("你的手机号已上限，请明日再试");
            }
        }else if (judgeCode == 1013 || judgeCode == 1011){
            Log.i(TAG, "手机请求正常" + judgeCode);
        }else if (judgeCode == 1000){
            if (t){
                startActivity(picIntent);
                finish();
            }else {
                UiThread("你的手机号已上限，请明日再试");
            }
        }else if (judgeCode == 1014){
            t = false;
            UiThread("你的手机号已上限，请明日再试");
        }else if (judgeCode == 1012 || judgeCode == 1003){
            UiThread("天欢的服务器炸了");
        }else {
            UiThread("请求登陆验证太频繁，请稍候再试");
        }
    }

    /**
     * 手机号输入完成之后，，
     * 在这里请求服务器并获取返回的数据
     */
    public void serverRequest() {
        String url = MyApplication.ServerUrl.TIANHUAN_TEST_URL + "requestMax/" + Utils.getPhoneNumber(userPhone);
        Log.i(TAG, "serverRequest: " + url);
        HttpRequest.request(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.obj = "你没有网络";
                message.what = SHOWINTERNET;
                handlerInfo.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String serverReponse = response.body().string();
                Log.i(TAG, "requestMax " + serverReponse);
                judgeStartActivity(serverReponse);
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initControl() {

        user_phone_back = (RelativeLayout) findViewById(R.id.user_phone_back);
        //跳过按钮
        user_btn_skip = (Button) findViewById(R.id.user_btn_skip);
        user_btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //这是里跳过按钮将要跳转的界面
                Intent intent = new Intent(UserPhone.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //下一步按钮
        user_btn_next = (Button) findViewById(R.id.user_btn_next);
        //这里是输入区域
        user_edit_phone = (EditText) findViewById(R.id.user_edit_phone);
        toast = Toast.makeText(UserPhone.this,"再按一次退出",Toast.LENGTH_LONG);

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
    public class HandlerInfo extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SHOWINTERNET : ToastUtil.showToast(UserPhone.this,(String) msg.obj,3000);
            }
        }
    }
    //回到主线程，并提示一个toast
    public void UiThread(final String toastString){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showToast(UserPhone.this,toastString,3000);
            }
        });
    }
    private class TextChange implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
           // PhoneNumberUtils.formatNumber()
            textLength = s;
            String contents = s.toString();
            int length = contents.length();
            if(length == 4){
                if(contents.substring(3).equals(new String(" "))){ // -
                    contents = contents.substring(0, 3);
                    user_edit_phone.setText(contents);
                    user_edit_phone.setSelection(contents.length());
                }else{ // +
                    contents = contents.substring(0, 3) + " " + contents.substring(3);
                    user_edit_phone.setText(contents);
                    user_edit_phone.setSelection(contents.length());
                }
            }
            else if(length == 9){
                if(contents.substring(8).equals(new String(" "))){ // -
                    contents = contents.substring(0, 8);
                    user_edit_phone.setText(contents);
                    user_edit_phone.setSelection(contents.length());
                }else{// +
                    contents = contents.substring(0, 8) + " " + contents.substring(8);
                    user_edit_phone.setText(contents);
                    user_edit_phone.setSelection(contents.length());
                }
            }
        }
        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

}