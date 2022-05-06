package com.duoduo.mark2.models;

import com.google.gson.annotations.SerializedName;

public class Topic {
    @SerializedName("topic_id")
    public int id;
    public String name;
    public String description;
    public Image cover;
}
