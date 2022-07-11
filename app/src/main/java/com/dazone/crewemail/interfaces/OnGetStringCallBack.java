package com.dazone.crewemail.interfaces;


import com.dazone.crewemail.data.ErrorData;

public interface OnGetStringCallBack {
     void onGetStringSuccess(String... strings);
    void onGetStringFail(ErrorData errorDto);
}
