package com.dazone.crewemail.database;

import android.os.AsyncTask;
import android.util.Log;

import com.dazone.crewemail.data.MailData;
import com.dazone.crewemail.interfaces.OnWriteLocalDataCallback;

import java.util.ArrayList;

public class WriteMailListTask extends AsyncTask<Void, Void, Void> {
    private OnWriteLocalDataCallback callback;
    ArrayList<MailData> mailData;

    public WriteMailListTask(ArrayList<MailData> mailData, OnWriteLocalDataCallback callback) {
        this.callback = callback;
        this.mailData = mailData;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            if (mailData != null)
                for (MailData mailData : mailData) {

                    DataManager.Instance().saveMail(mailData);
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (callback != null)
            callback.onWriteSuccess();
    }
}
