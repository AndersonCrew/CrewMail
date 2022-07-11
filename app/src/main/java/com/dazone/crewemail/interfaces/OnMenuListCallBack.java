package com.dazone.crewemail.interfaces;

import com.dazone.crewemail.data.ErrorData;

import java.util.LinkedHashMap;

/**
 * Created by Dinh Huynh on 12/10/15.
 */
public interface OnMenuListCallBack {

    void onMenuListSuccess(LinkedHashMap menuMap);
    void onMenuListFail(ErrorData errorData);
}
