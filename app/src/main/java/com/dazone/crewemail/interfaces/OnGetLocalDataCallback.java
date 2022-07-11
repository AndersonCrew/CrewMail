package com.dazone.crewemail.interfaces;

import com.dazone.crewemail.data.MailData;

import java.util.ArrayList;

public interface OnGetLocalDataCallback {
    void onGetMailList(ArrayList<MailData> mailData);
}
