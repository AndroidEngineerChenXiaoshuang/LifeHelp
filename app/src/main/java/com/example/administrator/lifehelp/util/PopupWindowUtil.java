package com.example.administrator.lifehelp.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.administrator.lifehelp.MainActivity;
import com.example.administrator.lifehelp.R;
import com.example.administrator.lifehelp.application.MyApplication;
import com.example.administrator.lifehelp.db.UserInfo;
import com.example.administrator.lifehelp.gson.LoginAndRegisterJson;
import com.example.administrator.lifehelp.gson.ParseJson;
import com.example.administrator.lifehelp.gson.UserActionJson;
import com.example.administrator.lifehelp.gson.VerficationJson;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class PopupWindowUtil extends Activity implements View.OnClickListener,View.OnKeyListener{


    public final static String TAG = "jsone";
    //判断用户是否输入完成手机号
    public boolean Fourteen = false;
    //是否开启倒计时
    public boolean isCountdown = false;
    //判断点击
    public int isOpen = 0;
    public int phoneIsOpen = 0;
    public int editPhoneIsOpen = 0;
    //手机号码格式
    public String telRegex = "[1][34578][0-9][ ]\\d{4}[ ]\\d{4}" ;
    //删除以及返回按钮
    public Button phone_popup_delete;
    public Button edit_popup_return;
    public Button edit_popup_delete;
    public Button edit_phone_popup_return;
    public Button edit_phone_popup_delete;
    //内存存放手机号码
    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    //用于二次注册的popup窗口
    public PopupWindow popupWindowPhone;
    public PopupWindow popupWindowEdit;
    public PopupWindow popupWindowPhoneEdit;
    //这是3个弹窗加载的界面
    public View editView;
    public View phoneView;
    public View editPhoneView;
    //记录用户手机号
    private String user_phone_number;
    //存在内存的手机号，用于判断
    private String shared_phone;
    //服务器发送来的验证码
    public String server_number = "1234";
    //验证码输入的4个edit 以及获取4个按钮的输入内容
    private EditText edit_one;
    private EditText edit_two;
    private EditText edit_three;
    private EditText edit_fore;
    //这是随机验证码
    public String edit_yanzhen = "1234";
    //下一步按钮
    private Button window_user_btn_next;
    //输入区域
    private EditText user_edit_phone;
    //判断长度
    private CharSequence textLength;
    private TextView viewCount_Down;
    //倒计时结束后出现按钮选择
    public Button first_count_down;
    public ImageView verification_base64;
    //这里是对手机验证码界面的控件
    public View view_one;
    public View view_two;
    public View view_three;
    public View view_fore;

    public TextView view_text_1;
    public TextView view_text_2;
    public TextView view_text_3;
    public TextView view_text_4;

    public Button update_image;

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
    public ImageButton user_btn_return;
    //记录用户输入内容
    public String user_input;

    public int user_btn_click = 0;

    public View pop_user_phone_keyboard;

    //显示内容（验证码已发送至）
    public TextView text_view_phone;

    //记录用户输入
    public int editNum = 0;
    //记录用户输入内容
    public String edit_user_yanzhen;
    public MainActivity mainActivity;

    public String onlyPhoneID;
    public String onlyPhoneToken;
    public int interFace;

    public PopupWindowUtil(Context context) {
        firstRegisterID();
        //在这个方法初始化其他控件
        mainActivity = (MainActivity) context;
        editView = LayoutInflater.from(context).inflate(R.layout.popupwindow_edit, null);
        editPhoneView = LayoutInflater.from(context).inflate(R.layout.popopwindow_edit_phone,null);
        phoneView = LayoutInflater.from(context).inflate(R.layout.poupwindow_phone,null);
        initControl();
        //这是第一个popupWindow
        popupWindowPhone = new PopupWindow(context);
        popupWindowPhone.setContentView(phoneView);
        popupWindowPhone.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindowPhone.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindowPhone.setAnimationStyle(R.style.popwindow_anim_style);
        popupWindowPhone.setFocusable(true);
        popupWindowPhone.setBackgroundDrawable(new BitmapDrawable());
        //这是第二个popupWindow
        popupWindowEdit = new PopupWindow(context);
        popupWindowEdit.setContentView(editView);
        popupWindowEdit.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindowEdit.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindowEdit.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindowEdit.setTouchable(true);
        popupWindowEdit.setFocusable(true);
        popupWindowEdit.setBackgroundDrawable(new BitmapDrawable());
        //这是第三个popupWindow
        popupWindowPhoneEdit = new PopupWindow(context);
        popupWindowPhoneEdit.setContentView(editPhoneView);
        //设置显示高宽度，以及键盘遮盖
        popupWindowPhoneEdit.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindowPhoneEdit.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindowPhoneEdit.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindowPhoneEdit.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindowPhoneEdit.setTouchable(true);
        popupWindowPhoneEdit.setFocusable(true);
        popupWindowPhoneEdit.setBackgroundDrawable(new BitmapDrawable());

        userPhoneCode();

        popupWindowPhone.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (isOpen == 0 && editPhoneIsOpen == 0){
                    setBackAnimation();
                }
            }
        });
        popupWindowPhoneEdit.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (phoneIsOpen == 0){
                    setBackAnimation();
                }
            }
        });
    }

    //这是倒计时方法
    public CountDownTimer timer = new CountDownTimer(60000,1000) {
        //60秒钟
        @Override
        public void onTick(long l) {
            isCountdown = true;
            viewCount_Down.setText((l / 1000) + "秒后重发");
        }

        @Override
        public void onFinish() {
            isCountdown = false;
            viewCount_Down.setVisibility(View.GONE);
            first_count_down.setVisibility(View.VISIBLE);
        }
    };

    //对popupWindow进行关闭调用
    public void dismiss(int i) {
        switch (i) {
            case 1:
                popupWindowPhone.dismiss();
                break;
            case 2:
                popupWindowEdit.dismiss();
                break;
            case 3:
                popupWindowPhoneEdit.dismiss();
                break;

        }
    }

    //对popupWindow进行显示调用
    public void show(int i) {
        switch (i){
            case 1:
                popupWindowPhone.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindowPhone.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindowPhone.showAtLocation(phoneView, Gravity.CENTER,0,0);
                break;
            case 2:
                popupWindowEdit.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindowEdit.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindowEdit.showAtLocation(editView,Gravity.CENTER,0,0);
                break;
            case 3:
                edit_one.getText().clear();
                edit_two.getText().clear();
                edit_three.getText().clear();
                edit_fore.getText().clear();
                editNum = 0;
                if (user_phone_number != null){
                    text_view_phone.setText("验证码已发送至 " + user_phone_number);
                }else {
                    text_view_phone.setText(shared_phone);
                }
                popupWindowPhoneEdit.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindowPhoneEdit.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindowPhoneEdit.showAtLocation(editPhoneView, Gravity.BOTTOM,0,0);
                break;
        }

    }
    //初始化控件
    public void initControl() {
        //这是返回和关闭按钮
        update_image = (Button) editView.findViewById(R.id.update_image);
        phone_popup_delete = (Button) phoneView.findViewById(R.id.phone_popup_delete);
        edit_popup_return = (Button) editView.findViewById(R.id.edit_popup_return);
        edit_popup_delete = (Button) editView.findViewById(R.id.edit_popup_delete);
        edit_phone_popup_return = (Button) editPhoneView.findViewById(R.id.edit_phone_popup_return);
        edit_phone_popup_delete = (Button) editPhoneView.findViewById(R.id.edit_phone_popup_delete);
        //这是键盘
        pop_user_phone_keyboard = editPhoneView.findViewById(R.id.pop_keyboard);
        //这是键盘内的按钮
        keyBoard1 = (Button) editPhoneView.findViewById(R.id.keyboard1);
        keyBoard2 = (Button) editPhoneView.findViewById(R.id.keyboard2);
        keyBoard3 = (Button) editPhoneView.findViewById(R.id.keyboard3);
        keyBoard4 = (Button) editPhoneView.findViewById(R.id.keyboard4);
        keyBoard5 = (Button) editPhoneView.findViewById(R.id.keyboard5);
        keyBoard6 = (Button) editPhoneView.findViewById(R.id.keyboard6);
        keyBoard7 = (Button) editPhoneView.findViewById(R.id.keyboard7);
        keyBoard8 = (Button) editPhoneView.findViewById(R.id.keyboard8);
        keyBoard9 = (Button) editPhoneView.findViewById(R.id.keyboard9);
        keyBoard0 = (Button) editPhoneView.findViewById(R.id.keyboard0);
        keyBoard_delete = (ImageButton) editPhoneView.findViewById(R.id.keyboard_delete);
        user_btn_return = (ImageButton) editPhoneView.findViewById(R.id.user_btn_return);
        //这是显示用户手机号的text
        text_view_phone = (TextView) editPhoneView.findViewById(R.id.text_view_phone);
        //这是输入的内容
        view_text_1 = (TextView) editPhoneView.findViewById(R.id.pop_view_text_1);
        view_text_2 = (TextView) editPhoneView.findViewById(R.id.pop_view_text_2);
        view_text_3 = (TextView) editPhoneView.findViewById(R.id.pop_view_text_3);
        view_text_4 = (TextView) editPhoneView.findViewById(R.id.pop_view_text_4);
        //这是输入显示的view
        view_one = editPhoneView.findViewById(R.id.fore);
        view_two = editPhoneView.findViewById(R.id.hour);
        view_three = editPhoneView.findViewById(R.id.second);
        view_fore = editPhoneView.findViewById(R.id.minute);
        //倒计时显示
        viewCount_Down = (TextView) editPhoneView.findViewById(R.id.text_count_down);
        first_count_down = (Button) editPhoneView.findViewById(R.id.first_count_down);
        //这里是验证码
        edit_one = (EditText) editView.findViewById(R.id.edit_one);
        edit_two = (EditText) editView.findViewById(R.id.edit_two);
        edit_three = (EditText) editView.findViewById(R.id.edit_three);
        edit_fore = (EditText) editView.findViewById(R.id.edit_fore);
        //显示base64图片
        verification_base64 = (ImageView) editView.findViewById(R.id.verification_base64);
        //这是用户进行手机输入的界面
        window_user_btn_next = (Button) phoneView.findViewById(R.id.window_user_btn_next);
        //这是倒计时结束显示的重新发送按钮
        first_count_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //这里重新发送
                first_count_down.setVisibility(View.GONE);
                //开始倒计时线程
                viewCount_Down.setVisibility(View.VISIBLE);
                timer.start();
            }
        });
        //在这里为控件进行注册事件
        initControlEvent();
    }
    //为控件注册事件
    public void initControlEvent() {
        //更新图片
        update_image.setOnClickListener(this);
        //每一个popupWindow的关闭和撤回按钮
        phone_popup_delete.setOnClickListener(this);
        edit_popup_return.setOnClickListener(this);
        edit_popup_delete.setOnClickListener(this);
        edit_phone_popup_return.setOnClickListener(this);
        edit_phone_popup_delete.setOnClickListener(this);
        //自定义键盘的点击事件
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
        keyBoard0.setOnClickListener(this);
        keyBoard_delete.setOnClickListener(this);
        //手机短信验证码的4个view的点击事件
        view_one.setOnClickListener(this);
        view_two.setOnClickListener(this);
        view_three.setOnClickListener(this);
        view_fore.setOnClickListener(this);
        //监听4个edit的输入
        TextChange textChange = new TextChange();
        edit_one.addTextChangedListener(textChange);
        edit_two.addTextChangedListener(textChange);
        edit_three.addTextChangedListener(textChange);
        edit_fore.addTextChangedListener(textChange);
        //对edit的删除键进行监听
        edit_one.setOnKeyListener(this);
        edit_two.setOnKeyListener(this);
        edit_three.setOnKeyListener(this);
        edit_fore.setOnKeyListener(this);
        //关闭点击事件
        edit_two.setEnabled(false);
        edit_three.setEnabled(false);
        edit_fore.setEnabled(false);

    }

    //判断用户是否已经输入过手机号码
    public void userPhoneCode() {
        user_edit_phone = (EditText) phoneView.findViewById(R.id.user_phone_number);
        user_edit_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            //对输入的手机号进行格式化888-8888-8888
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
                //用户手机号码
                user_phone_number = user_edit_phone.getText().toString();
            }
            //*这里是进行对输入的判断
            @Override
            public void afterTextChanged(Editable s) {
                String text = textLength.toString() + ' ';
                final int phone_length = text.length();
                if (phone_length == 14) {
                    Fourteen = false;
                    //将手机号存到内存里面
                    user_edit_phone.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            user_edit_phone.setFocusable(true);
                            user_edit_phone.setFocusableInTouchMode(true);
                            return false;
                        }
                    });
                    /**
                     * 这里让输入完后失去输入焦点
                     */
                    user_edit_phone.clearFocus();
                    user_edit_phone.setFocusable(false);
                    InputMethodManager imm = (InputMethodManager) MyApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(user_edit_phone.getWindowToken(),0);
                    window_user_btn_next.setTextColor(Color.rgb(252, 252, 252));
                    /**
                     * 这里是进行跳转到下一个界面，并且将手机号发送过去
                     */
                    window_user_btn_next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if ( !user_edit_phone.getText().toString().matches(telRegex) ){
                                if (!Fourteen)
                                    ToastUtil.showToast(MyApplication.getContext(),"你输入的格式错误",2000);
                            }else {
                                phoneIsOpen = 0;
                                editPhoneIsOpen = 1;
                                if (!isCountdown){
                                    serverRequest();
                                }else if (isCountdown){
                                    show(3);
                                    dismiss(1);
                                }

                                findFocusable(edit_one);
                            }
                        }
                    });
                }
                if (phone_length != 14){
                    Fourteen = true;
                    window_user_btn_next.setTextColor(Color.rgb(150, 147, 147));
                    window_user_btn_next.setEnabled(true);
                }
            }
        });
    }
    //将手机号发送给服务器，以服务器传回的数据进行下一步判断
    public void serverRequest() {
        //如果第一次启动的时候没联网，那么会再次请求
        if (onlyPhoneToken == null){
            firstRegisterID();
        }else {
            Log.i("jsone", "serverRequest: " + MyApplication.ServerUrl.LIFEHELP_SERVER_URL +
                    "v1/UserAction/inorup/" + Utils.getPhoneNumber(user_phone_number) + "/" + onlyPhoneToken);
            HttpRequest.request(MyApplication.ServerUrl.LIFEHELP_SERVER_URL +
                    "v1/UserAction/inorup/" + Utils.getPhoneNumber(user_phone_number) + "/" + onlyPhoneToken, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    judgeInterFace(response.body().string());
                }
            });
        }

    }

    //用户输入手机号，对之进行判断，是否为重复操作。将进入不同的界面
    public void judgeInterFace(String string) {
        Log.i(TAG, "judgeInterFace: judgeInterFacejudgeInterFacejudgeInterFace" + string);
        Gson gson = new Gson();
        final UserActionJson userActionJson = gson.fromJson(string,UserActionJson.class);
        interFace = userActionJson.getCode();
        Log.i("jsone", "judgeInterFace: " + interFace);
        Log.i(TAG, "judgeInterFace: " + userActionJson.getMessage());
        Log.i(TAG, "judgeInterFace: " + userActionJson.getVerifyImg());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (interFace == 1021){
                    show(3);
                    //开启倒计时
                    timer.start();
                    dismiss(1);
                }else if (interFace == 1588){
                    verification_base64.setImageBitmap(Utils.getStringToBitmap(userActionJson.getVerifyImg()));
                    show(2);
                    dismiss(1);

                }else if (interFace == 1505){
                    Log.i("jsone", "judgeInterFace: " + interFace);
                }
            }
        });

    }

    //第一个打开界面将会获得一个临时token
    public void firstRegisterID() {
        onlyPhoneID = Utils.getPhoneId();
        HttpRequest.request(MyApplication.ServerUrl.LIFEHELP_SERVER_URL + "v1/Signature/" + onlyPhoneID, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                onlyPhoneToken = ParseJson.getOnlyPhoneToken(response.body().string());
                Log.i("jsone", "onResponse: " + onlyPhoneToken);
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

    @Override
    public void onClick(View view) {
            switch (view.getId()){
                case R.id.fore:
                    pop_user_phone_keyboard.setVisibility(View.VISIBLE);
                    break;
                case R.id.hour:
                    pop_user_phone_keyboard.setVisibility(View.VISIBLE);
                    break;
                case R.id.second:
                    pop_user_phone_keyboard.setVisibility(View.VISIBLE);
                    break;
                case R.id.minute:
                    pop_user_phone_keyboard.setVisibility(View.VISIBLE);
                    break;
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
                case R.id.user_btn_return:
                    pop_user_phone_keyboard.setVisibility(View.GONE);
                    break;
                //返回关闭按钮
                case R.id.phone_popup_delete:
                    if(mainActivity!=null){
                        setBackAnimation();
                    }
                    dismiss(1);
                    dismiss(2);
                    dismiss(3);
                    break;
                case R.id.edit_popup_return:
                    show(1);
                    dismiss(2);
                    break;
                case R.id.edit_popup_delete:
                    if(mainActivity!=null){
                        setBackAnimation();
                    }
                    dismiss(1);
                    dismiss(2);
                    dismiss(3);
                    break;
                case R.id.edit_phone_popup_return:
                    isOpen = 0;
                    phoneIsOpen = 1;
                    show(1);
                    dismiss(3);
                    break;
                case R.id.edit_phone_popup_delete:
                    if(mainActivity!=null ){
                        setBackAnimation();
                    }
                    dismiss(3);
                    break;
                case R.id.update_image:
                    //更换验证码图片
                    editNum = 1;
                    requestVerification();
            }
        //这里进行判断如果用户输入的验证码正确就注册成功并进入主界面
        if (user_btn_click == 4){
            loginAndRegisterRequest();
        }
    }

    //登陆和注册请求
    public void loginAndRegisterRequest() {

        Log.i(TAG, "loginAndRegisterRequest: " + MyApplication.ServerUrl.LIFEHELP_SERVER_URL +
                "v1/UserAction/verifySMS/" + Utils.getPhoneNumber(user_phone_number) + "/" + user_input + "/" + onlyPhoneToken);
        HttpRequest.request(MyApplication.ServerUrl.LIFEHELP_SERVER_URL +
                "v1/UserAction/verifySMS/" + Utils.getPhoneNumber(user_phone_number) + "/" + user_input + "/" + onlyPhoneToken, new Callback() {
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //让键盘消失。
                            pop_user_phone_keyboard.setVisibility(View.GONE);
                            phoneView.setVisibility(View.GONE);
                            ToastUtil.showToast(MyApplication.getContext(),"验证成功，你以成功注册",3000);
                            dismiss(1);
                            dismiss(3);
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
                            user_input = null;
                            ToastUtil.showToast(MyApplication.getContext(),"您输入错误，请重新输入",2000);
                        }
                    });
                }
            }
        });

    }
    //设置一个背景显示已经加载和关闭动画
    public void setBackAnimation() {
        AlphaAnimation exitAlpha = (AlphaAnimation) AnimationUtils.loadAnimation(mainActivity, R.anim.exit_window_back);
        mainActivity.windowBack2.setAnimation(exitAlpha);
        mainActivity.windowBack2.setVisibility(View.GONE);
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
    //对editText的删除按键进行监听
    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        switch (view.getId()){
            case R.id.edit_one:
                if (i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    edit_one.getText().clear();
                    return true;
                }
                break;
            case R.id.edit_two:
                if (i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    clearFocusable(edit_two);
                    edit_two.getText().clear();
                    edit_one.getText().clear();
                    findFocusable(edit_one);
                    return true;
                }
                break;
            case R.id.edit_three:
                if (i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    edit_three.getText().clear();
                    clearFocusable(edit_three);
                    edit_two.getText().clear();
                    findFocusable(edit_two);
                    return true;
                }
                break;
            case R.id.edit_fore:
                if (i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    edit_three.getText().clear();
                    clearFocusable(edit_fore);
                    edit_fore.getText().clear();
                    findFocusable(edit_three);
                    return true;
                }
                break;
        }
        return false;
    }

    //验证码输入的editText对内容进行监听
    private class TextChange implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (edit_one.length() >= 1){
                edit_two.setEnabled(true);
                clearFocusable(edit_one);
                findFocusable(edit_two);
                if (edit_two.length()>=1){
                    edit_three.setEnabled(true);
                    clearFocusable(edit_two);
                    findFocusable(edit_three);
                    if (edit_three.length()>=1){
                        edit_fore.setEnabled(true);
                        clearFocusable(edit_three);
                        findFocusable(edit_fore);

                    }
                }
            }

            edit_user_yanzhen = edit_one.getText().toString() + edit_two.getText().toString()
                    + edit_three.getText().toString() + edit_fore.getText().toString();
            Log.i(TAG, "afterTextChanged: " + edit_user_yanzhen.length());
            if (edit_user_yanzhen.length() == 4){
                    isOpen = 1;
                    //当验证码输入正确之后，再次请求服务器
                    Log.i(TAG, "afterTextChanged: " + MyApplication.ServerUrl.LIFEHELP_SERVER_URL +
                            "v1/UserAction/inorup/" + Utils.getPhoneNumber(user_phone_number) +"/"+ onlyPhoneToken +"/"+ edit_user_yanzhen);

                requestVerification();
            }
        }
    }
    //将输入的验证码发送给服务器判断正确，否则重新输入输入并且更新验证码
    public void requestVerification() {
        HttpRequest.request(MyApplication.ServerUrl.LIFEHELP_SERVER_URL +
                "v1/UserAction/inorup/" + Utils.getPhoneNumber(user_phone_number)
                +"/"+ onlyPhoneToken +"/"+ edit_user_yanzhen, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.i(TAG, "error: " + res);
                Gson gson = new Gson();
                final VerficationJson verficationJson = gson.fromJson(res,VerficationJson.class);
                String status = verficationJson.getStatus();
                Log.i(TAG, "run: " + verficationJson.getVerifyImg());
                int code = verficationJson.getCode();
                Log.i(TAG, "code: " + status);
                if (code == 5754 || code == 5854 || code == 1021){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "run: " );
                            timerStart();
                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "run: " + verficationJson.getVerifyImg());
                            verification_base64.setImageBitmap(Utils.getStringToBitmap(verficationJson.getVerifyImg()));
                           requestError();
                        }
                    });
                }
            }
        });
    }

    public void requestError() {
        edit_one.getText().clear();
        edit_two.getText().clear();
        edit_three.getText().clear();
        edit_fore.getText().clear();
        edit_two.setEnabled(false);
        edit_three.setEnabled(false);
        edit_fore.setEnabled(false);
        findFocusable(edit_two);
        findFocusable(edit_three);
        findFocusable(edit_fore);
        findFocusable(edit_one);
        if (editNum == 0){
            ToastUtil.showToast(MyApplication.getContext(),"请重新输入验证码",3000);
        }
        editNum = 0;
    }
    //启动倒计时
    public void timerStart() {
        dismiss(1);
        dismiss(2);
        show(3);
        timer.start();
        //将textView显示出来
        viewCount_Down.setVisibility(View.VISIBLE);
        //将button隐藏掉
        first_count_down.setVisibility(View.GONE);
    }
}