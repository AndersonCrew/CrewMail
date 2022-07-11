package com.dazone.crewemail.interfaces;

import com.dazone.crewemail.data.ErrorData;

public interface IDeviceRestriction {
    void onSuccess();
    void onError(ErrorData error);
}
