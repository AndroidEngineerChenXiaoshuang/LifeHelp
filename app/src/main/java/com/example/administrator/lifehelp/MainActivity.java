package com.example.administrator.lifehelp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.lifehelp.IMapplication.view.UserinterfaceFragment;
import com.example.administrator.lifehelp.application.MyApplication;
import com.example.administrator.lifehelp.db.UserInfo;
import com.example.administrator.lifehelp.lifefragment.MainFragment;
import com.example.administrator.lifehelp.service.DownloadService;
import com.example.administrator.lifehelp.util.AnimationUtil;
import com.example.administrator.lifehelp.util.PopupWindowUtil;
import com.example.administrator.lifehelp.util.SetFouchable;
import com.example.administrator.lifehelp.util.Utils;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;
import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 该活动为主活动,绑定的view就是主界面,里面包含了主界面的任何一切操作
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,Animation.AnimationListener {

    //该booealn类型用于判断Drawer是否打开,打开时为true,关闭为false,该boolean类型是针对左边的侧滑
    public boolean isLogin = false;
    public boolean isOpenDrawer = false;
    public boolean isOpenDrawerOfRight = false;
    public PopupWindowUtil popupWindowUtil;
    //主界面drawerLayout控件
    public DrawerLayout drawer;
    //Design库中的控件,位于界面左边的布局
    public NavigationView rightLayout;
    //fragment后面的阴影
    public View isShow;
    public FragmentManager fragmentManager;
    public MainFragment mainFragment;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Toast toast;
    //用于管理是否退出
    public boolean isClose;
    //用于控制判断是否打开了下载管理器
    public boolean downloadRunning = false;

    //动画主体是否打开
    public boolean main_isRunning = false;
    //动画是否正在运行
    public boolean isRunning = false;
    //底部第一项按钮,不是实际按钮,而是一个imageView
    public ImageView bottom_item1;
    //底部第一项按钮,不是实际按钮,而是二个imageView
    public ImageView bottom_item2;
    //底部第一项按钮,不是实际按钮,而是三个imageView
    public ImageView bottom_item3;
    //window动画的背景颜色,用来显示遮挡效果
    public View windowBack;

    public View windowBack2;

    public View leftView;

    public View rightView;

    //imageView 快速发布
    public ImageView quickImageView;

    //imageView 发布
    public ImageView writeImageView;

    //开始提交发布,针对不是新手用户
    public static final int START_WRITEHELP_INFO = 1;

    //开始新手发布,针对是新手用户
    public static final int START_NEWUSER_WRITEHELP_INFO = 2;

    //用于handler的what区别
    public static final int START_ROATE = 1;

    public static final int EXIT_ANIMATION = 2;


    public AlphaAnimation showWindowBackAnimation;

    public RotateAnimation rotateAnimation;

    //用于将发布按钮控件旋转回到原位置
    public RotateAnimation rotateAnimation_exit;

    public Animation exitAnimation;

    //用于动画集合
    public AnimationSet animationSet;
    public AnimationSet animationSet_two;

    //用于退出动画的集合
    public AnimationSet animationSet_exit;
    public AnimationSet animationSet_exit_two;

    public TextView quickText;
    public TextView infoCommit;

    public HandlerInfo handlerInfo;

    public UserinterfaceFragment userinterfaceFragment;

    /**
     * framgnet的状态，默认为1
     * 1表示此碎片正处于mainFragment
     * 2表示此碎片正处于用户消息
     */
    public int fragmentstate = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBarColor(Color.TRANSPARENT, this);
        setContentView(R.layout.activity_main);
        initData();
    }
    /**
     * 用于初始化所有控件的方法
     */
    public void initData() {
        drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.setScrimColor(MyApplication.Color.DRAWERBACK);
        rightLayout = (NavigationView) findViewById(R.id.help_navView);
        //让navView显示的图片用图片本身的颜色
        rightLayout.setItemIconTintList(null);
        //创建数据库
        Connector.getReadableDatabase();
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, 0, 0) {
            //当打开了抽屉
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                isOpenDrawer = true;
            }

            //当关闭了抽屉
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                isOpenDrawer = false;
                SetFouchable.setFouch(drawer);
            }
        };
        setMainFragment();
        drawer.addDrawerListener(actionBarDrawerToggle);
        if (Build.VERSION.SDK_INT >= 21) {
            //添加paddingTop
            rightLayout.setPadding(0, Utils.getStatusHeight(), 0, 0);
        }
        //注册相应的点击事件
        rightLayout.setNavigationItemSelectedListener(this);
        //为rightLayout的每个子项设置背景颜色
        rightLayout.setItemBackgroundResource(R.drawable.help_class_selector);
        //初始化shared为一
        sharedPreferences = getSharedPreferences(MyApplication.Type.SHARED_USER_LIKE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(MyApplication.Type.SHARED_USER_HELPCLASS_SELECTOR, 0);
        editor.apply();
        toast = Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_LONG);
        bottom_item1 = (ImageView) findViewById(R.id.icon_item1);

        bottom_item2 = (ImageView) findViewById(R.id.icon_item2);

        bottom_item3 = (ImageView) findViewById(R.id.icon_item3);

        bottom_item1.setOnClickListener(this);

        bottom_item2.setOnClickListener(this);

        bottom_item3.setOnClickListener(this);

        windowBack = findViewById(R.id.windowBack);

        windowBack.setOnClickListener(this);

        windowBack2 = findViewById(R.id.windowBack2);

        leftView = findViewById(R.id.lisenter1);

        rightView = findViewById(R.id.lisenter2);

        leftView.setOnClickListener(this);

        rightView.setOnClickListener(this);

        quickImageView = (ImageView) findViewById(R.id.quick_write);

        writeImageView = (ImageView) findViewById(R.id.write_info);

        //初始化所有动画
        List<Animation> animations = AnimationUtil.initAnimation(Animation.RELATIVE_TO_SELF, 0f, 1f, 0f, 45f, 0.5f, 0.5f);

        showWindowBackAnimation = (AlphaAnimation) animations.get(0);

        showWindowBackAnimation.setAnimationListener(this);

        exitAnimation = animations.get(1);

        rotateAnimation = (RotateAnimation) animations.get(2);

        rotateAnimation_exit = (RotateAnimation) animations.get(3);

        exitAnimation.setAnimationListener(this);

        //设置动画弹出快速编辑
        animationSet = AnimationUtil.startInfoAnimation(Animation.RELATIVE_TO_SELF, 0f, -1f, 0f, -1.5f, 1f, 1.3f, 1f, 1.3f, 0.5f, 0.5f);
        //设置动画退出快速编辑
        animationSet_exit = AnimationUtil.setExitAnimation(Animation.RELATIVE_TO_SELF, -1f, 0, -1.5f, 0f, 1.3f, 1f, 1.3f, 1f, 0.5f, 0.5f);

        //设置动画弹出新手编辑
        animationSet_two = AnimationUtil.startInfoAnimation(Animation.RELATIVE_TO_SELF, 0f, 1f, 0f, -1.5f, 1f, 1.3f, 1f, 1.3f, 0.5f, 0.5f);

        animationSet_two.setStartOffset(300 - 200);

        //设置动画退出编辑
        animationSet_exit_two = AnimationUtil.setExitAnimation(Animation.RELATIVE_TO_SELF, 1f, 0f, -1.5f, 0f, 1.3f, 1f, 1.3f, 1f, 0.5f, 0.5f);

        quickText = (TextView) findViewById(R.id.textInfo_quick);

        infoCommit = (TextView) findViewById(R.id.textInfo_commit);

        handlerInfo = new HandlerInfo();

        findViewById(R.id.bottom).setOnClickListener(this);


    }

    //添加主界面的fragment
    private void setMainFragment() {
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
            mainFragment = new MainFragment();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.mainFragment, mainFragment);
        transaction.commit();
    }

    private void setIMFragment() {
    }

    /**
     * 当用户点击了back键,该函数用在主活动里面,
     * 当推栈里面没有其它活动了,用户点击了back键会询问用户是否退出
     */
    @Override
    public void onBackPressed() {
        if (downloadRunning) {
            //拦截onBack事件不做任何判断
        } else if (main_isRunning) {
            if (!isRunning) {
                exitAnimation();
            }
        } else {
            if (!isOpenDrawerOfRight) {
                if (!isOpenDrawer) {
                    //证明用户第一次点击处于栈顶,并且第一次或者状态消灭时点击
                    if (!isClose) {
                        isClose = true;
                        Utils.setToastDuration(3000, toast, this);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                isClose = false;
                            }
                        }, 3500);

                    } else {
                        toast.cancel();
                        finish();
                    }
                } else {
                    drawer.closeDrawers();
                }
            } else {
                getSupportFragmentManager().popBackStack();
                isOpenDrawerOfRight = false;
                AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
                AnimationUtil.setAccelerateInterpolator(isShow, alphaAnimation, MyApplication.Type.ALPHANIMATION, 200);
                isShow.setVisibility(View.GONE);
            }
        }


    }

    public void setOpenDrawerOfRight(boolean isOpenDrawerOfRight, View isShow) {
        this.isOpenDrawerOfRight = isOpenDrawerOfRight;
        this.isShow = isShow;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //当用户点击我的钱包
            case R.id.help_wallet:
                List<UserInfo> userInfo = DataSupport.findAll(UserInfo.class);
                for (UserInfo user : userInfo){
                    Log.i("jsone", "onNavigationItemSelected: ");
                    if (user.getUsername() != null){
                        isLogin = true;
                        Log.i("jsone", "onCreate: " + user.getUsername());
                        Intent intent = new Intent(this,MyWallet.class);
                        startActivity(intent);
                    }
                }
                if (!isLogin){
                    windowBack2.setVisibility(View.VISIBLE);
                    AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(this,R.anim.show_window_back);
                    windowBack2.startAnimation(alphaAnimation);
                    PopupWindowUtil.showPopupwindow(MainActivity.this,1);
                    closeDrawer();
                }
                break;
            //当用户点击我帮助的订单
            case R.id.help_myself:
                closeDrawer();
                break;
            //当用户点击我的礼包
            case R.id.help_thing:
                closeDrawer();
                break;
            //当用户点击设置
            case R.id.help_setting:
                closeDrawer();
                break;
            //当用户点击发布的订单
            case R.id.help_Release:
                closeDrawer();
                break;
            //当用户点击个人认证
            case R.id.help_Personal_proof:
                Intent intent = new Intent(this, PersonalCertification.class);
                startActivity(intent);
                break;

        }
        return false;
    }


    //给fragment提供相应的drawer关闭
    public void closeDrawer() {
        drawer.closeDrawers();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case DownloadService.ThinkeUpdate:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startDownload(mainFragment.initData.apk_link, DownloadService.ThinkeUpdate);
                    }
                }
                break;
            case DownloadService.MandatoryUpdate:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startDownload(mainFragment.initData.apk_link, DownloadService.MandatoryUpdate);
                    }
                }
                break;
        }
    }

    public void startDownload(final String url, final int type) {
        //设置当完成下载后通知给界面的回调
        DownloadService.setOkDownload(new DownloadService.finishDownload() {
            @Override
            public void finish(final File file) {
                if (type == DownloadService.MandatoryUpdate) {
                    TextView info = (TextView) mainFragment.downloadPop.getContentView().findViewById(R.id.downloadInfo);
                    TextView progress = (TextView) mainFragment.downloadPop.getContentView().findViewById(R.id.progress);
                    progress.setText(100 + "%");
                    info.setText("下载完成,点击安装最新客户端!");
                    mainFragment.downloadPop.getContentView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            installApplication(file);
                        }
                    });
                }
                stopService(new Intent(MainActivity.this, DownloadService.class));
                installApplication(file);
            }
        });
        if (type == DownloadService.ThinkeUpdate) {
            Intent intent = new Intent(MainActivity.this, DownloadService.class);
            intent.putExtra("DownloadUrl", url);
            intent.putExtra("DownloadType", DownloadService.ThinkeUpdate);
            startService(intent);
        } else if (type == DownloadService.MandatoryUpdate) {
            Intent intent = new Intent(MainActivity.this, DownloadService.class);
            intent.putExtra("DownloadUrl", url);
            intent.putExtra("DownloadType", DownloadService.MandatoryUpdate);
            DownloadService.setOnUpdateProgress(new DownloadService.onUpdate() {
                @Override
                public void setPorgress(int progress) {
                    TextView downloadProgress = (TextView) mainFragment.downloadPop.getContentView().findViewById(R.id.progress);
                    downloadProgress.setText(progress + "%");
                }

                @Override
                public void onFailure() {
                    mainFragment.downloadPop.dismiss();
                }
            });
            startService(intent);
        }

    }

    private void installApplication(File file) {
        Intent intent = new Intent();
        /**
         * 之所以这里加入版本判断,是因为从android7.0开始如果直接
         * 返回给文件的uri系统是认为不安全的,而使用fileProvider它
         * 将给我们封装好了的uri给我们,fileProvider是一种特殊的内
         * 容提供器和内容提供器有着相同的机制保护数据,它可以选择性的
         * 将封装好了的uri共享给外部,从而提升数据的安全
         */
        if (Build.VERSION.SDK_INT >= 24) {
            Uri uri = FileProvider.getUriForFile(MainActivity.this, "com.example.administrator.lifehelp.fileProvider", file);
            intent.setData(uri);
        } else {
            intent.setData(Uri.fromFile(file));
        }
        //如果设置该权限,意图接受方将获取数据的读取操作权限
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击最下边的第一项
            case R.id.icon_item1:
                if(fragmentstate !=1){
                    bottom_item1.setImageResource(R.drawable.help_bottom_item1_selector);
                    bottom_item3.setImageResource(R.drawable.help_speak);
                    if (mainFragment == null) {
                        mainFragment = new MainFragment();
                    }
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.mainFragment, mainFragment);
                    fragmentTransaction.commit();
                    fragmentstate = 1;
                }
                break;
            //点击最下边的第三项
            //点击最下边的第二项,就是用户点击发布请求
            case R.id.icon_item2:
                //开始动画,将主界面加上一层阴影效果,判断如果main_isRunning等于false那么主体动画没被启动
                if (!main_isRunning) {
                    if (!isRunning) {
                        if (mainFragment.verticalOffset == -(findViewById(R.id.appBar).getHeight() - MyApplication.Info.STATUS_HEIGHT)) {
                            if (Build.VERSION.SDK_INT >= 21)
                                getWindow().setStatusBarColor(Color.BLACK);
                        }
                        //关闭滑动功能
                        Utils.setCloseDrawer(drawer);
                        main_isRunning = true;//主体动画已经启动
                        bottom_item2.setImageResource(R.drawable.help_info_add_selector);
                        windowBack.startAnimation(showWindowBackAnimation);
                        windowBack.setVisibility(View.VISIBLE);
                        leftView.setVisibility(View.VISIBLE);
                        rightView.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (!isRunning) {
                        exitAnimation();
                    }
                }
                break;
            case R.id.icon_item3:
                if(fragmentstate!=2){
                    bottom_item1.setImageResource(R.drawable.help_bottom_item1);
                    bottom_item3.setImageResource(R.drawable.help_speak_selector);
                    if(userinterfaceFragment==null){
                        userinterfaceFragment = new UserinterfaceFragment();
                    }
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.add(R.id.mainFragment,userinterfaceFragment);
                    transaction.commit();
                    fragmentstate = 2;
                }

                break;
            //当用户点击了阴影部分退出选择模式
            case R.id.windowBack:
                if (!isRunning) {
                    exitAnimation();
                }
                break;
            case R.id.lisenter1:
                //解决动画效果带来的一闪
                if (!isRunning) {
                    exitAnimation2();
                    Intent startNewUserWriteHelpInfo = new Intent(this, WriteHelpInfo.class);
                    startActivityForResult(startNewUserWriteHelpInfo, START_WRITEHELP_INFO);
                    overridePendingTransition(R.anim.start_activity_animation_alph, R.anim.exit_activity_animation_alph);
                }
                break;
            case R.id.lisenter2:
                if (!isRunning) {
                    exitAnimation2();
                    Intent startNewUserWriteHelpInfo = new Intent(this, WriteHelpInfo.class);
                    startActivityForResult(startNewUserWriteHelpInfo, START_NEWUSER_WRITEHELP_INFO);
                    overridePendingTransition(R.anim.start_activity_animation_alph, R.anim.exit_activity_animation_alph);
                }
                break;
        }
    }

    public void exitAnimation() {
        //开启滑动功能
        Utils.setUnLockModeDrawer(drawer);
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

    public void exitAnimation2() {
        //开启滑动功能
        Utils.setUnLockModeDrawer(drawer);
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
        if (main_isRunning) {
            handlerInfo.sendEmptyMessage(START_ROATE);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            quickText.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }, 200);
        }
    }

    //动画循环
    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    public class HandlerInfo extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
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
            }
        }
    }
}
