package com.duoduo.mark2.models;

public class Comment {
    public int comment_id;
    public int commentable_id;
    public String commentable_type;
    public int user_id;
    public String content;
    public int vote_count;
    public int vote_up_count;
    public int vote_down_count;
    public long create_time;
    public long update_time;
    public Relationships relationships;
    public int reply_count;
}
