package com.dazone.crewemail.interfaces;


import com.dazone.crewemail.data.ErrorData;

/**
 * Clone from time card / note project
 * Created by david on 9/25/15.
 */
public interface OnCheckDevice {
    void onDeviceSuccess();
    void onHTTPFail(ErrorData errorData);
}
