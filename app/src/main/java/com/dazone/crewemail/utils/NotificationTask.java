package com.dazone.crewemail.utils;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.dazone.crewemail.BuildConfig;
import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationTask extends AsyncTask<String, Integer, String> {

    private final static String TAG = NotificationTask.class.getName();
    private NotificationCompat.Builder mBuilder;
    private final int mId;
    private NotificationManager mNotifyManager;
    private final String mTitle;
    private Context context;
    private PowerManager.WakeLock mWakeLock;
    ProgressDialog mProgressDialog;
    private File outputFile;

    public NotificationTask(Context context, String title, int id) {
        this.context = context;
        mTitle = title;
        mId = id;
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            outputFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/Download/" + sUrl[1]);
            output = new FileOutputStream(outputFile);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    /**
     * called only once
     */
    private void initNotification() {
        String channelId = "channel-01";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        mNotifyManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, "crewMail1", importance);
            mNotifyManager.createNotificationChannel(mChannel);
        }
        mBuilder = new NotificationCompat.Builder(context,channelId);
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "onPostExecute");
        mWakeLock.release();
        mProgressDialog.dismiss();
        if (result != null)
            Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();

        setCompletedNotification();
    }


    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute");
        super.onPreExecute();

        initNotification();

        setStartedNotification();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(context.getResources().getString(R.string.wating_app_download));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();

    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        Log.d(TAG, "onProgressUpdate with argument = " + progress[0]);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(progress[0]);
        setProgressNotification();
        updateProgressNotification(progress[0]);

    }

    /**
     * the last notification
     */
    private void setCompletedNotification() {
        mBuilder.setSmallIcon(R.drawable.icon_download_black_48).setContentTitle(mTitle)
                .setContentText("Download completed");
        openFile(outputFile);
        mNotifyManager.notify(mId, mBuilder.build());

    }

    void openFile(File file) {
        try {
            String type = Statics.getMimeType(file.getAbsolutePath());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(DaZoneApplication.getInstance(), BuildConfig.APPLICATION_ID + ".provider", file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(apkUri, type);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setPriority(Notification.PRIORITY_MAX);
                mBuilder.setContentIntent(pendingIntent);
            } else {
                Log.d(TAG, "type:" + type);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), type);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setPriority(Notification.PRIORITY_MAX);
                mBuilder.setContentIntent(pendingIntent);
            }
        } catch (Exception e) {


            Log.d(TAG, "Exception");
            Toast.makeText(DaZoneApplication.getInstance(), "No Application available to view this file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private String getMimeType(String url) {
        String type = null;
        String extension = url.substring(url.lastIndexOf(".") + 1);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    /**
     * the progress notification
     * <p>
     * called only once
     */
    private void setProgressNotification() {
        mBuilder.setContentTitle(mTitle).setContentText("Download in progress")
                .setSmallIcon(R.drawable.icon_downloaf_white_48);
    }

    /**
     * the first notification
     */
    private void setStartedNotification() {
        mBuilder.setSmallIcon(R.drawable.icon_download_black_48).setContentTitle(mTitle)
                .setContentText("Started");
        mNotifyManager.notify(mId, mBuilder.build());
    }

    /**
     * the progress notification
     * <p>
     * called every 0.1 sec to update the progress bar
     *
     * @param incr
     */
    private void updateProgressNotification(int incr) {
        // Sets the progress indicator to a max value, the
        // current completion percentage, and "determinate"
        // state
        mBuilder.setProgress(100, incr, false);
        // Displays the progress bar for the first time.
        mNotifyManager.notify(mId, mBuilder.build());
        // Sleeps the thread, simulating an operation
        // that takes time
    }
}