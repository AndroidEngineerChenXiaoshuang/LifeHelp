package com.example.administrator.lifehelp.adapter;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.lifehelp.MainActivity;
import com.example.administrator.lifehelp.gson.InitArticle;
import com.example.administrator.lifehelp.util.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.security.PolicySpi;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Jam
 * on 2017/6/6 0006.
 */

public class OnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

    public Context context;
    private List<InitArticle> initArticles;
    private String url;
    private OnRecyclerListener.ApplicationRunnable applicationRunnable;

    public OnRefreshListener(Context context, List<InitArticle> initArticles){
        this.context = context;
        this.initArticles = initArticles;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://192.168.43.67/v1/ArticleOperation/getarticle/");
        stringBuilder.append("0/");
        stringBuilder.append(10);
        url = stringBuilder.toString();
        applicationRunnable = new OnRecyclerListener.ApplicationRunnable();
}

    @Override
    public void onRefresh() {
        HttpRequest.request(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                applicationRunnable.setType(OnRecyclerListener.ApplicationRunnable.REQUEST_FAILURE);
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.mainFragment.handlerInfo.post(applicationRunnable);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                MainActivity mainActivity = (MainActivity) context;
                if(!result.equals("0")){
                    initArticles.clear();
                    Gson gson = new Gson();
                    List<InitArticle> initArticles = gson.fromJson(result,new TypeToken<List<InitArticle>>(){}.getType());
                    for(InitArticle initArticle:initArticles){
                        OnRefreshListener.this.initArticles.add(initArticle);
                    }
                    applicationRunnable.setType(OnRecyclerListener.ApplicationRunnable.REQUEST_REPEAT);
                    mainActivity.mainFragment.handlerInfo.post(applicationRunnable);
                }else{
                    //证明服务器中没有一条数据
                    applicationRunnable.setType(OnRecyclerListener.ApplicationRunnable.SERVER_NOCONTENT);
                    mainActivity.mainFragment.handlerInfo.post(applicationRunnable);
                }
                mainActivity.mainFragment.startRequestInit();
            }
        });
    }
}
