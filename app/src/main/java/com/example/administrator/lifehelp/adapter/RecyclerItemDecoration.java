package com.example.administrator.lifehelp.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.example.administrator.lifehelp.application.MyApplication;
import com.example.administrator.lifehelp.util.Utils;

/**
 * create by Jam
 */

public class RecyclerItemDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if(parent.getChildAdapterPosition(view)==0){
            outRect.top = Utils.dip2px(MyApplication.getContext(),5);
            outRect.bottom = Utils.dip2px(MyApplication.getContext(),5);
        }else{
            outRect.bottom = Utils.dip2px(MyApplication.getContext(),5);
        }

    }
}
