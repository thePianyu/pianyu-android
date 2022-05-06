package com.duoduo.mark2.models;

public class Article {
    public int article_id;
    public int user_id;
    public String title;
    public String content_markdown;
    public String content_rendered;
    public long update_time;
    public long create_time;
    public int comment_count;
    public Relationships relationships;
}
