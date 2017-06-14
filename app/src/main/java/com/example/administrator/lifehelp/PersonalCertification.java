package com.example.administrator.lifehelp;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzz on 2017/5/31
 */

public class PersonalCertification extends AppCompatActivity implements View.OnClickListener{

    //返回按钮
    public ImageButton returnButton;
    //显示标题
    public TextView titleName;
    //使用viewPager切换界面
    public ViewPager viewPager;
    public PagerAdapter pagerAdapter;
    public List<View> listView = new ArrayList<>();
    //开始认证
    public Button beginAuthentication;
    //注释
    private final static String TAG = "jsone";
    //判断界面
    private static int judgeInterface = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        //初始化控件
        initControl();
        titleName.setText("个人认证");
    }

    private void initControl() {
        viewPager = (ViewPager) findViewById(R.id.person_viewPager);
        //关闭viewPager的滑动显示事件
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        View inflaterUser = LayoutInflater.from(this).inflate(R.layout.activity_person_user,null);
        View inflaterContext = LayoutInflater.from(this).inflate(R.layout.activity_person_context,null);
        listView.add(inflaterUser);
        listView.add(inflaterContext);

        returnButton = (ImageButton) findViewById(R.id.title_return);
        returnButton.setOnClickListener(this);
        titleName = (TextView) findViewById(R.id.title_name);
        beginAuthentication = (Button) inflaterUser.findViewById(R.id.begin_Authentication);
        beginAuthentication.setOnClickListener(this);

        pagerAdapter = new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {

                View view = listView.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(listView.get(position));
            }

            @Override
            public int getCount() {
                return listView.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                Log.i(TAG, "initControl: " +2);
                return view == object;
            }
        };

        viewPager.setAdapter(pagerAdapter);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.begin_Authentication:
                viewPager.setCurrentItem(1);
                judgeInterface = 1;
                break;
            case R.id.title_return:
                if (judgeInterface == 1){
                    viewPager.setCurrentItem(0);
                    judgeInterface = 0;
                }else {
                    finish();
                    overridePendingTransition(0,R.anim.walletactivity_left_exit);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (judgeInterface == 1){
            viewPager.setCurrentItem(0);
            judgeInterface = 0;
        }else {
            finish();
            overridePendingTransition(0,R.anim.walletactivity_left_exit);
        }
    }
}
