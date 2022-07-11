package com.dazone.crewemail.interfaces;

import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.MailBoxData;

/**
 * Created by THANHTUNG on 22/12/2015.
 */
public interface OnMailDetailCallBack {
    void OnMailDetailSuccess(MailBoxData mailBoxData);
    void OnMaillDetailFail (ErrorData errorData);
}
