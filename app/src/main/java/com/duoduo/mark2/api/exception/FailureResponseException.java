package com.duoduo.mark2.api.exception;

import java.io.IOException;

public class FailureResponseException extends IOException {
    public FailureResponseException(String msg) {
        super(msg);
    }
}
