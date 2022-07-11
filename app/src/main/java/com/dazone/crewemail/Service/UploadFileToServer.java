package com.dazone.crewemail.Service;

import android.os.AsyncTask;
import android.util.Log;

import com.dazone.crewemail.interfaces.pushlishProgressInterface;
import com.dazone.crewemail.utils.Util;


public class UploadFileToServer extends AsyncTask<String, String, String> implements pushlishProgressInterface {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Util.printLogs("Upload " + result);
    }


    @Override
    public void push(double progress, String nameFile) {
    }
}
