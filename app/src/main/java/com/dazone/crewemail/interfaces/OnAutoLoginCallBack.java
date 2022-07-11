package com.dazone.crewemail.interfaces;

import com.dazone.crewemail.data.ErrorData;

public interface OnAutoLoginCallBack {
    void OnAutoLoginSuccess(String response);
    void OnAutoLoginFail(ErrorData dto);
}