package com.example.administrator.lifehelp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
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
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.lifehelp.MainActivity;
import com.example.administrator.lifehelp.R;
import com.example.administrator.lifehelp.application.MyApplication;


public class PopupWindowUtil implements View.OnClickListener,View.OnKeyListener{


    //判断用户是否输入完成手机号
    private boolean Fourteen = false;

    //判断点击
    private int isOpen = 0;
    private int phoneIsOpen = 0;
    private int editPhoneIsOpen = 0;
    //手机号码格式
    private String telRegex = "[1][34578][0-9][ ]\\d{4}[ ]\\d{4}" ;
    //删除以及返回按钮
    private Button phone_popup_delete;
    private Button edit_popup_return;
    private Button edit_popup_delete;
    private Button edit_phone_popup_return;
    private Button edit_phone_popup_delete;
    //内存存放手机号码
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    //用于二次注册的popup窗口
    private PopupWindow popupWindowPhone;
    private PopupWindow popupWindowEdit;
    private PopupWindow popupWindowPhoneEdit;
    //这是3个弹窗加载的界面
    private View editView;
    private View phoneView;
    private View editPhoneView;
    //记录用户手机号
    private String user_phone_number;
    //存在内存的手机号，用于判断
    private String shared_phone;
    //服务器发送来的验证码
    public String server_number = "4322";
    //验证码输入的4个edit 以及获取4个按钮的输入内容
    private EditText edit_one;
    private EditText edit_two;
    private EditText edit_three;
    private EditText edit_fore;
    //这是随机验证码
    private String edit_yanzhen = "gfae";
    //只需要用户输入一次验证码
    private int yanzhenPD = 0;
    //下一步按钮
    private Button window_user_btn_next;
    //输入区域
    private EditText user_edit_phone;
    //判断长度
    private CharSequence textLength;
    private TextView viewCount_Down;
    //倒计时结束后出现按钮选择
    private Button first_count_down;
    //这里是对手机验证码界面的控件
    private View view_one;
    private View view_two;
    private View view_three;
    private View view_fore;

    private TextView view_text_1;
    private TextView view_text_2;
    private TextView view_text_3;
    private TextView view_text_4;

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
    private ImageButton user_btn_return;
    //记录用户输入内容
    private String user_input;

    private int user_btn_click = 0;

    private View pop_user_phone_keyboard;

    //显示内容（验证码已发送至）
    private TextView text_view_phone;

    //记录用户输入
    private int editNum = 0;
    //记录用户输入内容
    private String edit_user_yanzhen;
    private MainActivity mainActivity;

    public PopupWindowUtil(Context context) {
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
                    AlphaAnimation exitAlpha = (AlphaAnimation) AnimationUtils.loadAnimation(mainActivity,R.anim.exit_window_back);
                    mainActivity.mainFragment.windowBack2.setAnimation(exitAlpha);
                    mainActivity.mainFragment.windowBack2.setVisibility(View.GONE);
                }


            }
        });
        popupWindowPhoneEdit.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (phoneIsOpen == 0){
                    AlphaAnimation exitAlpha = (AlphaAnimation) AnimationUtils.loadAnimation(mainActivity,R.anim.exit_window_back);
                    mainActivity.mainFragment.windowBack2.setAnimation(exitAlpha);
                    mainActivity.mainFragment.windowBack2.setVisibility(View.GONE);
                }

            }
        });

    }

    //这是倒计时方法
    public CountDownTimer timer = new CountDownTimer(60000,1000) {
        //60秒钟
        @Override
        public void onTick(long l) {
            viewCount_Down.setText((l / 1000) + "秒后重发");
        }

        @Override
        public void onFinish() {
            viewCount_Down.setVisibility(View.GONE);
            first_count_down.setVisibility(View.VISIBLE);
        }
    };
    /**
     * 对popupWindow进行关闭调用
     */
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

    /**
     * 对popupWindow进行显示调用
     */
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
    private void initControl() {
        //这是返回和关闭按钮
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

    private void initControlEvent() {
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
    private void userPhoneCode() {
        user_edit_phone = (EditText) phoneView.findViewById(R.id.user_phone_number);
        //从内存获取数据
        preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        if (preferences != null && preferences.getString("phone","").equals(shared_phone)){
            final String phone = preferences.getString("phone","");
            shared_phone = phone;
            user_edit_phone.setText(phone);
            window_user_btn_next.setTextColor(Color.rgb(252, 252, 252));
            window_user_btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //手机号码格式
                    if (user_edit_phone.getText().toString().matches(telRegex)){
                        ToastUtil.showToast(MyApplication.getContext(),"你输入的格式错误",2000);
                    }else {
                        phoneIsOpen = 0;
                        if (yanzhenPD == 1){
                            editPhoneIsOpen = 1;
                            dismiss(1);
                            show(3);
                        }else {
                            show(2);
                            showEdit();
                        }

                    }


                }
            });
        }
        user_edit_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            /**
             *这里是手机代码格式例如 158 8888 8888
             */
            public void onTextChanged(CharSequence s, int start, int before, int i2) {
                textLength = s;
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
                //用户手机号码
                user_phone_number = user_edit_phone.getText().toString();

            }

            /**
             *这里是进行对输入的判断
             */
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
                    InputMethodManager imm = (InputMethodManager)MyApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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
                                if (yanzhenPD == 1){
                                    editPhoneIsOpen = 1;
                                    dismiss(1);
                                    show(3);
                                }else {
                                    showEdit();
                                    show(2);
//                            跳转后自动让第一个edit获取焦点
                                    edit_one.setFocusable(true);
                                    edit_one.setFocusableInTouchMode(true);
                                    edit_one.requestFocus();
                                    edit_one.findFocus();
                                }
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

    //判断用户是否返回更改上次的输入
    private void showEdit() {

            edit_one.getText().clear();
            edit_two.getText().clear();
            edit_three.getText().clear();
            edit_fore.getText().clear();
            editNum = 0;

            edit_two.setFocusable(true);
            edit_two.setFocusableInTouchMode(true);
            edit_two.requestFocus();
            edit_two.findFocus();
            //
            edit_three.setFocusable(true);
            edit_three.setFocusableInTouchMode(true);
            edit_three.requestFocus();
            edit_three.findFocus();
            //
            edit_fore.setFocusable(true);
            edit_fore.setFocusableInTouchMode(true);
            edit_fore.requestFocus();
            edit_fore.findFocus();

        edit_one.setFocusable(true);
        edit_one.setFocusableInTouchMode(true);
        edit_one.requestFocus();
        edit_one.findFocus();

            if (edit_one.length() >= 1){
                edit_one.clearFocus();
                edit_one.setFocusable(false);

                edit_two.setFocusable(true);
                edit_two.setFocusableInTouchMode(true);
                edit_two.requestFocus();
                edit_two.findFocus();

                if (edit_two.length()>=1){
                    edit_two.clearFocus();
                    edit_two.setFocusable(false);

                    edit_three.setFocusable(true);
                    edit_three.setFocusableInTouchMode(true);
                    edit_three.requestFocus();
                    edit_three.findFocus();
                    if (edit_three.length()>=1){
                        edit_three.clearFocus();
                        edit_three.setFocusable(false);

                        edit_fore.setFocusable(true);
                        edit_fore.setFocusableInTouchMode(true);
                        edit_fore.requestFocus();
                        edit_fore.findFocus();
                    }
                }
            }


        editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit();
        editor.putString("phone",user_phone_number);
        editor.apply();
        editNum = 0;
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
            //键盘按钮
            case R.id.keyboard1:
                if (user_btn_click == 0) {
                    view_text_1.setText("1");
                    user_input = view_text_1.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 1){
                    view_text_2.setText("1");
                    user_input = user_input + view_text_2.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 2){
                    view_text_3.setText("1");
                    user_input = user_input + view_text_3.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 3){
                    view_text_4.setText("1");
                    user_input = user_input + view_text_4.getText().toString();
                    user_btn_click++;
                }
                break;
            case R.id.keyboard2:
                if (user_btn_click == 0) {
                    view_text_1.setText("2");
                    user_input = view_text_1.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 1){
                    view_text_2.setText("2");
                    user_input = user_input + view_text_2.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 2){
                    view_text_3.setText("2");
                    user_input = user_input + view_text_3.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 3){
                    view_text_4.setText("2");
                    user_input = user_input + view_text_4.getText().toString();
                    user_btn_click++;
                }
                break;
            case R.id.keyboard3:
                if (user_btn_click == 0) {
                    view_text_1.setText("3");
                    user_input = view_text_1.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 1){
                    view_text_2.setText("3");
                    user_input = user_input + view_text_2.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 2){
                    view_text_3.setText("3");
                    user_input = user_input + view_text_3.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 3){
                    view_text_4.setText("3");
                    user_input = user_input + view_text_4.getText().toString();
                    user_btn_click++;
                }
                break;
            case R.id.keyboard4:
                if (user_btn_click == 0) {
                    view_text_1.setText("4");
                    user_input = view_text_1.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 1){
                    view_text_2.setText("4");
                    user_input = user_input + view_text_2.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 2){
                    view_text_3.setText("4");
                    user_input = user_input + view_text_3.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 3){
                    view_text_4.setText("4");
                    user_input = user_input + view_text_4.getText().toString();
                    user_btn_click++;
                }
                break;
            case R.id.keyboard5:
                if (user_btn_click == 0) {
                    view_text_1.setText("5");
                    user_input = view_text_1.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 1){
                    view_text_2.setText("5");
                    user_input = user_input + view_text_2.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 2){
                    view_text_3.setText("5");
                    user_input = user_input + view_text_3.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 3){
                    view_text_4.setText("5");
                    user_input = user_input + view_text_4.getText().toString();
                    user_btn_click++;
                }
                break;
            case R.id.keyboard6:
                if (user_btn_click == 0) {
                    view_text_1.setText("6");
                    user_input = view_text_1.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 1){
                    view_text_2.setText("6");
                    user_input = user_input + view_text_2.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 2){
                    view_text_3.setText("6");
                    user_input = user_input + view_text_3.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 3){
                    view_text_4.setText("6");
                    user_input = user_input + view_text_4.getText().toString();
                    user_btn_click++;
                }
                break;
            case R.id.keyboard7:
                if (user_btn_click == 0) {
                    view_text_1.setText("7");
                    user_input = view_text_1.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 1){
                    view_text_2.setText("7");
                    user_input = user_input + view_text_2.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 2){
                    view_text_3.setText("7");
                    user_input = user_input + view_text_3.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 3){
                    view_text_4.setText("7");
                    user_input = user_input + view_text_4.getText().toString();
                    user_btn_click++;
                }
                break;
            case R.id.keyboard8:
                if (user_btn_click == 0) {
                    view_text_1.setText("8");
                    user_input = view_text_1.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 1){
                    view_text_2.setText("8");
                    user_input = user_input + view_text_2.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 2){
                    view_text_3.setText("8");
                    user_input = user_input + view_text_3.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 3){
                    view_text_4.setText("8");
                    user_input = user_input + view_text_4.getText().toString();
                    user_btn_click++;
                }
                break;
            case R.id.keyboard9:
                if (user_btn_click == 0) {
                    view_text_1.setText("9");
                    user_input = view_text_1.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 1){
                    view_text_2.setText("9");
                    user_input = user_input + view_text_2.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 2){
                    view_text_3.setText("9");
                    user_input = user_input + view_text_3.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 3){
                    view_text_4.setText("9");
                    user_input = user_input + view_text_4.getText().toString();
                    user_btn_click++;
                }
                break;
            case R.id.keyboard0:
                if (user_btn_click == 0) {
                    view_text_1.setText("0");
                    user_input = view_text_1.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 1){
                    view_text_2.setText("0");
                    user_input = user_input + view_text_2.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 2){
                    view_text_3.setText("0");
                    user_input = user_input + view_text_3.getText().toString();
                    user_btn_click++;
                }else if (user_btn_click == 3){
                    view_text_4.setText("0");
                    user_input = user_input + view_text_4.getText().toString();
                    user_btn_click++;
                }
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
                    AlphaAnimation exitAlpha = (AlphaAnimation) AnimationUtils.loadAnimation(mainActivity,R.anim.exit_window_back);
                    mainActivity.mainFragment.windowBack2.setAnimation(exitAlpha);
                    mainActivity.mainFragment.windowBack2.setVisibility(View.GONE);
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
                    AlphaAnimation exitAlpha = (AlphaAnimation) AnimationUtils.loadAnimation(mainActivity,R.anim.exit_window_back);
                    mainActivity.mainFragment.windowBack2.setAnimation(exitAlpha);
                    mainActivity.mainFragment.windowBack2.setVisibility(View.GONE);
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
                    AlphaAnimation exitAlpha = (AlphaAnimation) AnimationUtils.loadAnimation(mainActivity,R.anim.exit_window_back);
                    mainActivity.mainFragment.windowBack2.setAnimation(exitAlpha);
                    mainActivity.mainFragment.windowBack2.setVisibility(View.GONE);
                }
                dismiss(3);
                break;
        }
        //这里进行判断如果用户输入的验证码正确就注册成功并进入主界面
        if (user_btn_click == 4 && user_input.equals(server_number)){
            //让键盘消失。
            pop_user_phone_keyboard.setVisibility(View.GONE);
            phoneView.setVisibility(View.GONE);
            ToastUtil.showToast(MyApplication.getContext(),"验证成功，你以成功注册",3000);
            AlphaAnimation exitAlpha = (AlphaAnimation) AnimationUtils.loadAnimation(mainActivity,R.anim.exit_window_back);
            mainActivity.mainFragment.windowBack2.setAnimation(exitAlpha);
            mainActivity.mainFragment.windowBack2.setVisibility(View.GONE);
            dismiss(1);
            dismiss(3);
        }else if (user_btn_click == 4 && !user_input.equals(server_number) ){
            view_text_1.setText(" ");
            view_text_2.setText(" ");
            view_text_3.setText(" ");
            view_text_4.setText(" ");
            user_btn_click = 0;
            ToastUtil.showToast(MyApplication.getContext(),"您输入错误，请重新输入",2000);
        }
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
                    edit_two.setFocusable(false);
                    edit_two.getText().clear();
                    edit_one.getText().clear();
                    edit_one.setFocusable(true);
                    edit_one.setFocusableInTouchMode(true);
                    edit_one.requestFocus();
                    edit_one.findFocus();
                    return true;
                }
                break;
            case R.id.edit_three:
                if (i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    edit_three.getText().clear();
                    edit_three.setFocusable(false);
                    edit_two.getText().clear();
                    edit_two.setFocusable(true);
                    edit_two.setFocusableInTouchMode(true);
                    edit_two.requestFocus();
                    edit_two.findFocus();
                    return true;
                }
                break;
            case R.id.edit_fore:
                if (i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    edit_fore.setFocusable(false);
                    edit_fore.getText().clear();
                    edit_three.getText().clear();
                    edit_three.setFocusable(true);
                    edit_three.setFocusableInTouchMode(true);
                    edit_three.requestFocus();
                    edit_three.findFocus();
                    return true;
                }
                break;
        }
        return false;
    }

    //对内容进行监听
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
                edit_one.clearFocus();
                edit_one.setFocusable(false);

                edit_two.setEnabled(true);
                edit_two.setFocusable(true);
                edit_two.setFocusableInTouchMode(true);
                edit_two.requestFocus();
                edit_two.findFocus();

                if (edit_two.length()>=1){
                    edit_two.clearFocus();
                    edit_two.setFocusable(false);

                    edit_three.setEnabled(true);
                    edit_three.setFocusable(true);
                    edit_three.setFocusableInTouchMode(true);
                    edit_three.requestFocus();
                    edit_three.findFocus();
                    if (edit_three.length()>=1){
                        edit_three.clearFocus();
                        edit_three.setFocusable(false);

                        edit_fore.setEnabled(true);
                        edit_fore.setFocusable(true);
                        edit_fore.setFocusableInTouchMode(true);
                        edit_fore.requestFocus();
                        edit_fore.findFocus();
                        if (edit_fore.length() >= 1){
                            editNum = 1;
                        }
                    }
                }
            }

            edit_user_yanzhen = edit_one.getText().toString() + edit_two.getText().toString()
                    + edit_three.getText().toString() + edit_fore.getText().toString();
            if (editNum == 1){
                if (edit_user_yanzhen.equals(edit_yanzhen)){
                    timer.start();
                    yanzhenPD = 1;
                    isOpen = 1;
                    dismiss(1);
                    dismiss(2);
                    show(3);
                }else{
                    editNum = 0;
                    edit_one.getText().clear();
                    edit_two.getText().clear();
                    edit_three.getText().clear();
                    edit_fore.getText().clear();
                    edit_two.setEnabled(false);
                    edit_three.setEnabled(false);
                    edit_fore.setEnabled(false);
                    //
                    edit_two.setFocusable(true);
                    edit_two.setFocusableInTouchMode(true);
                    edit_two.requestFocus();
                    edit_two.findFocus();
                    //
                    edit_three.setFocusable(true);
                    edit_three.setFocusableInTouchMode(true);
                    edit_three.requestFocus();
                    edit_three.findFocus();
                    //
                    edit_fore.setFocusable(true);
                    edit_fore.setFocusableInTouchMode(true);
                    edit_fore.requestFocus();
                    edit_fore.findFocus();

                    edit_one.setFocusable(true);
                    edit_one.setFocusableInTouchMode(true);
                    edit_one.requestFocus();
                    edit_one.findFocus();
                    if (edit_one.length() >= 1){
                        edit_one.clearFocus();
                        edit_one.setFocusable(false);

                        edit_two.setFocusable(true);
                        edit_two.setFocusableInTouchMode(true);
                        edit_two.requestFocus();
                        edit_two.findFocus();

                        if (edit_two.length()>=1){
                            edit_two.clearFocus();
                            edit_two.setFocusable(false);

                            edit_three.setFocusable(true);
                            edit_three.setFocusableInTouchMode(true);
                            edit_three.requestFocus();
                            edit_three.findFocus();
                            if (edit_three.length()>=1){
                                edit_three.clearFocus();
                                edit_three.setFocusable(false);

                                edit_fore.setFocusable(true);
                                edit_fore.setFocusableInTouchMode(true);
                                edit_fore.requestFocus();
                                edit_fore.findFocus();
                            }
                        }
                    }
                    ToastUtil.showToast(MyApplication.getContext(),"请重新输入验证码",3000);
                }
            }
        }
    }

}
