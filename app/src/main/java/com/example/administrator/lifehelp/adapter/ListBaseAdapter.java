package com.example.administrator.lifehelp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.lifehelp.R;
import com.example.administrator.lifehelp.application.MyApplication;

/**
 * Created by Administrator on 2017/4/10 0010.
 */

public class ListBaseAdapter extends BaseAdapter {

    public int type;
    public int count;
    public SharedPreferences sharedPreferences;
    public int selector;


    public ListBaseAdapter(int type){
        this.type = type;
        switch (type){
            case MyApplication.Type.MAINACTIVITY_RIGHT_LIST:
                count = MyApplication.Info.HELP_CLASS_IMAGES.length;
                break;

        }
        sharedPreferences = MyApplication.getContext().getSharedPreferences(MyApplication.Type.SHARED_USER_LIKE, Context.MODE_PRIVATE);
        selector = sharedPreferences.getInt(MyApplication.Type.SHARED_USER_HELPCLASS_SELECTOR,0);
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        switch (type){
            case MyApplication.Type.MAINACTIVITY_RIGHT_LIST:

                return MyApplication.Info.HELP_CLASS_NAMES[position];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        switch (type){
            case MyApplication.Type.MAINACTIVITY_RIGHT_LIST:

                return MyApplication.Info.HELP_CLASS_IMAGES[position];
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (type){
            case MyApplication.Type.MAINACTIVITY_RIGHT_LIST:
               return setHelpClass(position,convertView,parent);
        }
        return null;
    }

    /**
     * 用于设置筛选的类型
     * @param position  标度
     * @param convertView   复用控件
     * @param parent    父类
     * @return  返回设置好的对象
     */
    public View setHelpClass(int position,View convertView,ViewGroup parent){
        View root ;
        HelpClass helpClass;
        String info;
        if(convertView==null){
            root = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.help_class_item,parent,false);
            helpClass = new HelpClass();
            helpClass.className = (TextView) root.findViewById(R.id.class_name);
            helpClass.classImg = (ImageView) root.findViewById(R.id.class_img);
            root.setTag(helpClass);
        }else{
            root = convertView;
            helpClass = (HelpClass) root.getTag();
        }

        helpClass.classImg.setImageResource((int) getItemId(position));
        info = (String) getItem(position);
        helpClass.className.setText(info);

        if(position==selector){
            root.findViewById(R.id.selector).setVisibility(View.VISIBLE);
            helpClass.className.setTextColor(MyApplication.Color.THEMECOLOR);
        }else{
            helpClass.className.setTextColor(MyApplication.Color.FONTCOLOR);
            root.findViewById(R.id.selector).setVisibility(View.GONE);
        }
        return root;
    }

    /**
     * 帮助分类中的viewHodler用于和绑定视图的view进行setTag或getTag
     */
    public class HelpClass{
        public TextView className;
        public ImageView classImg;
    }
}
