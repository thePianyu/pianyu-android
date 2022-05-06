package com.duoduo.mark2.api;

public enum Errors {
    INVALID_FIELD(200001),
    USER_NOT_EXIST(201003),
    NOT_LOGIN(201001);

    private final int code;
    private Errors(int code) {
        this.code = code;
    }
}
