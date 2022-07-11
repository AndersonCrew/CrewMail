package com.dazone.crewemail.database;

import android.os.AsyncTask;
import android.util.Log;

import com.dazone.crewemail.data.MailData;
import com.dazone.crewemail.interfaces.OnGetLocalDataCallback;

import java.util.ArrayList;

public class GetMailListTask extends AsyncTask<Void, Void, ArrayList<MailData>> {
    private OnGetLocalDataCallback callback;
    private int mailBoxNo;
    private String searchString;

    public GetMailListTask(int mailBox, String searchText, OnGetLocalDataCallback callback){
        this.callback = callback;
        this.mailBoxNo = mailBox;
        this.searchString = searchText;
    }

    @Override
    protected ArrayList<MailData> doInBackground(Void... strings) {
        return DataManager.Instance().getMailList(mailBoxNo, searchString);
    }

    @Override
    protected void onPostExecute(ArrayList<MailData> mailData) {
        if(callback != null)
            callback.onGetMailList(mailData);
    }
}
