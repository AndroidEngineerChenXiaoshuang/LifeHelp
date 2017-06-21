package com.example.administrator.lifehelp.lifefragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.administrator.lifehelp.MainActivity;
import com.example.administrator.lifehelp.R;
import com.example.administrator.lifehelp.application.MyApplication;
import com.example.administrator.lifehelp.util.AnimationUtil;
import com.example.administrator.lifehelp.util.SetFouchable;
import com.example.administrator.lifehelp.util.Utils;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import okhttp3.RequestBody;

/**
 * 该类继承的是support v4包中,为了兼容低版本的android版本,该类用于加载主界面的第二个滑动界面
 * 主要操作fragment的筛选价格
 */

public class RightLayoutFragment extends Fragment implements View.OnClickListener,View.OnFocusChangeListener{

    //帮助类的布局
    public LinearLayout helpClassLayout;
    //与试图绑定的view
    public View root;
    //输入控件:表示金额最小开始
    public EditText startMoney;
    //输入控件:表示金额最大开始
    public EditText endMoney;
    //选中状态的类名
    public TextView className;

    public MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater inflaterRoot = LayoutInflater.from(MyApplication.getContext());
        root = inflaterRoot.inflate(R.layout.right_layout_one,container,false);
        if(Build.VERSION.SDK_INT>=21){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Utils.getStatusHeight());
            root.findViewById(R.id.title_status).setLayoutParams(layoutParams);
            root.findViewById(R.id.title_status).setBackgroundColor(0xFFBABABA);
        }
        root.setOnClickListener(this);
        className = (TextView) root.findViewById(R.id.selectorClassName);
        startMoney = (EditText) root.findViewById(R.id.start_money);
        //监听焦点获取
        startMoney.setOnFocusChangeListener(this);
        endMoney = (EditText) root.findViewById(R.id.end_money);
        //监听焦点获取
        endMoney.setOnFocusChangeListener(this);
        //注册重置按钮事件
        root.findViewById(R.id.restart).setOnClickListener(this);
        //注册确定按钮事件
        root.findViewById(R.id.sure).setOnClickListener(this);

        helpClassLayout = (LinearLayout) root.findViewById(R.id.help_class);
        helpClassLayout.setOnClickListener(this);
        mainActivity = (MainActivity) getActivity();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        className.setText(MyApplication.Info.HELP_CLASS_NAMES
                [getActivity().getSharedPreferences(MyApplication.Type.SHARED_USER_LIKE, Context.MODE_PRIVATE)
                .getInt(MyApplication.Type.SHARED_USER_HELPCLASS_SELECTOR,0)]);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.help_class:
                //告诉主界面我已经打开右边的fragment的第二层了
                mainActivity.setOpenDrawerOfRight(true,root.findViewById(R.id.isShow));
                //当布局打开的时候给相应的布局加上显示阴影的动画效果
                AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
                AnimationUtil.setDecelerateInterpolator(root.findViewById(R.id.isShow),alphaAnimation,MyApplication.Type.ALPHANIMATION,200);
                root.findViewById(R.id.isShow).setVisibility(View.VISIBLE);
                //加载fragment
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                SetFouchable.setFouch(startMoney);
                //为fragment设置动画效果
                fragmentTransaction.setCustomAnimations(R.anim.fragment_left_enter,0,0,R.anim.fragment_right_exit);
                RightLayoutFragmentOrder rightLayoutFragmentOrder = new RightLayoutFragmentOrder();
                rightLayoutFragmentOrder.setIsShow(root.findViewById(R.id.isShow),className);
                fragmentTransaction.addToBackStack(null).add(R.id.right_layout,rightLayoutFragmentOrder);
                fragmentTransaction.commit();
                break;
            case R.id.restart:
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(MyApplication.Type.SHARED_USER_LIKE,Context.MODE_PRIVATE).edit();
                editor.putInt(MyApplication.Type.SHARED_USER_HELPCLASS_SELECTOR,0);
                editor.apply();
                startMoney.setText("");
                endMoney.setText("");
                className.setText(MyApplication
                        .Info.HELP_CLASS_NAMES[getActivity().getSharedPreferences
                        (MyApplication.Type.SHARED_USER_LIKE,Context.MODE_PRIVATE)
                        .getInt(MyApplication.Type.SHARED_USER_HELPCLASS_SELECTOR,0)]);
                break;
            case R.id.sure:
                mainActivity.closeDrawer();
                SetFouchable.setFouch(startMoney);
                break;
        }
    }


    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()){
            case R.id.start_money:
                if (b){
                    startMoney.setHint("");
                } else {
                    startMoney.setHint("起步报酬");
                }
                break;
            case R.id.end_money:
                if (b){
                    endMoney.setHint("");
                }else {
                    endMoney.setHint("最高报酬");
                }
                break;
        }
    }
}