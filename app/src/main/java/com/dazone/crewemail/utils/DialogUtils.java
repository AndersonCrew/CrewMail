package com.dazone.crewemail.utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.dazone.crewemail.R;
import com.dazone.crewemail.dialog.MessageDialog;

public class DialogUtils {

    public static ProgressDialog mProgressDialog;

    public static void showProgressDialog(Context context) {
        if (null == mProgressDialog || !mProgressDialog.isShowing()) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setTitle(context.getString(R.string.loading_title));
            mProgressDialog.setMessage(context.getString(R.string.loading_content));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }

    public static void dismissProgressDialog() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
