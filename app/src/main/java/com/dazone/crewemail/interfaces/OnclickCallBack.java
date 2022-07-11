package com.dazone.crewemail.interfaces;

import android.view.View;

import com.dazone.crewemail.data.MailProfileData;

/**
 * Created by dazone on 4/28/2017.
 */

public interface OnclickCallBack {
    void onClickCall(View view, String phonenumber);

    void onClickMessage(View view, String phoneNumber);

    void onClickSendEmail(View view, String Email);
    void onClickBack(View view);

    void onClickChangePass(View view, MailProfileData data);
}
