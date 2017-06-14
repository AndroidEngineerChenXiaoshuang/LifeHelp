package com.example.administrator.lifehelp.util;

import android.app.Activity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import com.example.administrator.lifehelp.R;
import com.example.administrator.lifehelp.application.MyApplication;
import java.util.ArrayList;
import java.util.List;

/**
 * 该类用于动画的工具类,用于提供开始动画,动画的速率和类型都可以由开发者自主调用
 */

public class AnimationUtil {


    //加速动画
    public static void setAccelerateInterpolator(View isShow, Animation animation,int animationType,int duration){
        switch (animationType){
            case MyApplication.Type.ALPHANIMATION:
                AlphaAnimation alphaAnimation = (AlphaAnimation) animation;
                alphaAnimation.setDuration(duration);
                AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
                alphaAnimation.setInterpolator(accelerateInterpolator);
                isShow.startAnimation(alphaAnimation);
                break;
            case MyApplication.Type.ROATEANIMTION:

                break;
        }
    }

    //减速动画
    public static void setDecelerateInterpolator(View isShow, Animation animation,int animationType,int duration){
        switch (animationType){
            case MyApplication.Type.ALPHANIMATION:
                AlphaAnimation alphaAnimation = (AlphaAnimation) animation;
                alphaAnimation.setDuration(duration);
                DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
                alphaAnimation.setInterpolator(decelerateInterpolator);
                isShow.startAnimation(alphaAnimation);
                break;
        }
    }

    //设置指定的退出动画
    public static AnimationSet setExitAnimation(int AnimationType,float fromX,float toX,float fromY,float toY,float scaleX,float scaleToX,float scaleY,float scaleToY,float pinX,float pinY){
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation = new TranslateAnimation(AnimationType,fromX,AnimationType,toX,AnimationType,fromY,AnimationType,toY);
        ScaleAnimation scaleAnimation = new ScaleAnimation(scaleX,scaleToX,scaleY,scaleToY,AnimationType,pinX,AnimationType,pinY);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setDuration(300);
        return animationSet;
    }

    //设置弹出动画效果
    public static AnimationSet startInfoAnimation(int AnimationType,float fromX,float toX,float fromY,float toY,float scaleX,float scaleToX,float scaleY,float scaleToY,float pinX,float pinY){
        OvershootInterpolator overshootInterpolator = new OvershootInterpolator(2f);
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation = new TranslateAnimation(AnimationType,fromX,AnimationType,toX,AnimationType,fromY,AnimationType,toY);
        ScaleAnimation scaleAnimation = new ScaleAnimation(scaleX,scaleToX,scaleY,scaleToY,AnimationType,pinX,AnimationType,pinY);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setDuration(300);
        animationSet.setFillAfter(true);
        animationSet.setInterpolator(overshootInterpolator);
        return animationSet;
    }

    public static List<Animation> initAnimation(int AnimationType,float fromAlpha,float toAlpha,float fromDegress,float toDegress,float pinX,float pinY){
        List<Animation> animations = new ArrayList<>();
        AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha,toAlpha);
        alphaAnimation.setDuration(300);
        AlphaAnimation exit_alphaAnimation = new AlphaAnimation(toAlpha,fromAlpha);
        exit_alphaAnimation.setDuration(300);
        OvershootInterpolator overshootInterpolator = new OvershootInterpolator(3.5f);
        RotateAnimation rotateAnimation = new RotateAnimation(fromDegress,toDegress,AnimationType,pinX,AnimationType,pinY);
        rotateAnimation.setDuration(300);
        rotateAnimation.setInterpolator(overshootInterpolator);
        rotateAnimation.setFillAfter(true);
        RotateAnimation exit_rotateAnimation = new RotateAnimation(toDegress,fromAlpha,AnimationType,pinX,AnimationType,pinY);
        exit_rotateAnimation.setDuration(300);
        exit_rotateAnimation.setInterpolator(overshootInterpolator);
        exit_rotateAnimation.setFillAfter(true);
        animations.add(alphaAnimation);
        animations.add(exit_alphaAnimation);
        animations.add(rotateAnimation);
        animations.add(exit_rotateAnimation);
        return animations;
    }

    public static boolean isRunning = false;

    public static void infoAnimationTranslate(final View root, final Activity activity){
        if(!isRunning){
            isRunning = true;
            root.setVisibility(View.VISIBLE);
            TranslateAnimation translateAnimation = (TranslateAnimation) AnimationUtils.loadAnimation(activity,R.anim.info_translate);
            root.startAnimation(translateAnimation);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            root.setVisibility(View.GONE);
                            TranslateAnimation exitTranslateAnimation = (TranslateAnimation) AnimationUtils.loadAnimation(MyApplication.getContext(),R.anim.info_translate_exit);
                            root.startAnimation(exitTranslateAnimation);
                            isRunning = false;
                        }
                    });
                }
            }).start();
        }

    }
}
