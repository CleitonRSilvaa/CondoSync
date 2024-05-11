package com.CondoSync.components;

import lombok.Getter;

public class ValidateUserException extends RuntimeException {

    @Getter
    private String title;

    @Getter
    private Object data;

    public ValidateUserException() {
        super();
    }

    public ValidateUserException(String msg, Object data) {
        super(msg);
        this.data = data;
    }

    public ValidateUserException(String title, String msg, Object data) {
        super(msg);
        this.data = data;
        this.title = title;
    }

}
