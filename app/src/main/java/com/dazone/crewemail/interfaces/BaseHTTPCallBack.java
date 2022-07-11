package com.dazone.crewemail.interfaces;


import com.dazone.crewemail.data.ErrorData;

public interface BaseHTTPCallBack {
    void onHTTPSuccess();
    void onHTTPFail(ErrorData errorDto);
}
