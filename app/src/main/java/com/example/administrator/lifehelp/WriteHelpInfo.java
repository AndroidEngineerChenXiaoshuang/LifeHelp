package com.example.administrator.lifehelp;

import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.util.Util;
import com.example.administrator.lifehelp.application.MyApplication;
import com.example.administrator.lifehelp.db.UserHelpInfo;
import com.example.administrator.lifehelp.util.AnimationUtil;
import com.example.administrator.lifehelp.util.SetFouchable;
import com.example.administrator.lifehelp.util.Utils;
import com.google.gson.reflect.TypeToken;

import org.litepal.crud.DataSupport;
import java.util.ArrayList;
import java.util.List;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 用户发布帮助
 */

public class WriteHelpInfo extends AppCompatActivity implements View.OnClickListener,TextWatcher, View.OnFocusChangeListener{
    //输入该笔订单的佣金
    public EditText inputMoneySize;
    //用户设置完成的时间
    public LinearLayout setTimerLayout;
    //标题栏上方的返回键
    public ImageView backImg;
    //文章的标题
    public EditText help_title;
    //文章的详细信息
    public EditText help_info;
    //android用于设置用户指定的时间完成控件
    public TimePicker timePicker;
    //用于弹出顶部设置时间的view
    public View root;
    //用于弹出提示框
    public View warnLayout;
    //该view用于遮挡背景颜色
    public View windowBack;
    //用于记录小时
    public int hour;
    //用于记录分钟
    public int minute;
    //用于显示用户设置后的时间
    public TextView setTimer_text;
    //用于存放选择后的图片
    public LinearLayout photoView;
    //用于提示用户当前可输入的数
    public TextView infoSize;
    //用于提示用户是否保存的view
    public View isSaveRoot;
    //用于保存用户的相片目录地址
    public List<String> photoPaths;
    //addImg不可选的颜色
    public static final int NOTSELECTOR_ADDIMGCOLOR = 0xFFE2E8EA;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_help_info);
        initData();
    }

    //初始化所有控件
    public void initData(){
        //初速化用户输入金额控件
        inputMoneySize = (EditText) findViewById(R.id.help_moneySize);
        //初始化用户设置时间的布局
        setTimerLayout = (LinearLayout) findViewById(R.id.setTimer);
        //返回上级
        backImg = (ImageView) findViewById(R.id.back);
        //初始化用户设置文章标题
        help_title = (EditText) findViewById(R.id.help_title);
        //初始化用户设置该笔订单的详细信息
        help_info = (EditText) findViewById(R.id.help_info);
        //初速化界面的背景颜色,默认值为gone 用于弹出设置时间控件时的背景颜色展示
        windowBack = findViewById(R.id.windowBack);
        //给windowBack添加点击事件
        windowBack.setOnClickListener(this);
        //用于设置用户设置指定完成时间后的一个text展示
        setTimer_text = (TextView) findViewById(R.id.timer_id);
        //用于提示用户金额错误的view
        warnLayout = findViewById(R.id.warn_layout);
        //为输入金额大小控件添加焦点监听
        inputMoneySize.setOnFocusChangeListener(this);
        //为输入金额大小控件添加输入监听事件
        inputMoneySize.addTextChangedListener(this);
        //监听用户点击设置指定完成的时间
        setTimerLayout.setOnClickListener(this);
        //监听用户点击back键
        backImg.setOnClickListener(this);
        //加载一个view用于popupWindow
        root = LayoutInflater.from(this).inflate(R.layout.user_set_timer,null,false);
        //获取添加图片控件的布局
        photoView = (LinearLayout) findViewById(R.id.photo_root);
        //获取设置时间控件
        timePicker = (TimePicker) root.findViewById(R.id.setTimer);
        //设置时间控件为24小时制
        timePicker.setIs24HourView(true);

        //监听时间的滚动事件
        help_info.addTextChangedListener(this);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                WriteHelpInfo.this.hour = hourOfDay;
                WriteHelpInfo.this.minute = minute;
            }
        });
        timePicker.setCurrentHour(0);
        timePicker.setCurrentMinute(0);
        //设置timerPicker控件不能设置
        timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        //监听popupWindow的确定按钮事件
        root.findViewById(R.id.setTimer_sure).setOnClickListener(this);
        //监听popupWindow的取消事件
        root.findViewById(R.id.setTimer_cancle).setOnClickListener(this);
        findViewById(R.id.addImg).setOnClickListener(this);
        //提交最后的内容
        findViewById(R.id.helpInfoCommit).setOnClickListener(this);
        infoSize = (TextView) findViewById(R.id.info_length);
        help_info.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = help_info.getText().toString().length();
                infoSize.setText(String.format("%d/100",length));
            }
        });
        isSaveRoot = LayoutInflater.from(this).inflate(R.layout.help_is_save,null,false);
        isSaveRoot.findViewById(R.id.save_dismss).setOnClickListener(this);
        isSaveRoot.findViewById(R.id.save_sure).setOnClickListener(this);
        //初始化存放地址的集合
        photoPaths = new ArrayList<>();
    }

    /**
     * BUG :
     * 如果用户在填写了数据后并将数据保存为了草稿模式
     * 下次再次启动该页面的时候会回调onResume,程序会
     * 在此将数据进行写入然后进行ui刷新,如果此时我们
     * 的用户在进入图片筛选界面进行筛选图片,当返回该界面的时候
     * 会再次调用该界面进行并执行onResume方法，此时会将数据库中的
     * 数据再一次拉取出来，而我们筛选的图片也会随之被加入到layout中
     * 从而导致图片不正确
     */

    public boolean isFirst = false;

    @Override
    protected void onResume() {
        super.onResume();
        if(!isFirst){
            UserHelpInfo userHelpInfo = DataSupport.find(UserHelpInfo.class,1);
            if(userHelpInfo!=null){
                if(userHelpInfo.getUser_title()!=null){
                    if(userHelpInfo.getUser_title().length()>0){
                        help_title.setText(userHelpInfo.getUser_title());
                    }
                }
                if(userHelpInfo.getUser_money()!=null){
                    if(userHelpInfo.getUser_money().length()>0){
                        inputMoneySize.setText(userHelpInfo.getUser_money());
                    }
                }
                if(userHelpInfo.getUser_timer()!=null){
                    if(userHelpInfo.getUser_timer().length()>0){
                        setTimer_text.setText(userHelpInfo.getUser_timer());
                    }
                }
                if(userHelpInfo.getUser_info()!=null){
                    if(userHelpInfo.getUser_info().length()>0){
                        help_info.setText(userHelpInfo.getUser_info());
                    }
                }
                if(userHelpInfo.getUser_img()!=null){
                    if(userHelpInfo.getUser_img().length()>0){
                        String[] imagpaths = userHelpInfo.getUser_img().split(":");
                        List<PhotoInfo> photoInfos = new ArrayList<>();
                        for(String paths : imagpaths){
                            PhotoInfo photoInfo = new PhotoInfo();
                            photoInfo.setPhotoPath(paths);
                            photoInfos.add(photoInfo);
                            photoPaths.add(photoInfo.getPhotoPath());
                        }
                        addPhtoView(photoInfos);
                    }
                }
                //避免系统第二次加载该函数
                isFirst = true;
            }

        }

    }

    //用户输入前
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    //用户输入中
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    //用户输入后
    @Override
    public void afterTextChanged(Editable s) {
        String info = s.toString();
        String[] infos = info.split("\\.");
        if(infos.length>1){
            //当用户输入的金额不符合规定的格式进行处理
            if(infos[1].length()>=3){
                AnimationUtil.infoAnimationTranslate(warnLayout,this);
            }
        }
    }

    //监听控件是否获取焦点的状态
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.help_moneySize:
                if(hasFocus){
                    inputMoneySize.setHint("");
                }else if(inputMoneySize.getText().length()==0){
                    inputMoneySize.setHint("0.00");
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setTimer:
                SetFouchable.setFouch(v);
                Utils.openWindowManager(root,v,windowBack, Gravity.BOTTOM,
                ViewGroup.LayoutParams.MATCH_PARENT,250,R.style.StartSetTimerAniamtion);
                break;
            case R.id.back:
                SetFouchable.setFouch(v);
                thinkExit();
                break;
            case R.id.setTimer_sure:
                if(hour==0&&minute==0){
                    Utils.closeWindowManager();
                }else{
                    String minute = String.valueOf(this.minute);
                    String hour = String.valueOf(this.hour);
                    if(minute.length()==1){
                        minute = "0"+minute;
                    }
                    if(hour.length()==1){
                        hour = "0"+hour;
                    }
                    setTimer_text.setText("剩余:"+hour+"小时"+minute+"分钟");
                    Utils.closeWindowManager();
                }
                break;
            case R.id.setTimer_cancle:
                Utils.closeWindowManager();
                break;
            case R.id.addImg:
                if(photoView.getChildCount()<5){
                    //打开多选图片
                    GalleryFinal.openGalleryMuti(MyApplication.Type.START_PHOTO_RESULT, 5-photoView.getChildCount(), new GalleryFinal.OnHanlderResultCallback() {
                        @Override
                        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                            switch (reqeustCode){
                                case MyApplication.Type.START_PHOTO_RESULT:
                                    //添加phto
                                    addPhtoView(resultList);
                                    for(PhotoInfo photoInfo : resultList){
                                        photoPaths.add(photoInfo.getPhotoPath());
                                    }
                                    break;
                            }
                        }

                        @Override
                        public void onHanlderFailure(int requestCode, String errorMsg) {
                            Toast.makeText(WriteHelpInfo.this,"获取图片失败!",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(WriteHelpInfo.this,"图片最多上传5张!",Toast.LENGTH_SHORT).show();
                }
                break;

            //用户点击了取消
            case R.id.save_dismss:
                Utils.closeWindowManager();
                ContentValues contentValues = new ContentValues();
                contentValues.put("user_title","");
                contentValues.put("user_money","");
                contentValues.put("user_info","");
                contentValues.put("user_img","");
                contentValues.put("user_timer","");
                DataSupport.updateAll(UserHelpInfo.class,contentValues);
                finish();
                break;
            case R.id.save_sure:
                //进行本地数据库存储操作
                List<UserHelpInfo> list = DataSupport.findAll(UserHelpInfo.class);
                StringBuilder stringBuilder = new StringBuilder();
                for(String string : photoPaths){
                    if(string.length()>0){
                        stringBuilder.append(string);
                        stringBuilder.append(":");
                    }
                }
                if(list.size()>0){
                    ContentValues setEmpty = new ContentValues();
                    setEmpty.put("user_title",help_title.getText().toString());
                    setEmpty.put("user_money",inputMoneySize.getText().toString());
                    setEmpty.put("user_info",help_info.getText().toString());
                    setEmpty.put("user_timer",setTimer_text.getText().toString());
                    setEmpty.put("user_img",stringBuilder.toString());
                    DataSupport.updateAll(UserHelpInfo.class,setEmpty,"id=1");
                }else if(list.size() == 0){
                    UserHelpInfo userHelpInfo = new UserHelpInfo();
                    userHelpInfo.setUser_title(help_title.getText().toString());
                    userHelpInfo.setUser_money(inputMoneySize.getText().toString());
                    userHelpInfo.setUser_timer(setTimer_text.getText().toString());
                    userHelpInfo.setUser_info(help_info.getText().toString());
                    userHelpInfo.setUser_info(stringBuilder.toString());
                    userHelpInfo.save();
                }else{
                    Toast.makeText(WriteHelpInfo.this,"存储到数据库出现异常",Toast.LENGTH_SHORT).show();
                }
                Utils.closeWindowManager();
                finish();
                break;
            case R.id.windowBack:
                Utils.closeWindowManager();
                break;
            case R.id.helpInfoCommit:
                break;
        }
    }

    public void addPhtoView(List<PhotoInfo> photoInfos){
        for(PhotoInfo photoInfo : photoInfos){
            final int position = photoView.getChildCount();
            final View childView = LayoutInflater.from(WriteHelpInfo.this).inflate(R.layout.photo_item,photoView,false);
            ImageView dismssImg = (ImageView) childView.findViewById(R.id.dismss);
            dismssImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photoView.removeView(childView);
                    photoPaths.set(position,"");
                }
            });
            ImageView imageView = (ImageView) childView.findViewById(R.id.user_image);
            Glide.with(WriteHelpInfo.this)
                    .load(photoInfo.getPhotoPath())
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .crossFade(500)
                    .into(imageView);
            photoView.addView(childView);

        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.close_activity_animation_translate,R.anim.close_activity_animation_tarnslate_exit);
    }

    @Override
    public void onBackPressed() {
        //如果点击back判断当前popupwindow是否显示,如果显示了的话就将其dimss掉
        if(Utils.popupWindow.isShowing()){
            Utils.closeWindowManager();
            //没有显示,做thikExit逻辑处理
        }else{
            thinkExit();
        }
    }


    //用于判断当前ui界面中是否携带数据,如果存有数据就询问用户是否保存草稿
    public void thinkExit(){
        Log.d("Jam",String.valueOf(photoView.getChildCount()));
        if(help_title.getText().toString().length()>0){
            Utils.openWindowManager(isSaveRoot,findViewById(R.id.setTimer),windowBack,Gravity.CENTER, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,R.style.StartSetSaveWindow);
        }else if(inputMoneySize.getText().toString().length()>0){
            Utils.openWindowManager(isSaveRoot,findViewById(R.id.setTimer),windowBack,Gravity.CENTER, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,R.style.StartSetSaveWindow);
        }else if(help_info.getText().toString().length()>0) {
            Utils.openWindowManager(isSaveRoot, findViewById(R.id.setTimer), windowBack, Gravity.CENTER, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, R.style.StartSetSaveWindow);
        }else if(setTimer_text.getText().toString().lastIndexOf(":")!=-1){
            Utils.openWindowManager(isSaveRoot,findViewById(R.id.setTimer),windowBack,Gravity.CENTER,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,R.style.StartSetSaveWindow);
        }else if(photoView.getChildCount()>0){
            Utils.openWindowManager(isSaveRoot,findViewById(R.id.setTimer),windowBack,Gravity.CENTER,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,R.style.StartSetSaveWindow);
        }else{
            super.onBackPressed();
        }
    }

}