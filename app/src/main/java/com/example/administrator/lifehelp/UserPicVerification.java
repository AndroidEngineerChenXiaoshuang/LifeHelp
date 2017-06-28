package com.example.administrator.lifehelp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.administrator.lifehelp.application.MyApplication;
import com.example.administrator.lifehelp.gson.VerficationJson;
import com.example.administrator.lifehelp.util.HttpRequest;
import com.example.administrator.lifehelp.util.ToastUtil;
import com.example.administrator.lifehelp.util.Utils;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserPicVerification extends AppCompatActivity implements View.OnKeyListener ,View.OnClickListener{

    private final static String TAG = "jsone";

    //判断用户是否输入完成
    public int editNum;

    public String VerifyToken;

    public String userEditVer;
    //返回到上一个界面
    public ImageButton returnPhone;
    //再次请求验证码
    public Button aginRequest;

    public String serverVerification = "1234";

    public ImageView serverVerificationImgae;

    //用户手机号
    private String userPhone;
    //临时token
    private String mTemporaryToken;
    //base64图片验证码
    private String VerifyImgString;

    //输入的4个控件
    public EditText editOne;
    public EditText editTwo;
    public EditText editThree;
    public EditText editFore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pic_verification);
        Intent intent = getIntent();
        userPhone = intent.getStringExtra("userPhoneNumber");
        mTemporaryToken = intent.getStringExtra("temporaryToken");
        VerifyImgString = intent.getStringExtra("verifyImgBase64");
        Log.i(TAG, "onCreate: " + userPhone + mTemporaryToken );
        Log.i(TAG, "VerifyImgString: " + VerifyImgString);
        //初始化控件
        initControl();
        getVerifylImg();
    }

    public void getVerifylImg() {
        HttpRequest.request(MyApplication.ServerUrl.TIANHUAN_TEST_URL + "getVerifyImg", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: " + "th is died");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                Log.i(TAG, "onResponse: " + res);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        serverVerificationImgae.setImageBitmap(Utils.getStringToBitmap(res));
                    }
                });
            }
        });
    }

    private void initControl() {

        TextChange textChange = new TextChange();

        returnPhone = (ImageButton) findViewById(R.id.returnPhone);
        returnPhone.setOnClickListener(this);
        aginRequest = (Button) findViewById(R.id.aginRequest);

        serverVerificationImgae = (ImageView) findViewById(R.id.server_Verification);
        LoadServerImage();

        editOne = (EditText) findViewById(R.id.edit_one);
        editTwo = (EditText) findViewById(R.id.edit_two);
        editThree = (EditText) findViewById(R.id.edit_three);
        editFore = (EditText) findViewById(R.id.edit_fore);

        editOne.addTextChangedListener(textChange);
        editTwo.addTextChangedListener(textChange);
        editThree.addTextChangedListener(textChange);
        editFore.addTextChangedListener(textChange);
        //关闭点击事件
        editTwo.setEnabled(false);
        editThree.setEnabled(false);
        editFore.setEnabled(false);

        editOne.setOnKeyListener(this);
        editTwo.setOnKeyListener(this);
        editThree.setOnKeyListener(this);
        editFore.setOnKeyListener(this);

        aginRequest.setOnClickListener(this);

    }

    /**
     * 显示出服务器传来的base64验证码图片
     */
    private void LoadServerImage() {
        serverVerificationImgae.setImageBitmap(Utils.getStringToBitmap(VerifyImgString));
    }


    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        switch (view.getId()){
            case R.id.edit_one:
                if (i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    editOne.getText().clear();
                    return true;
                }
                break;
            case R.id.edit_two:
                if (i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    editTwo.setFocusable(false);
                    editTwo.getText().clear();
                    editOne.getText().clear();
                    findFocusable(editOne);
                    return true;
                }
                break;
            case R.id.edit_three:
                if (i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    editThree.getText().clear();
                    editThree.setFocusable(false);
                    editTwo.getText().clear();
                    findFocusable(editTwo);
                    return true;
                }
                break;
            case R.id.edit_fore:
                if (i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    editFore.setFocusable(false);
                    editFore.getText().clear();
                    editThree.getText().clear();
                    findFocusable(editThree);
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.returnPhone:
                Intent intent = new Intent(UserPicVerification.this,UserPhone.class);
                startActivity(intent);
                finish();
                break;
            case R.id.aginRequest:
                editNum = 1;
                verificationRequest();
                break;
        }

    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            if (editOne.length() >= 1){
                clearFocusable(editOne);
                editTwo.setEnabled(true);
                findFocusable(editTwo);
                if (editTwo.length()>=1){
                    clearFocusable(editTwo);
                    editThree.setEnabled(true);
                    findFocusable(editThree);
                    if (editThree.length()>=1){
                        clearFocusable(editThree);
                        editFore.setEnabled(true);
                        findFocusable(editFore);
                    }
                }
            }
            userEditVer = editOne.getText().toString() + editTwo.getText().toString()
                    + editThree.getText().toString() + editFore.getText().toString();
            if (userEditVer.length() == 4){
                //请求短信验证码
                verificationRequest();
            }
        }
    }

    /**
     * 对频繁操作的用户进行验证码判断
     */
    public void verificationRequest() {
        Log.i(TAG, "response verificationRequest Url: " + MyApplication.ServerUrl.TIANHUAN_TEST_URL + "verifyImgNum/" + userEditVer);
        HttpRequest.request(MyApplication.ServerUrl.TIANHUAN_TEST_URL + "verifyImgNum/" + userEditVer, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.i(TAG, "onResponse: " + res);
                Gson gson = new Gson();
                final VerficationJson verficationJson = gson.fromJson(res,VerficationJson.class);
                int code = verficationJson.getCode();
                Log.i(TAG, "onResponse: " + code);
                if (code == 1006){
                    VerifyToken = verficationJson.getVerifyToken();
                    timerStart();

                }else if (code == 1007){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            serverVerificationImgae.setImageBitmap(Utils.getStringToBitmap(verficationJson.getVerifyImg()));
                            requestError();
                        }
                    });

                }
            }
        });
    }
    //让editText获取焦点
    public void findFocusable(EditText editText){
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.findFocus();
    }
    //让editText失去焦点
    public void clearFocusable(EditText editText){
        editText.clearFocus();
        editText.setFocusable(false);
    }
    //请求错误
    public void requestError() {
        editOne.getText().clear();
        editTwo.getText().clear();
        editThree.getText().clear();
        editFore.getText().clear();
        editTwo.setEnabled(false);
        editThree.setEnabled(false);
        editFore.setEnabled(false);
        findFocusable(editTwo);
        findFocusable(editThree);
        findFocusable(editFore);
        findFocusable(editOne);
        if (editNum == 0){
            ToastUtil.showToast(MyApplication.getContext(),"请重新输入验证码",3000);
        }
        editNum = 0;
    }
    //启动倒计时
    public void timerStart() {

        Intent intentVerification = new Intent(UserPicVerification.this,UserVerification.class);
        Intent intent = getIntent();
        intentVerification.putExtra("user_phone",intent.getStringExtra("user_phone"));
        intentVerification.putExtra("userPhoneNumber",intent.getStringExtra("userPhoneNumber"));
        intentVerification.putExtra("VerifyToken",VerifyToken);
        startActivity(intentVerification);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UserPicVerification.this,UserPhone.class);
        startActivity(intent);
        finish();
    }
}
