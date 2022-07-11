package com.dazone.crewemail.data;

import com.google.gson.annotations.SerializedName;

public class ErrorData {

    public boolean unAuthentication;
    public static final int EXCEPTION_ERROR_CODE = 99;

    @SerializedName("code")
    private int mCode = 1;

    @SerializedName("message")
    private String mMessage;

    public ErrorData() {
    }

    public ErrorData(int code, String message) {
        mCode = code;
        mMessage = message;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        this.mCode = code;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }
}
