package com.duoduo.mark2.api.exception;

import java.io.IOException;

public class LoginException extends IOException {
    public LoginException(String msg) {
        super(msg);
    }
}
