package com.example.administrator.lifehelp.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.example.administrator.lifehelp.R;

import org.litepal.LitePalApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.permission.EasyPermissions;
import cn.finalteam.galleryfinal.widget.GFImageView;

/**
 * 配置全局信息
 */

public class MyApplication extends Application{

    //获取全局Context
    public static Context mcontext ;
    @Override
    public void onCreate() {
        super.onCreate();
        mcontext = getApplicationContext();
        LitePalApplication.initialize(mcontext);
        /**
         * 配置多选单选的图片框架
         * ThemeConfig 配置框架的主题{
         *     setTitleBarTextColor :设置标题栏文本颜色
         *     setTitleBarBgColor :设置标题栏颜色
         *     setCheckSelectedColor :设置选中的颜色
         *     setFabNornalColor : floatingButton未选中的颜色
         * }
         * FunctionConfig 配置框架功能{
         *     setMutiSelectMaxSize :配置多选数量
         *     setEnableRotate :开启旋转功能
         *     setEnableCamera :开启相机功能
         * }
         *
         */
        ThemeConfig.Builder themebuilder = new ThemeConfig.Builder();
        themebuilder.setTitleBarTextColor(android.graphics.Color.WHITE);
        themebuilder.setTitleBarBgColor(Color.THEMECOLOR);
        themebuilder.setCheckSelectedColor(Color.THEMECOLOR);
        themebuilder.setFabNornalColor(Color.THEMECOLOR);
        themebuilder.setFabPressedColor(Color.FLOATING_COLOR_SELECTOR);

        FunctionConfig.Builder functionBuilder = new FunctionConfig.Builder();
        functionBuilder.setMutiSelectMaxSize(5);
        functionBuilder.setEnableRotate(false);
        functionBuilder.setEnableCamera(true);

        GlideImageLoader glideImageLoader = new GlideImageLoader();
        CoreConfig coreConfig = new CoreConfig.Builder(mcontext,glideImageLoader,themebuilder.build())
                .setFunctionConfig(functionBuilder.build())
                .build();
        GalleryFinal.init(coreConfig);
    }
    public static Context getContext(){
        return mcontext;
    }

    /**
     * 加载图片的一个类
     */
    public class GlideImageLoader implements ImageLoader {

        @Override
        public void displayImage(Activity activity, String path, final GFImageView imageView, Drawable defaultDrawable, int width, int height) {
            Glide.with(activity)
                    .load("file://"+path)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(new ImageViewTarget<GlideDrawable>(imageView) {
                        @Override
                        protected void setResource(GlideDrawable resource) {
                            imageView.setImageDrawable(resource);
                        }

                        @Override
                        public void setRequest(Request request) {

                        }

                        @Override
                        public Request getRequest() {
                            return super.getRequest();
                        }
                    });
        }

        @Override
        public void clearMemoryCache() {

        }
    }

    /**
     * 该类是用来在代码中需要用到的全局颜色管理类
     */
    public class Color{
        //打开drawerLayout后界面的背景颜色
        public static final int DRAWERBACK = 0x50000000;
        //基本的字体颜色
        public static final int FONTCOLOR = 0XFF595959;
        //跟主题很匹配的字体颜色
        public static final int THEMECOLOR = 0xFF02A1DF;
        //顶部状态栏的color颜色:系统主题默认颜色
        public static final int PRIMARYCOLOR = 0xFF02A1DF;
        //顶部状态栏的color颜色:黑色
        public static final int PRIMARYCOLOR_BLACK = 0XFF000000;
        //图片框架floatingButton选中的颜色
        public static final int FLOATING_COLOR_SELECTOR = 0xFF0078A7;

    }

    /**
     * 该类是用于管理项目中的类型,用于做不同的逻辑判断
     */
    public class Type{
        //用于主界面右边侧滑栏的listView控件
        public static final int MAINACTIVITY_RIGHT_LIST = 1;

        //动画类型:alph动画类型
        public static final int ALPHANIMATION = 1;

        //动画类型:roate动画类型
        public static final int ROATEANIMTION = 2;

        //保存着判断用户是否更新的时间戳
        public static final String ISUPDATE_TIMER = "is_update_timer";

        //用户偏好文件名
        public static final String SHARED_USER_LIKE = "user_shared";

        //偏好文件为user_shared中的一个节点名:用户选中的帮助类
        public static final String SHARED_USER_HELPCLASS_SELECTOR = "user_helpClass_selector";

        //存放临时token名称
        public static final String INFORMALTOKEN = "informalToken";

        //存放正式token名称
        public static final String FORMALTOKEN = "formalToken";

        //用于小时的adapter
        public static final int SETHOUR = 1;

        //用于分钟的adapter
        public static final int SETMINUTE = 2;

        //用于打开框架选择图片后返回的结果
        public static final int START_PHOTO_RESULT = 10000;


    }

    /**
     * 该类用于管理一些全局数据集
     */
    public static class Info{
        //用于保存帮助类的所有名称的数据集
        public static final String[] HELP_CLASS_NAMES = {"全部分类","宠物/宠物用品/代看","游戏账号/虚拟帮助","早中晚餐/夜宵"};
        //用于保存帮助类说有图片的资源id
        public static final int[] HELP_CLASS_IMAGES = {R.drawable.help_all,R.drawable.help_dog,R.drawable.help_foot,R.drawable.help_game};
    }

    /**
     * 关于lifeHelp请求的所有url地址
     */
    public static class ServerUrl{
        /**
         *  http://192.168.43.67/用于当前测试开发所使用等同于bangbangshenghuoserver01.website
         */
        public static final String LIFEHELP_SERVER_URL = "http://192.168.43.67/";
    }



}
