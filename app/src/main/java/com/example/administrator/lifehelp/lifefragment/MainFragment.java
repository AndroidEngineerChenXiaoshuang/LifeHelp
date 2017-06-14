package com.example.administrator.lifehelp.lifefragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.lifehelp.MainActivity;
import com.example.administrator.lifehelp.R;
import com.example.administrator.lifehelp.WriteHelpInfo;
import com.example.administrator.lifehelp.adapter.InitArticleAdapter;
import com.example.administrator.lifehelp.adapter.OnRecyclerListener;
import com.example.administrator.lifehelp.adapter.RecyclerItemDecoration;
import com.example.administrator.lifehelp.adapter.OnRefreshListener;
import com.example.administrator.lifehelp.application.MyApplication;
import com.example.administrator.lifehelp.gson.InitArticle;
import com.example.administrator.lifehelp.gson.InitData;
import com.example.administrator.lifehelp.service.DownloadService;
import com.example.administrator.lifehelp.util.AnimationUtil;
import com.example.administrator.lifehelp.util.HttpRequest;
import com.example.administrator.lifehelp.util.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 *  主碎片：布局在主界面之上的碎片
 */

public class MainFragment extends Fragment implements View.OnClickListener,Animation.AnimationListener{

    //用于打开左边的侧滑
    public ImageView open_userSetting;
    //用于打开右边筛选的侧滑
    public ImageView open_orderInfo;
    //底部第一项按钮,不是实际按钮,而是一个imageView
    public ImageView bottom_item1;
    //底部第一项按钮,不是实际按钮,而是二个imageView
    public ImageView bottom_item2;
    //底部第一项按钮,不是实际按钮,而是三个imageView
    public ImageView bottom_item3;
    //与fragment绑定的视图
    public View root;
    //关联fragment的Activity
    public MainActivity mainActivity;
    //window动画的背景颜色,用来显示遮挡效果
    public View windowBack ;
    //imageView 快速发布
    public ImageView quickImageView;
    //imageView 发布
    public ImageView writeImageView;
    //该RecyclerView是我们的文章界面展示
    public RecyclerView initDateRecycler;
    //用于使用RecyclerView的adapter
    public InitArticleAdapter initArticleAdapter;
    public List<InitArticle> initArticles;
    //SwipeRefersh控件
    public SwipeRefreshLayout swipeRefreshLayout;

    public TextView quickText;
    public TextView infoCommit;

    public HandlerInfo handlerInfo;

    //是否显示有网络连接
    public LinearLayout shownoNetWork;
    //是否显示没有文章提示
    public LinearLayout showServerNoContent;


    /**
     * 动画类型
     * START_ROATE:表示开始进行添加按钮的旋转动画
     */
    //用于handler的what区别
    public static final int START_ROATE = 1;
    public static final int SHORT_ANIMATION = 300;
    public static final int EXIT_ANIMATION = 2;
    //动画是否正在运行
    public boolean isRunning = false;
    //动画主体是否打开
    public boolean main_isRunning = false;
    public AlphaAnimation showWindowBackAnimation;
    public Animation exitAnimation;
    public RotateAnimation rotateAnimation;
    //用于将发布按钮控件旋转回到原位置
    public RotateAnimation rotateAnimation_exit;
    //用于动画集合
    public AnimationSet animationSet;
    public AnimationSet animationSet_two;

    //用于退出动画的集合
    public AnimationSet animationSet_exit;
    public AnimationSet animationSet_exit_two;

    public View leftView ;
    public View rightView ;

    //开始提交发布,针对不是新手用户
    public static final int START_WRITEHELP_INFO = 1;
    //开始新手发布,针对是新手用户
    public static final int START_NEWUSER_WRITEHELP_INFO = 2;
    //关闭刷新控件
    public static final int CLOSE_REFRESH = 3 ;
    //启动让用户选择下载的popupwindow
    public static final int OPEN_THINKE_DOWNLOAD = 4;
    //需要进行强制性更新操作
    public static final int OPEN_UPDATE = 5;

    //Appbar的高度
    public int appBarHeight ;
    //appBar隐藏高度
    public int verticalOffset;



    //用于判断当前是否已经请求过客户端初始化了
    public static  boolean ISREQUEST = false;
    //存储从服务器初始化下来的数据
    public InitData initData;

    public View windowBack2;

    public boolean isRequestInitInfo = false;

    public String testToken = "ew0KImdyYWRlIjogIjEiLA0KIk1hYyI6" +
            "ICI1NWI0ZjllMzEyNTc0ZmFiMWMwYTc3MTc0NjllNzM0OC" +
            "IsDQoic3RhcnRfdGltZSI6ICIxNDk1MDIyNjgzIiwNCiJpcC" +
            "I6ICIxOTIuMTY4LjQzLjc0IiwNCiJzaWduYXR1cmVfc2VydmV" +
            "yIjogImh0dHA6Ly9iYW5nYmFuZ3NoZW5naHVvc2VydmVyMDEu" +
            "d2Vic2l0ZSINCn0=.f325d089682d21f236acfe4b2bf57021a" +
            "bd110466e3dd79ae3815ea60bdfe684";
    //测试用的Url地址，后面将不会使用这样的Url

    public PopupWindow downloadPop;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = LayoutInflater.from(getActivity()).inflate(R.layout.activity_main_fragment,container,false);
        initData();
        return root;
    }

    /**
     * 用于初始化所有控件的方法
     */
    public void initData(){
        open_orderInfo = (ImageView)root.findViewById(R.id.open_userSetting);

        open_userSetting = (ImageView)root.findViewById(R.id.open_order_info);

        bottom_item1 = (ImageView)root.findViewById(R.id.icon_item1);

        bottom_item2 = (ImageView)root.findViewById(R.id.icon_item2);

        bottom_item3 = (ImageView)root.findViewById(R.id.icon_item3);

        mainActivity = (MainActivity) getActivity();

        windowBack = root.findViewById(R.id.windowBack);

        quickImageView = (ImageView) root.findViewById(R.id.quick_write);

        writeImageView = (ImageView) root.findViewById(R.id.write_info);

        quickText = (TextView) root.findViewById(R.id.textInfo_quick);

        infoCommit = (TextView) root.findViewById(R.id.textInfo_commit);

        leftView = root.findViewById(R.id.lisenter1);

        rightView = root.findViewById(R.id.lisenter2);

        initArticles = new ArrayList<>();

        initArticleAdapter = new InitArticleAdapter(getContext(),initArticles);

        initDateRecycler = (RecyclerView) root.findViewById(R.id.infoRecycler);

        initDateRecycler.addOnScrollListener(new OnRecyclerListener(root,getActivity(),initArticles));

        initDateRecycler.addItemDecoration(new RecyclerItemDecoration());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);

        initDateRecycler.setLayoutManager(linearLayoutManager);

        initDateRecycler.setAdapter(initArticleAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.refreshInfo);

        swipeRefreshLayout.setColorSchemeColors(MyApplication.Color.PRIMARYCOLOR);

        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener(mainActivity,initArticles));
        if(Build.VERSION.SDK_INT>=21){
            //设置appBar的paddingTop
            root.findViewById(R.id.appBar).setPadding(0,Utils.getStatusHeight(),0,0);
        }

        //初始化所有动画
        List<Animation> animations = AnimationUtil.initAnimation(Animation.RELATIVE_TO_SELF,0f,1f,0f,45f,0.5f,0.5f);

        showWindowBackAnimation = (AlphaAnimation) animations.get(0);

        exitAnimation = animations.get(1);

        rotateAnimation = (RotateAnimation) animations.get(2);

        rotateAnimation_exit = (RotateAnimation) animations.get(3);

        showWindowBackAnimation.setAnimationListener(this);

        exitAnimation.setAnimationListener(this);

        //设置动画弹出快速编辑
        animationSet = AnimationUtil.startInfoAnimation(Animation.RELATIVE_TO_SELF,0f,-1f,0f,-1.5f,1f,1.3f,1f,1.3f,0.5f,0.5f);

        //设置动画弹出新手编辑
        animationSet_two = AnimationUtil.startInfoAnimation(Animation.RELATIVE_TO_SELF,0f,1f,0f,-1.5f,1f,1.3f,1f,1.3f,0.5f,0.5f);

        animationSet_two.setStartOffset(SHORT_ANIMATION-200);

       //设置动画退出快速编辑
        animationSet_exit =  AnimationUtil.setExitAnimation(Animation.RELATIVE_TO_SELF,-1f,0,-1.5f,0f,1.3f,1f,1.3f,1f,0.5f,0.5f);

       //设置动画退出新手编辑
        animationSet_exit_two = AnimationUtil.setExitAnimation(Animation.RELATIVE_TO_SELF,1f,0f,-1.5f,0f,1.3f,1f,1.3f,1f,0.5f,0.5f);

        //获取windowBack2
        windowBack2 = root.findViewById(R.id.windowBack2);

        //获取显示网络异常的layout
        shownoNetWork = (LinearLayout) root.findViewById(R.id.show_no_network);
        //获取显示服务器没有文章的layout
        showServerNoContent = (LinearLayout) root.findViewById(R.id.show_no_data);

        //异步消息处理类
        handlerInfo = new HandlerInfo();

        windowBack.setOnClickListener(this);
        open_userSetting.setOnClickListener(this);
        open_orderInfo.setOnClickListener(this);
        bottom_item1.setOnClickListener(this);
        bottom_item2.setOnClickListener(this);
        bottom_item3.setOnClickListener(this);
        leftView.setOnClickListener(this);
        rightView.setOnClickListener(this);
        AppBarLayout appBarLayout = (AppBarLayout) root.findViewById(R.id.appBar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(verticalOffset == -(appBarLayout.getHeight()-MyApplication.Info.STATUS_HEIGHT)){
                    if(Build.VERSION.SDK_INT>=21){
                        getActivity().getWindow().setStatusBarColor(MyApplication.Color.THEMECOLOR);
                        MainFragment.this.verticalOffset = verticalOffset;
                    }
                }else{
                    if(Build.VERSION.SDK_INT>=21){
                        getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
                    }
                    MainFragment.this.verticalOffset = verticalOffset;
                }
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        if(!ISREQUEST){
            swipeRefreshLayout.setRefreshing(true);
            HttpRequest.request("http://192.168.43.67/v1/articles/getarticle/0/10", new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ISREQUEST = true;
                    Message message = new Message();
                    message.obj = "请检查设备是否联网";
                    message.what = CLOSE_REFRESH;
                    handlerInfo.sendMessage(message);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ISREQUEST = true;
                    String result = response.body().string();
                    if(!result.equals("0")){
                        Gson gson = new Gson();
                        List<InitArticle> initArticles = gson.fromJson(result,new TypeToken<List<InitArticle>>(){}.getType());
                        for(InitArticle initArticle : initArticles){
                            MainFragment.this.initArticles.add(initArticle);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //通知刷新RecyclerView
                                initArticleAdapter.notifyItemInserted(MainFragment.this.initArticles.size()-1);
                                swipeRefreshLayout.setRefreshing(false);
                                startRequestInit();
                            }
                        });
                    }else{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"服务器中没有数据",Toast.LENGTH_SHORT).show();
                                swipeRefreshLayout.setRefreshing(false);
                                showServerNoContent.setVisibility(View.VISIBLE);
                                startRequestInit();
                            }
                        });
                    }

                }
            });
        }
    }
    //该方法用于请求服务器的一些初始数据,判断版本号等.
    public void startRequestInit() {
        Log.d("Jam","startRequestInit");
        if(!isRequestInitInfo){
            HttpRequest.request("http://192.168.43.67/V1/INITIALIZATION/ewoiZ3JhZGUiOiAiMSIsCiJNYWMiOiAiYjIwN2UyYmVmNTN\n" +
                    "mYjA4NzA0YjVhMjUxOGQ3Y2Y4YmIiLAoic3RhcnRfdGltZSI6ICIxNDk3MjMyMTI3IiwKImlwIjogIjE5Mi4\n" +
                    "xNjguNDMuODUiLAoic2lnbmF0dXJlX3NlcnZlciI6ICJodHRwOi8vMTkyLjE2OC40My42NyIKfQ==.4e95\n" +
                    "b9796c21628cdbadf4ead488eea665d0446e176097bf32cb47678baf9d4e", new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    isRequestInitInfo = true;
                    Gson gson = new Gson();
                    initData = gson.fromJson(response.body().string(),InitData.class);
                    //从服务器里面拉取数据版本号将其格式解析出来
                    int version = Integer.parseInt(initData.version.split("\\.")[0]);
                    try {
                        //判断版本是否有过更新,如果又新的版本就提示用户升级
                        if(version>MyApplication.getContext().getPackageManager().getPackageInfo("com.example.administrator.lifehelp",0).versionCode){
                            //如果需要强制更新
                            if(initData.is_update==1){
                                handlerInfo.sendEmptyMessage(OPEN_UPDATE);
                            }else{
                                if(Utils.isUpdate()){
                                    handlerInfo.sendEmptyMessage(OPEN_THINKE_DOWNLOAD);
                                }
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //打开左边的fragment
            case R.id.open_userSetting:
                mainActivity.drawer.openDrawer(Gravity.LEFT);
                break;
            //打开右边的drawerLayout
            case R.id.open_order_info:
                mainActivity.drawer.openDrawer(Gravity.RIGHT);
                break;

            //点击最下边的第一项
            case R.id.icon_item1:
                bottom_item1.setImageResource(R.drawable.help_bottom_item1_selector);
                bottom_item3.setImageResource(R.drawable.help_speak);
                break;
            //点击最下边的第二项,就是用户点击发布请求
            case R.id.icon_item2:
                //开始动画,将主界面加上一层阴影效果,判断如果main_isRunning等于false那么主体动画没被启动
                if(!main_isRunning){
                    if(!isRunning){
                        if(verticalOffset==-(root.findViewById(R.id.appBar).getHeight()-MyApplication.Info.STATUS_HEIGHT)){
                            if(Build.VERSION.SDK_INT>=21)
                                getActivity().getWindow().setStatusBarColor(Color.BLACK);
                        }
                        //关闭滑动功能
                        Utils.setCloseDrawer(mainActivity.drawer);
                        main_isRunning = true;//主体动画已经启动
                        bottom_item2.setImageResource(R.drawable.help_info_add_selector);
                        windowBack.startAnimation(showWindowBackAnimation);
                        windowBack.setVisibility(View.VISIBLE);
                        leftView.setVisibility(View.VISIBLE);
                        rightView.setVisibility(View.VISIBLE);
                    }
                }else{
                    if(!isRunning){
                        exitAnimation();
                    }
                }
                break;
            //点击最下边的第三项
            case R.id.icon_item3:
                bottom_item1.setImageResource(R.drawable.help_bottom_item1);
                bottom_item3.setImageResource(R.drawable.help_speak_selector);
                break;
            //当用户点击了阴影部分退出选择模式
            case R.id.windowBack:
                if(!isRunning){
                    exitAnimation();
                }

                break;
            case R.id.lisenter1:
                //解决动画效果带来的一闪
                if(!isRunning){
                    exitAnimation2();
                    Intent startNewUserWriteHelpInfo = new Intent(getActivity(),WriteHelpInfo.class);
                    getActivity().startActivityForResult(startNewUserWriteHelpInfo,START_NEWUSER_WRITEHELP_INFO);
                    getActivity().overridePendingTransition(R.anim.start_activity_animation_alph,R.anim.exit_activity_animation_alph);
                }
                break;
            case R.id.lisenter2:
                if(!isRunning){
                    exitAnimation2();
                    Intent startNewUserWriteHelpInfo = new Intent(getActivity(),WriteHelpInfo.class);
                    getActivity().startActivityForResult(startNewUserWriteHelpInfo,START_NEWUSER_WRITEHELP_INFO);
                    getActivity().overridePendingTransition(R.anim.start_activity_animation_alph,R.anim.exit_activity_animation_alph);
                }
                break;
        }
    }

    public void exitAnimation(){
        //开启滑动功能
        Utils.setUnLockModeDrawer(mainActivity.drawer);
        main_isRunning = false;
        bottom_item2.startAnimation(rotateAnimation_exit);
        windowBack.startAnimation(exitAnimation);
        bottom_item2.setImageResource(R.drawable.help_info_add);
        writeImageView.startAnimation(animationSet_exit_two);
        quickImageView.startAnimation(animationSet_exit);
        windowBack.setVisibility(View.GONE);
        infoCommit.setVisibility(View.GONE);
        quickText.setVisibility(View.GONE);
        leftView.setVisibility(View.GONE);
        rightView.setVisibility(View.GONE);
    }

    public void exitAnimation2(){
        //开启滑动功能
        Utils.setUnLockModeDrawer(mainActivity.drawer);
        main_isRunning = false;
        infoCommit.setVisibility(View.GONE);
        quickText.setVisibility(View.GONE);
        leftView.setVisibility(View.GONE);
        rightView.setVisibility(View.GONE);
        bottom_item2.startAnimation(rotateAnimation_exit);
        windowBack.startAnimation(exitAnimation);
        bottom_item2.setImageResource(R.drawable.help_info_add);
        writeImageView.startAnimation(exitAnimation);
        quickImageView.startAnimation(exitAnimation);
        windowBack.setVisibility(View.GONE);
    }

    //动画开始
    @Override
    public void onAnimationStart(Animation animation) {
        isRunning = true;
    }
    //动画结束
    @Override
    public void onAnimationEnd(Animation animation) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isRunning = false;
            }
        }).start();
        if(main_isRunning){
            handlerInfo.sendEmptyMessage(START_ROATE);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            quickText.setVisibility(View.VISIBLE);
                        }
                    });
                }
            },200);
        }
    }
    //动画循环
    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    public class HandlerInfo extends Handler{
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what){
                case START_ROATE:
                    bottom_item2.startAnimation(rotateAnimation);
                    quickImageView.startAnimation(animationSet);
                    quickImageView.setVisibility(View.VISIBLE);
                    writeImageView.startAnimation(animationSet_two);
                    writeImageView.setVisibility(View.VISIBLE);
                    infoCommit.setVisibility(View.VISIBLE);
                    break;
                case EXIT_ANIMATION:
                    exitAnimation();
                    break;
                case CLOSE_REFRESH:
                    swipeRefreshLayout.setRefreshing(false);
                    if(msg.obj!=null){
                        Toast.makeText(getActivity(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    }
                    shownoNetWork.setVisibility(View.VISIBLE);
                    break;
                case OPEN_THINKE_DOWNLOAD:
                    windowBack2.setVisibility(View.VISIBLE);
                    windowBack2.startAnimation(AnimationUtils.loadAnimation(mainActivity,R.anim.start_activity_animation_alph));
                    Utils.openDownloadPopupWindow(root, MainFragment.this, null, null, getActivity(), new Utils.Callback() {
                        @Override
                        public void onClickDownload() {
                            if(ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(mainActivity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},DownloadService.ThinkeUpdate);
                            }else{
                                mainActivity.startDownload(initData.apk_link, DownloadService.ThinkeUpdate);
                            }
                        }
                    });
                    break;
                case OPEN_UPDATE:
                    windowBack2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //拦截屏幕的点击事件,此处不做任何逻辑代码操作
                        }
                    });
                    Utils.setCloseDrawer(mainActivity.drawer);//关闭drawer滑动
                    windowBack2.setVisibility(View.VISIBLE);
                    windowBack2.startAnimation(AnimationUtils.loadAnimation(mainActivity,R.anim.start_activity_animation_alph));
                    downloadPop = new PopupWindow();
                    View thinkeRoot = LayoutInflater.from(getActivity()).inflate(R.layout.download_think_layout,null,false);
                    thinkeRoot.findViewById(R.id.download_dimsses).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mainActivity.finish();
                        }
                    });
                    thinkeRoot.findViewById(R.id.download_sure).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            downloadPop.dismiss();
                            View root = LayoutInflater.from(getContext()).inflate(R.layout.update_start,null,false);
                            downloadPop.setContentView(root);
                            downloadPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                                @Override
                                public void onDismiss() {
                                    mainActivity.downloadRunning = false;
                                    windowBack2.setVisibility(View.GONE);
                                    windowBack2.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.exit_activity_animation_alph));
                                    windowBack2.setOnClickListener(null);
                                    Utils.setUnLockModeDrawer(mainActivity.drawer);//解锁滑动
                                }
                            });
                            downloadPop.showAtLocation(root,Gravity.CENTER,0,0);
                            if(ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(mainActivity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},DownloadService.MandatoryUpdate);
                            }else{
                                mainActivity.startDownload(initData.apk_link, DownloadService.MandatoryUpdate);
                            }
                        }
                    });
                    downloadPop.setContentView(thinkeRoot);
                    downloadPop.setBackgroundDrawable(new BitmapDrawable());
                    downloadPop.setTouchable(true);
                    mainActivity.downloadRunning = true;
                    downloadPop.setWidth(Utils.dip2px(getContext(),300));
                    downloadPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    downloadPop.showAtLocation(root,Gravity.CENTER,0,0);
                    break;

            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ISREQUEST = false;
    }
}
