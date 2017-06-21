package com.example.administrator.lifehelp.IMapplication.view;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.administrator.lifehelp.R;
import com.example.administrator.lifehelp.util.Utils;

/**
 *
 */

public class UserinterfaceFragment extends Fragment implements View.OnClickListener{

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.user_chat_interface,container,false);
        rootView.setOnClickListener(this);
        ImageButton imageButton = (ImageButton) rootView.findViewById(R.id.title_return);
        TextView textView = (TextView) rootView.findViewById(R.id.title_name);
        textView.setText("消息");
        imageButton.setVisibility(View.GONE);
        if(Build.VERSION.SDK_INT>=21){
            View view = rootView.findViewById(R.id.is_show);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = Utils.getStatusHeight();
            view.setLayoutParams(layoutParams);
            view.setVisibility(View.VISIBLE);
            RelativeLayout relativeLayout = (RelativeLayout) rootView.findViewById(R.id.include_title);
            RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
            layoutParams2.topMargin = Utils.getStatusHeight();
            relativeLayout.setLayoutParams(layoutParams2);
        }

        return rootView;
    }


    @Override
    public void onClick(View v) {

    }
}
