package com.example.administrator.lifehelp.gson;

import com.google.gson.annotations.SerializedName;

/**
 *  请求文章的详细信息
 */

public class InitArticleInfo {

    @SerializedName("article_id")
    public String articleId;

    @SerializedName("article_title")
    public String articleTitle;

    @SerializedName("article_content")
    public String articleContent;

    @SerializedName("article_author")
    public String articleAuthor;

    @SerializedName("article_reward")
    public String articleReward;

    @SerializedName("article_avatar")
    public String articleAvatar;

    @SerializedName("article_signature")
    public String articleSignature;

}
