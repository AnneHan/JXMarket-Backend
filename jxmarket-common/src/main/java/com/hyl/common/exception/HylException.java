package com.hyl.common.exception;

import com.hyl.common.enums.ResponseCodeEnum;

public class HylException extends Exception {

    private final ResponseCodeEnum responseCode;
    private final String language;


    public HylException(ResponseCodeEnum responseCode, String language) {
        super();

        this.responseCode = responseCode;
        this.language = language;
    }

    public HylException(ResponseCodeEnum e, Throwable throwable, String language) {
        super(throwable);
        this.responseCode = e;
        this.language = language;
    }

    public ResponseCodeEnum getResponseCode() {
        return responseCode;
    }

    public String getLanguage() {
        return language;
    }

}
