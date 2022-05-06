package com.duoduo.mark2.models;

import java.util.HashMap;
import java.util.List;

public class BaseResponse<T> {
    public int code;
    public T data;
    public String message;
    public Pagination pagination;
    public String extra_message;
    public HashMap<String, String> errors;
}
