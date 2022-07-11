package com.dazone.crewemail.interfaces;


import com.dazone.crewemail.data.ErrorData;

public interface BaseHTTPCallBackWithString {
    void onHTTPSuccess(String message);
    void onHTTPFail(ErrorData errorData);
}
