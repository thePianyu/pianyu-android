package com.duoduo.mark2.models;

public class Question {

    public int question_id;
    public int user_id;
    public String title;
    public String content_markdown;
    public String content_rendered;
    public int comment_count;
    public int answer_count;
    public int follower_count;
    public int vote_count;
    public int vote_up_count;
    public int vote_down_count;
    public long last_answer_time;
    public long create_time;
    public long update_time;
    public int delete_time;
    public Relationships relationships;

}
