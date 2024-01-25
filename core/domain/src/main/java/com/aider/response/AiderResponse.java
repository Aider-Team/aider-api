package com.aider.response;

import com.aider.enums.MESSAGES;
import lombok.Getter;

@Getter
public class AiderResponse<T> {
    private int code;
    private String message;
    private T data;

    public AiderResponse(T data) {
        this.code = MESSAGES.SUCCESS.getCode();
        this.message = MESSAGES.SUCCESS.getMessage();
        this.data = data;
    }

    public AiderResponse(MESSAGES message, T data) {
        this.code = message.getCode();
        this.message = message.getMessage();
        this.data = data;
    }
}
