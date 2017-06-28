package com.example.administrator.lifehelp.lifefragment;

import android.Manifest;
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
import android.util.Base64;
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
import com.example.administrator.lifehelp.MainActivity;
import com.example.administrator.lifehelp.R;
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
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 *  主碎片：布局在主界面之上的碎片
 */

public class MainFragment extends Fragment implements View.OnClickListener{

    //用于打开左边的侧滑
    public ImageView open_userSetting;
    //用于打开右边筛选的侧滑
    public ImageView open_orderInfo;
    //与fragment绑定的视图
    public View root;
    //关联fragment的Activity
    public MainActivity mainActivity;
    //该RecyclerView是我们的文章界面展示
    public RecyclerView initDateRecycler;
    //用于使用RecyclerView的adapter
    public InitArticleAdapter initArticleAdapter;
    public List<InitArticle> initArticles;
    //SwipeRefersh控件
    public SwipeRefreshLayout swipeRefreshLayout;

    public HandlerInfo handlerInfo;

    //是否显示有网络连接
    public LinearLayout shownoNetWork;
    //是否显示没有文章提示
    public LinearLayout showServerNoContent;


    /**
     * 动画类型
     * START_ROATE:表示开始进行添加按钮的旋转动画
     */

    //关闭刷新控件
    public static final int CLOSE_REFRESH = 3 ;
    //启动让用户选择下载的popupwindow
    public static final int OPEN_THINKE_DOWNLOAD = 4;
    //需要进行强制性更新操作
    public static final int OPEN_UPDATE = 5;
    //appBar隐藏高度
    public int verticalOffset;
    //用于判断当前是否已经请求过客户端初始化了
    public static  boolean ISREQUEST = false;
    //存储从服务器初始化下来的数据
    public InitData initData;

    public boolean isRequestInitInfo = false;

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

        mainActivity = (MainActivity) getActivity();

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
        //获取显示网络异常的layout
        shownoNetWork = (LinearLayout) root.findViewById(R.id.show_no_network);
        //获取显示服务器没有文章的layout
        showServerNoContent = (LinearLayout) root.findViewById(R.id.show_no_data);

        //异步消息处理类
        handlerInfo = new HandlerInfo();
        open_userSetting.setOnClickListener(this);
        open_orderInfo.setOnClickListener(this);
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
                    if(Build.VERSION.SDK_INT>=21&&getActivity().getWindow().getStatusBarColor()!=Color.TRANSPARENT){
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
            HttpRequest.request("http://192.168.43.67/v1/ArticleOperation/getarticle/0/10", new Callback() {
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
        HttpRequest.request("http://192.168.43.67/v1/clientControl/clientinitialization", new Callback() {
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
        }
    }

    public class HandlerInfo extends Handler{
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what){
                case CLOSE_REFRESH:
                    swipeRefreshLayout.setRefreshing(false);
                    if(msg.obj!=null){
                        Toast.makeText(getActivity(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    }
                    shownoNetWork.setVisibility(View.VISIBLE);
                    break;
                case OPEN_THINKE_DOWNLOAD:
                    mainActivity.windowBack2.setVisibility(View.VISIBLE);
                    mainActivity.windowBack2.startAnimation(AnimationUtils.loadAnimation(mainActivity,R.anim.start_activity_animation_alph));
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
                    mainActivity.windowBack2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //拦截屏幕的点击事件,此处不做任何逻辑代码操作
                        }
                    });
                    Utils.setCloseDrawer(mainActivity.drawer);//关闭drawer滑动
                    mainActivity.windowBack2.setVisibility(View.VISIBLE);
                    mainActivity.windowBack2.startAnimation(AnimationUtils.loadAnimation(mainActivity,R.anim.start_activity_animation_alph));
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
                                    mainActivity.windowBack2.setVisibility(View.GONE);
                                    mainActivity.windowBack2.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.exit_activity_animation_alph));
                                    mainActivity.windowBack2.setOnClickListener(null);
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
