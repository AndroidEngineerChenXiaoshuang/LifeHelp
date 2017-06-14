package com.example.administrator.lifehelp.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;

import com.example.administrator.lifehelp.MainActivity;
import com.example.administrator.lifehelp.gson.InitArticle;
import com.example.administrator.lifehelp.lifefragment.MainFragment;
import com.example.administrator.lifehelp.util.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Create by Jam
 */

public class OnRecyclerListener extends RecyclerView.OnScrollListener {

    private Context context;
    private View root;
    private static List<InitArticle> initArticles;
    private static MainActivity mainActivity;
    private ApplicationRunnable applicationRunnable;

    public OnRecyclerListener(View root, Context context, List<InitArticle> initArticles){
        this.root = root;
        this.context = context;
        this.initArticles = initArticles;
        mainActivity = (MainActivity) context;
        applicationRunnable = new ApplicationRunnable();
    }


    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if(!mainActivity.mainFragment.swipeRefreshLayout.isRefreshing()){
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                int verticalScrollExtent = recyclerView.computeVerticalScrollExtent();
                int verticalScrollOffset = recyclerView.computeVerticalScrollOffset();
                int verticalScrollRange = recyclerView.computeVerticalScrollRange();
                if(verticalScrollExtent+verticalScrollOffset>=verticalScrollRange){
                    mainActivity.mainFragment.swipeRefreshLayout.setRefreshing(true);//显示swipeRefresh
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("http://192.168.43.67/v1/articles/getarticle/");
                    stringBuilder.append(initArticles.size());
                    stringBuilder.append("/10");
                    HttpRequest.request(stringBuilder.toString(), new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            applicationRunnable.setType(ApplicationRunnable.REQUEST_FAILURE);
                            mainActivity.mainFragment.handlerInfo.post(applicationRunnable);
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            String result = response.body().string();
                            if(!result.equals("0")){
                                Gson gson = new Gson();
                                final List<InitArticle> initArticles = gson.fromJson(result,new TypeToken<List<InitArticle>>(){}.getType());
                                for(InitArticle initArticle : initArticles){
                                    OnRecyclerListener.initArticles.add(initArticle);
                                }
                                applicationRunnable.setType(ApplicationRunnable.REQUEST_SUCESS);
                                mainActivity.mainFragment.handlerInfo.post(applicationRunnable);
                            }
                            //证明服务器里面没有更多的文章了
                            else{
                                if(initArticles.size()==0){
                                    applicationRunnable.setType(ApplicationRunnable.SERVER_NOCONTENT);
                                    mainActivity.mainFragment.handlerInfo.post(applicationRunnable);
                                }else{
                                    applicationRunnable.setType(ApplicationRunnable.NO_CONTENT);
                                    mainActivity.mainFragment.handlerInfo.post(applicationRunnable);
                                }

                            }
                            //每次请求都将加载判断是否初始化过数据
                            mainActivity.mainFragment.startRequestInit();
                        }
                    });
                }
            }
        }
    }


    public static class ApplicationRunnable implements Runnable{

        //服务器中没有文章了
        public static final int NO_CONTENT = 0;
        //请求失败
        public static final int REQUEST_FAILURE = 1;
        //请求成功拉取到服务器返回的数据
        public static final int REQUEST_SUCESS = 2;
        //服务器里没有任何一条数据
        public static final int SERVER_NOCONTENT = 3;
        //服务器重新请求加载
        public static final int REQUEST_REPEAT = 4;

        //设置相应的type做不同的逻辑判断
        public int type;

        public void setType(int type){
            this.type = type;
        }

        @Override
        public void run() {
            mainActivity.mainFragment.swipeRefreshLayout.setRefreshing(false);
            switch (type){
                case NO_CONTENT:
                    Toast.makeText(mainActivity,"已经没有更多的文章了",Toast.LENGTH_SHORT).show();
                    break;
                case REQUEST_FAILURE:
                    if(initArticles.size()==0&&mainActivity.mainFragment.shownoNetWork.getVisibility()==View.GONE){
                        mainActivity.mainFragment.shownoNetWork.setVisibility(View.VISIBLE);
                        mainActivity.mainFragment.showServerNoContent.setVisibility(View.GONE);
                    }
                    Toast.makeText(mainActivity,"请求服务器失败",Toast.LENGTH_SHORT).show();
                    break;
                case REQUEST_SUCESS:
                    if(mainActivity.mainFragment.shownoNetWork.getVisibility()==View.VISIBLE){
                        mainActivity.mainFragment.shownoNetWork.setVisibility(View.GONE);
                    }
                    if(mainActivity.mainFragment.showServerNoContent.getVisibility()==View.VISIBLE){
                        mainActivity.mainFragment.showServerNoContent.setVisibility(View.GONE);
                    }
                    mainActivity.mainFragment.initArticleAdapter.notifyItemChanged(initArticles.size()-1);
                    break;
                case SERVER_NOCONTENT:
                    if(initArticles.size()==0&&mainActivity.mainFragment.showServerNoContent.getVisibility()==View.GONE){
                        mainActivity.mainFragment.showServerNoContent.setVisibility(View.VISIBLE);
                        mainActivity.mainFragment.shownoNetWork.setVisibility(View.GONE);
                    }
                    if(mainActivity.mainFragment.initDateRecycler.getChildCount()>0){
                        mainActivity.mainFragment.initDateRecycler.removeAllViews();
                    }
                    Toast.makeText(mainActivity,"服务器中没有数据",Toast.LENGTH_SHORT).show();
                    break;
                case REQUEST_REPEAT:
                    if(mainActivity.mainFragment.shownoNetWork.getVisibility()==View.VISIBLE){
                        mainActivity.mainFragment.shownoNetWork.setVisibility(View.GONE);
                    }
                    if(mainActivity.mainFragment.showServerNoContent.getVisibility()==View.VISIBLE){
                        mainActivity.mainFragment.showServerNoContent.setVisibility(View.GONE);
                    }
                    mainActivity.mainFragment.initArticleAdapter = null;
                    mainActivity.mainFragment.initArticleAdapter = new InitArticleAdapter(mainActivity,initArticles);
                    mainActivity.mainFragment.initDateRecycler.setAdapter(mainActivity.mainFragment.initArticleAdapter);
                    break;

            }
        }
    }
}