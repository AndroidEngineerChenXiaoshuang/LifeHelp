package com.example.administrator.lifehelp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.RippleDrawable;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.lifehelp.R;
import com.example.administrator.lifehelp.UserArticleActivity;
import com.example.administrator.lifehelp.application.MyApplication;
import com.example.administrator.lifehelp.gson.InitArticle;
import com.example.administrator.lifehelp.util.PopupWindowUtil;
import com.example.administrator.lifehelp.util.Utils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 *  CreateBy Jam
 */

public class InitArticleAdapter extends RecyclerView.Adapter {
    public List<InitArticle> initArticles;
    public Context context;

    public InitArticleAdapter(Context context,List<InitArticle> initArticles){
        this.initArticles = initArticles;
        this.context = context;
    }

    public class InitArticleViewHolder extends RecyclerView.ViewHolder{

        public View root;
        public TextView articleTitle;
        public TextView articleContent;
        public TextView articleReward;
        public TextView articleNickname;
        public CircleImageView circleImageView;

        public InitArticleViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            articleTitle = (TextView) itemView.findViewById(R.id.help_title_content);
            articleContent = (TextView) itemView.findViewById(R.id.help_content);
            articleReward = (TextView) itemView.findViewById(R.id.help_price);
            articleNickname = (TextView) itemView.findViewById(R.id.userName);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.user_image);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InitArticleViewHolder(LayoutInflater.from(context).inflate(R.layout.help_information,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final InitArticleViewHolder initArticleViewHolder = (InitArticleViewHolder) holder;
        initArticleViewHolder.articleTitle.setText(initArticles.get(position).article_title);
        initArticleViewHolder.articleContent.setText(initArticles.get(position).article_content);
        initArticleViewHolder.articleReward.setText(initArticles.get(position).article_reward);
        initArticleViewHolder.articleNickname.setText(initArticles.get(position).article_nickname);

        String userImg = initArticles.get(position).article_author;
        //证明头像是存在服务器里面的
        if(userImg.lastIndexOf("h")!=-1){
            Glide.with(MyApplication.getContext())
                    .load(initArticles.get(position).author_avatar)
                    .asBitmap()
                    .into(initArticleViewHolder.circleImageView);
        }else{
            initArticleViewHolder.circleImageView.setBackgroundResource(R.drawable.ic_user);
        }

        initArticleViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyApplication.getContext(), UserArticleActivity.class);
                intent.putExtra("createTime",initArticles.get(position).create_time);
                intent.putExtra("articleValid",initArticles.get(position).article_valid);
                intent.putExtra("articleId",initArticles.get(position).article_id);
                intent.putExtra("userId",initArticles.get(position).article_author);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return initArticles.size();
    }
}
