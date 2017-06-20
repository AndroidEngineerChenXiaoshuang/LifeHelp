package com.example.administrator.lifehelp.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.example.administrator.lifehelp.R;
import com.example.administrator.lifehelp.application.MyApplication;
import com.example.administrator.lifehelp.lifefragment.MainFragment;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 设置顶部color
 */

public class Utils {

    public static PopupWindow popupWindow;
    public static PopupWindow downloadPopupWindow;
    public static AlphaAnimation start_alphaAnimation;
    public static AlphaAnimation exit_alphaAnimation;
    public static final String DOWNLOAD_SERVICE = "com.example.administrator.lifehelp.DownloadService";

    static{
        start_alphaAnimation = new AlphaAnimation(0,1);
        exit_alphaAnimation = new AlphaAnimation(1,0);
        popupWindow = new PopupWindow();
        start_alphaAnimation.setDuration(200);
        start_alphaAnimation.setFillAfter(true);
        exit_alphaAnimation.setDuration(200);
        //初始化downloadPopupWindow

    }

    public static void setPrimary(int Color, Activity activity){
        if(Build.VERSION.SDK_INT>=21){
            activity.getWindow().setStatusBarColor(Color);
        }
    }

    //设置drawerLayout的模式为锁定模式:不能滑动
    public static void setCloseDrawer(DrawerLayout drawer){
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    //设置draweLayout为解锁模式:可以滑动
    public static void setUnLockModeDrawer(DrawerLayout drawer){
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public static void openWindowManager(View root, View parent, final View back, int grvity,int width, int height,int animationStyle){
        back.setVisibility(View.VISIBLE);
        if(height==ViewGroup.LayoutParams.WRAP_CONTENT){
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }else{
            popupWindow.setHeight(dip2px(MyApplication.getContext(),height));
        }
        popupWindow.setWidth(width);
        popupWindow.setContentView(root);
        popupWindow.setAnimationStyle(animationStyle);
        popupWindow.showAtLocation(parent, grvity,0,0);
        back.startAnimation(start_alphaAnimation);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                back.startAnimation(exit_alphaAnimation);
                back.setVisibility(View.GONE);
            }
        });
    }

    public interface Callback{
        void onClickDownload();
    }

    public static void openDownloadPopupWindow(View parent, final MainFragment mainFragment, String title, String content, final Activity activity, final Callback callback){
        downloadPopupWindow = new PopupWindow();
        View root = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.download_think_layout, (ViewGroup) parent,false);
        downloadPopupWindow.setContentView(root);
        downloadPopupWindow.setWidth(dip2px(MyApplication.getContext(),300));
        downloadPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        downloadPopupWindow.setAnimationStyle(R.style.StartDownloadAnimation);
        //点击确定下载
        root.findViewById(R.id.download_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClickDownload();
                downloadPopupWindow.dismiss();
            }
        });
        //点击取消下载
        root.findViewById(R.id.download_dimsses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPopupWindow.dismiss();
            }
        });
        //添加取消事件监听
        downloadPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(mainFragment!=null){
                    mainFragment.mainActivity.windowBack2.startAnimation(AnimationUtils.loadAnimation(activity,R.anim.exit_activity_animation_alph));
                    mainFragment.mainActivity.windowBack2.setVisibility(View.GONE);
                }else{
                    throw new NullPointerException("mainFragment is not null Reason code need MainFragment.WindowBack2");
                }
            }
        });
        downloadPopupWindow.setOutsideTouchable(true);
        downloadPopupWindow.setFocusable(true);
        downloadPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        downloadPopupWindow.showAtLocation(parent, Gravity.CENTER,0,0);
    }



    public static void setStatusBarColor(int color,Activity activity){
        if(Build.VERSION.SDK_INT>=21){
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            activity.getWindow().setStatusBarColor(color);
        }
    }

    public static void closeWindowManager(){
        popupWindow.dismiss();
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     *  获取状态栏的高度
     */
    public static int getStatusHeight(){
        //获取到资源id
        int identifierId = MyApplication.getContext().getResources().getIdentifier("status_bar_height","dimen","android");
        int statusHeight;
        if(identifierId>1){
            //根据资源id来获取指定资源高度
            statusHeight = MyApplication.getContext().getResources().getDimensionPixelSize(identifierId);
            return statusHeight;
        }
        return 0;
    }

    /**
     * 该方法用于判断当前客户端距离上次更新的时间做判断是否提示用户更新
     */
    public static boolean isUpdate(){
        SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences(MyApplication.Type.SHARED_USER_LIKE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long lastUpdate = sharedPreferences.getLong(MyApplication.Type.ISUPDATE_TIMER,0L);
        if(lastUpdate==0){
            editor.putLong(MyApplication.Type.ISUPDATE_TIMER,System.currentTimeMillis());
            editor.apply();
            return true;
        }
        long nowUpdate = System.currentTimeMillis();
        long oneDayTimer = 86400000;
        //如果大于一天的时间
        if((nowUpdate-oneDayTimer)>=lastUpdate){
            editor.putLong(MyApplication.Type.ISUPDATE_TIMER,nowUpdate);
            editor.apply();
            return true;
        }
        return false;
    }


    public static void setToastDuration(final int duration, final Toast toast, final Activity activity){
         if(toast!=null&&duration>0){
             final Timer timer = new Timer();
             timer.schedule(new TimerTask() {
                 @Override
                 public void run() {
                     activity.runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             toast.show();
                         }
                     });
                 }
             },0,3000);
             new Timer().schedule(new TimerTask() {
                 @Override
                 public void run() {
                     timer.cancel();
                     toast.cancel();
                 }
             },3000);
         }
    }

    //获取手机id,该id只针对我们应用起效,为了区别唯一性
    public static String getPhoneId(){
        char[] chs = {'A','B','C','D','E','F','G','H','I','J'};
        String result = "";
        String radom_values = String.valueOf(radom(100000000,1000000000));
        String time = String.valueOf(System.currentTimeMillis());
        for(int i=0;i<time.length();i++){
            if(i<radom_values.length()){
                int index = Integer.parseInt(radom_values.substring(i,i+1));
                result+=time.substring(i,i+1);
                result+=chs[index];
            }else if(i>=radom_values.length()){
                int index = new Random().nextInt(10);
                result+=time.substring(i,i+1);
                result+=chs[index];
            }
        }
        return result;
    }

    //获取到指定范围的随机数
    public static int radom(int min,int max){
        return (int)(Math.random()*(max-min)+min);
    }

    //将字符串转换为纯数字手机号
    public static String getPhoneNumber(String userPhone) {
        String userPhoneNumber;
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(userPhone);
        userPhoneNumber = m.replaceAll("").trim();
        return userPhoneNumber;
    }

    //将传来的字符串转换成Bitmap类型
    public static Bitmap getStringToBitmap(String verifyImg) {
        Bitmap bitmap = null;
        try {
            byte[]bitmapArray = Base64.decode(verifyImg, Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
