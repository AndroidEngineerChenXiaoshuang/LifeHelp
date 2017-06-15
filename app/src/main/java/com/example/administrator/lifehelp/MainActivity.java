package com.example.administrator.lifehelp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import com.example.administrator.lifehelp.application.MyApplication;
import com.example.administrator.lifehelp.db.UserInfo;
import com.example.administrator.lifehelp.lifefragment.MainFragment;
import com.example.administrator.lifehelp.service.DownloadService;
import com.example.administrator.lifehelp.util.AnimationUtil;
import com.example.administrator.lifehelp.util.PopupWindowUtil;
import com.example.administrator.lifehelp.util.SetFouchable;
import com.example.administrator.lifehelp.util.Utils;
import com.tencent.TIMManager;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;
import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 该活动为主活动,绑定的view就是主界面,里面包含了主界面的任何一切操作
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

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
    public FragmentTransaction transaction;
    public MainFragment mainFragment;
    public SharedPreferences sharedPreferences ;
    public SharedPreferences.Editor editor;
    public Toast toast;
    //用于管理是否退出
    public boolean isClose;
    //用于控制判断是否打开了下载管理器
    public boolean downloadRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBarColor(Color.TRANSPARENT,this);
        setContentView(R.layout.activity_main);
        initData();
    }

    /**
     * 用于初始化所有控件的方法
    */
    public void initData(){
        drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.setScrimColor(MyApplication.Color.DRAWERBACK);
        rightLayout = (NavigationView) findViewById(R.id.help_navView);
        //让navView显示的图片用图片本身的颜色
        rightLayout.setItemIconTintList(null);
        //创建数据库
        Connector.getReadableDatabase();
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawer,0,0){
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

        drawer.setDrawerListener(actionBarDrawerToggle);
        if(Build.VERSION.SDK_INT>=21){
            //添加paddingTop
            rightLayout.setPadding(0,Utils.getStatusHeight(),0,0);
        }
        //注册相应的点击事件
        rightLayout.setNavigationItemSelectedListener(this);
        //为rightLayout的每个子项设置背景颜色
        rightLayout.setItemBackgroundResource(R.drawable.help_class_selector);
        //添加主界面的fragment
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        mainFragment = new MainFragment();
        transaction.add(R.id.mainFragment,mainFragment);
        transaction.commit();
        //初始化shared为一
        sharedPreferences = getSharedPreferences(MyApplication.Type.SHARED_USER_LIKE,MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(MyApplication.Type.SHARED_USER_HELPCLASS_SELECTOR,0);
        editor.apply();
        toast = Toast.makeText(MainActivity.this,"再按一次退出",Toast.LENGTH_LONG);
    }



    /**
     * 当用户点击了back键,该函数用在主活动里面,
     * 当推栈里面没有其它活动了,用户点击了back键会询问用户是否退出
     */
    @Override
    public void onBackPressed() {
        if(downloadRunning){
            //拦截onBack事件不做任何判断
        }else if(mainFragment.main_isRunning ){
            if(!mainFragment.isRunning){
                mainFragment.exitAnimation();
            }
        }else{
            if(!isOpenDrawerOfRight){
                if(!isOpenDrawer){
                    //证明用户第一次点击处于栈顶,并且第一次或者状态消灭时点击
                    if(!isClose){
                        isClose = true;
                        Utils.setToastDuration(3000,toast,this);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                               isClose = false;
                            }
                        },3500);

                    }else{
                        toast.cancel();
                        finish();
                    }
                }else {
                    drawer.closeDrawers();
                }
            }else{
                getSupportFragmentManager().popBackStack();
                isOpenDrawerOfRight = false;
                AlphaAnimation alphaAnimation = new AlphaAnimation(1,0);
                AnimationUtil.setAccelerateInterpolator(isShow,alphaAnimation,MyApplication.Type.ALPHANIMATION,200);
                isShow.setVisibility(View.GONE);
            }
        }


    }

    public void setOpenDrawerOfRight(boolean isOpenDrawerOfRight,View isShow){
        this.isOpenDrawerOfRight = isOpenDrawerOfRight;
        this.isShow = isShow;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //当用户点击我的钱包
            case R.id.help_wallet:
                List<UserInfo> userInfo = DataSupport.findAll(UserInfo.class);
                for (UserInfo user : userInfo){
                    if (user.getMessage() != null){
                        isLogin = true;
                        Log.i("jsone", "onCreate: " + user.getMessage());
                        closeDrawer();
                       Intent intent = new Intent(MainActivity.this,MyWallet.class);
                        startActivity(intent);
                    }
                }
                if (!isLogin){
                    mainFragment.windowBack2.setVisibility(View.VISIBLE);
                    AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(this,R.anim.show_window_back);
                    mainFragment.windowBack2.startAnimation(alphaAnimation);
                    popupWindowUtil = new PopupWindowUtil(MainActivity.this);
                    popupWindowUtil.show(1);
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
                Intent intent = new Intent(this,PersonalCertification.class);
                startActivity(intent);
                break;

        }

        return false;
    }


    //给fragment提供相应的drawer关闭
    public void closeDrawer(){
        drawer.closeDrawers();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case DownloadService.ThinkeUpdate:
                if(grantResults.length>0){
                    if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                        startDownload(mainFragment.initData.apk_link,DownloadService.ThinkeUpdate);
                    }
                }
                break;
            case DownloadService.MandatoryUpdate:
                if(grantResults.length>0){
                    if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                        startDownload(mainFragment.initData.apk_link,DownloadService.MandatoryUpdate);
                    }
                }
                break;
        }
    }

    public void startDownload(final String url, final int type){
        //设置当完成下载后通知给界面的回调
        DownloadService.setOkDownload(new DownloadService.finishDownload() {
            @Override
            public void finish(final File file) {
                if(type == DownloadService.MandatoryUpdate){
                    TextView info = (TextView) mainFragment.downloadPop.getContentView().findViewById(R.id.downloadInfo);
                    TextView progress = (TextView) mainFragment.downloadPop.getContentView().findViewById(R.id.progress);
                    progress.setText(100+"%");
                    info.setText("下载完成,点击安装最新客户端!");
                    mainFragment.downloadPop.getContentView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            installApplication(file);
                        }
                    });
                }
                stopService(new Intent(MainActivity.this,DownloadService.class));
                installApplication(file);
            }
        });
        if(type == DownloadService.ThinkeUpdate){
            Intent intent = new Intent(MainActivity.this, DownloadService.class);
            intent.putExtra("DownloadUrl",url);
            intent.putExtra("DownloadType",DownloadService.ThinkeUpdate);
            startService(intent);
        }else if(type == DownloadService.MandatoryUpdate){
            Intent intent = new Intent(MainActivity.this,DownloadService.class);
            intent.putExtra("DownloadUrl",url);
            intent.putExtra("DownloadType",DownloadService.MandatoryUpdate);
            DownloadService.setOnUpdateProgress(new DownloadService.onUpdate() {
                @Override
                public void setPorgress(int progress) {
                    TextView downloadProgress = (TextView) mainFragment.downloadPop.getContentView().findViewById(R.id.progress);
                    downloadProgress.setText(progress+"%");
                }

                @Override
                public void onFailure() {
                    mainFragment.downloadPop.dismiss();
                }
            });
            startService(intent);
        }

    }

    private void installApplication(File file){
        Intent intent = new Intent();
        /**
         * 之所以这里加入版本判断,是因为从android7.0开始如果直接
         * 返回给文件的uri系统是认为不安全的,而使用fileProvider它
         * 将给我们封装好了的uri给我们,fileProvider是一种特殊的内
         * 容提供器和内容提供器有着相同的机制保护数据,它可以选择性的
         * 将封装好了的uri共享给外部,从而提升数据的安全
         */
        if(Build.VERSION.SDK_INT>=24){
            Uri uri = FileProvider.getUriForFile(MainActivity.this,"com.example.administrator.lifehelp.fileProvider",file);
            intent.setData(uri);
        }else{
            intent.setData(Uri.fromFile(file));
        }
        //如果设置该权限,意图接受方将获取数据的读取操作权限
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
        startActivity(intent);
    }

}
