package com.example.administrator.lifehelp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.administrator.lifehelp.application.MyApplication;
import com.example.administrator.lifehelp.gson.TokenJson;
import com.example.administrator.lifehelp.gson.UserActionJson;
import com.example.administrator.lifehelp.util.HttpRequest;
import com.example.administrator.lifehelp.util.ToastUtil;
import com.example.administrator.lifehelp.util.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 这里是用户输入手机号的界面
 */
public class UserPhone extends AppCompatActivity{

    private final static String TAG = "jsone";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    //判断用户操作
    public int judgeCode;

    //手机唯一ID
    public String onlyPhoneId;
    //将json字符存在内存中
    public String onlyPhoneToken;

    //手机号码格式
    private String telRegex = "[1][34578][0-9][ ]\\d{4}[ ]\\d{4}";
    //下一步按钮
    private Button user_btn_next;
    //输入区域
    private EditText user_edit_phone;
    //判断长度
    private CharSequence textLength;
    private int phone_num;

    //跳过按钮
    private Button user_btn_skip;

    //判断当前状态
    private boolean isOpen;

    private Toast toast;
    //判断在那个页面
    public boolean isEdit = true;

    public RelativeLayout user_phone_back;
    //拥挤手机号
    public String userPhone;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_phone_first);
        preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit();
        //当第一次打开app时，会获取一个唯一ID
        firstUse();
        //初始化控件
        initControl();
        //用户手机格式
        userPhoneFormat();
    }

    /**
     * 第一次打开客户端
     */
    private void firstUse() {
        onlyPhoneId = Utils.getPhoneId();
        Log.i(TAG, "onlyPhoneId: " + onlyPhoneId);
        Log.i(TAG, "onlyPhoneToken: " + onlyPhoneToken);
        Log.i(TAG, "getTemporaryToken: " + onlyPhoneToken);
        getTemporaryToken();
    }

    private void getTemporaryToken() {

        HttpRequest.request(MyApplication.ServerUrl.LIFEHELP_SERVER_URL + "v1/Signature/" + onlyPhoneId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "Internet: " + "没有网络");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String temToken = response.body().string();
                //editor.putString("onlyPhoneToken",getParseServer(temToken));
                onlyPhoneToken = getParseServer(temToken);
                //editor.apply();
                Log.i(TAG, "onlyPhoneToken: " + onlyPhoneToken);
            }
        });

    }

    //解析出服务器返回的临时token
    public String getParseServer(String onlyPhoneTokent) {
        Gson gson = new Gson();
        TokenJson token = gson.fromJson(onlyPhoneTokent,TokenJson.class);
        String tok = token.getToken();
        Log.i(TAG, "parseJSONByGSON: ." + token.getToken());
        return tok;
    }

    /**
     * 对输入的手机号格式进行判断
     */
    private void userPhoneFormat() {
        user_edit_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            /**
             *这里是手机代码格式例如 888 8888 8888
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int i2) {
                textLength = s;
                if (s == null || s.length() == 0){
                    phone_num = s.length();
                    if (phone_num == 11){
                        Toast.makeText(UserPhone.this,"phone",Toast.LENGTH_LONG).show();
                    }
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < s.length(); i++) {
                    if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                        continue;
                    } else {
                        sb.append(s.charAt(i));
                        if ((sb.length() == 4 || sb.length() == 9)
                                && sb.charAt(sb.length() - 1) != ' ') {
                            sb.insert(sb.length() - 1, ' ');
                        }
                    }
                }
                if (!sb.toString().equals(s.toString())) {
                    int index = start + 1;
                    if (sb.charAt(start) == ' ') {
                        if (before == 0) {
                            index++;
                        } else {
                            index--;
                        }
                    } else {
                        if (before == 1) {
                            index--;
                        }
                    }
                    user_edit_phone.setText(sb.toString());
                    user_edit_phone.setSelection(index);
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
                    InputMethodManager imm = (InputMethodManager)MyApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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
                                serverRequest();
                            }
                        }
                    });
                }
            }
        });
    }

    //解析数据并且判断用户是否频繁操作
    private void judgeStartActivity(String reponse) {
        Log.i(TAG, "judgeStartActivity: String reponse" + reponse);
        Gson gson = new Gson();
        UserActionJson userActionJson = gson.fromJson(reponse,UserActionJson.class);
        judgeCode = userActionJson.getCode();

        Intent intent = new Intent(UserPhone.this,UserVerification.class);
        Intent PicIntent = new Intent(UserPhone.this,UserPicVerification.class);

        intent.putExtra("user_phone",userPhone);
        intent.putExtra("userPhoneNumber",getPhoneNumber(userPhone));
        intent.putExtra("temporaryToken",onlyPhoneToken);
        intent.putExtra("VerifyImgBase64",userActionJson.getVerifyImg());
        //服务器维护中，
        if (judgeCode == 1021 ){
            startActivity(intent);
            finish();
        }else if (judgeCode == 1588){
            startActivity(PicIntent);
            //finish();
        }else if (judgeCode == 1024){
            //startActivity(PicIntent);
        }else if (judgeCode == 1588){

        }
        Log.i(TAG, "parmes tianhuan: " + userActionJson.getMessage());
        Log.i(TAG, "parmes tianhuan: " + userActionJson.getStatus());
        Log.i(TAG, "parmes tianhuan: " + userActionJson.getCode());
        Log.i(TAG, "parmes tianhuan: " + userActionJson.getTime());
        Log.i(TAG, "parmes tianhuan: " + userActionJson.getVerifyImg());
    }

    //将字符串转换为手机号
    private String getPhoneNumber(String userPhone) {
        String userPhoneNumber;
        String regEx="[^0-9]";
        Pattern p   =   Pattern.compile(regEx);
        Matcher m   =   p.matcher(userPhone);
        userPhoneNumber = m.replaceAll("").trim();
        return userPhoneNumber;
    }

    /**
     * 手机号输入完成之后，，
     * 在这里请求服务器并获取返回的数据
     */
    private void serverRequest() {
        if (onlyPhoneToken == null) {
            getTemporaryToken();
        }

        Log.i(TAG,"serverUrl: " + MyApplication.ServerUrl.LIFEHELP_SERVER_URL +
                "v1/UserAction/inorup/" + getPhoneNumber(userPhone) + "/" + onlyPhoneToken);
        HttpRequest.request(MyApplication.ServerUrl.LIFEHELP_SERVER_URL +
                "v1/UserAction/inorup/" + getPhoneNumber(userPhone) + "/" + onlyPhoneToken, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "Internet: " + "没有网络");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String serverReponse = response.body().string();
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
}