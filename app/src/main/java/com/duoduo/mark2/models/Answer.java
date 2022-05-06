package com.duoduo.mark2.models;

public class Answer {
    public int answer_id;
    public int question_id;
    public int user_id;
    public String content_markdown;
    public int comment_count;
    public int vote_up_count;
    public int vote_down_count;
    public long update_time;
    public Relationships relationships;
}
