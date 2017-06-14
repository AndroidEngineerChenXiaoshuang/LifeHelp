package com.example.administrator.lifehelp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.administrator.lifehelp.application.MyApplication;
import com.example.administrator.lifehelp.util.HttpRequest;
import com.example.administrator.lifehelp.util.ToastUtil;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserPicVerification extends AppCompatActivity implements View.OnKeyListener ,View.OnClickListener{

    private final static String TAG = "jsone";

    //判断用户是否输入完成
    public int editNum;

    public String userEditVer;

    public ImageButton returnPhone;

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
        VerifyImgString = intent.getStringExtra("VerifyImgBase64");
        Log.i(TAG, "VerifyImgString: " + VerifyImgString);
        //初始化控件
        initControl();
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

    }

    /**
     * 显示出服务器传来的base64验证码图片
     */
    private void LoadServerImage() {
        serverVerificationImgae.setImageBitmap(stringToBitmap(VerifyImgString));
    }

    /**
     * 服务器传来的base64字符转换为图片
     */
    public Bitmap stringToBitmap(String string){
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[]bitmapArray = Base64.decode(getBase64ImageString(string), Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            Log.i("jsone", "stringToBitmap: " + bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将含有转义字符的base64进行筛选获得正确格式的base64
     */
    private String getBase64ImageString(String userPhone) {
        String userPhoneNumber;
        String regEx="\\W+/";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(userPhone);
        userPhoneNumber = m.replaceAll("").trim();
        return userPhoneNumber;
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
                    editOne.setFocusable(true);
                    editOne.setFocusableInTouchMode(true);
                    editOne.requestFocus();
                    editOne.findFocus();
                    return true;
                }
                break;
            case R.id.edit_three:
                if (i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    editThree.getText().clear();
                    editThree.setFocusable(false);
                    editTwo.getText().clear();
                    editTwo.setFocusable(true);
                    editTwo.setFocusableInTouchMode(true);
                    editTwo.requestFocus();
                    editTwo.findFocus();
                    return true;
                }
                break;
            case R.id.edit_fore:
                if (i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    editFore.setFocusable(false);
                    editFore.getText().clear();
                    editThree.getText().clear();
                    editThree.setFocusable(true);
                    editThree.setFocusableInTouchMode(true);
                    editThree.requestFocus();
                    editThree.findFocus();
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
                    finish();
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
                editOne.clearFocus();
                editOne.setFocusable(false);

                editTwo.setEnabled(true);
                editTwo.setFocusable(true);
                editTwo.setFocusableInTouchMode(true);
                editTwo.requestFocus();
                editTwo.findFocus();

                if (editTwo.length()>=1){
                    editTwo.clearFocus();
                    editTwo.setFocusable(false);

                    editThree.setEnabled(true);
                    editThree.setFocusable(true);
                    editThree.setFocusableInTouchMode(true);
                    editThree.requestFocus();
                    editThree.findFocus();
                    if (editThree.length()>=1){
                        editThree.clearFocus();
                        editThree.setFocusable(false);

                        editFore.setEnabled(true);
                        editFore.setFocusable(true);
                        editFore.setFocusableInTouchMode(true);
                        editFore.requestFocus();
                        editFore.findFocus();
                        if (editFore.length() >= 1){
                            editNum = 1;
                        }
                    }
                }
            }
            userEditVer = editOne.getText().toString() + editTwo.getText().toString()
                    + editThree.getText().toString() + editFore.getText().toString();
            if (editNum == 1){
                if (userEditVer.equals(serverVerification)){
                    Intent intentVerification = new Intent(UserPicVerification.this,UserVerification.class);
                    startActivity(intentVerification);
                    finish();
                    //请求短信验证码
                    verificationRequest();
                    //timer.start();
                    //yanzhenPD = 1;
                    //isOpen = 1;
                }else{
                    editNum = 0;
                    editOne.getText().clear();
                    editTwo.getText().clear();
                    editThree.getText().clear();
                    editFore.getText().clear();
                    editTwo.setEnabled(false);
                    editThree.setEnabled(false);
                    editFore.setEnabled(false);
                    //
                    editTwo.setFocusable(true);
                    editTwo.setFocusableInTouchMode(true);
                    editTwo.requestFocus();
                    editTwo.findFocus();
                    //
                    editThree.setFocusable(true);
                    editThree.setFocusableInTouchMode(true);
                    editThree.requestFocus();
                    editThree.findFocus();
                    //
                    editFore.setFocusable(true);
                    editFore.setFocusableInTouchMode(true);
                    editFore.requestFocus();
                    editFore.findFocus();

                    editOne.setFocusable(true);
                    editOne.setFocusableInTouchMode(true);
                    editOne.requestFocus();
                    editOne.findFocus();

                    ToastUtil.showToast(MyApplication.getContext(),"请重新输入验证码",3000);
                }
            }
        }
    }

    /**
     * 对频繁操作的用户进行验证码判断
     */
    private void verificationRequest() {
        HttpRequest.request(MyApplication.ServerUrl.LIFEHELP_SERVER_URL +
                "UserAction/inorup/" + userPhone + mTemporaryToken + serverVerification, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.i("jsone", "onResponse: " + res);
            }
        });

    }

}
