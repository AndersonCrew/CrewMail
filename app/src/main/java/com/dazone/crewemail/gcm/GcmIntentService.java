package com.dazone.crewemail.gcm;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;
import static androidx.core.app.NotificationCompat.PRIORITY_LOW;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.dazone.crewemail.R;
import com.dazone.crewemail.activities.ActivityMailDetail;
import com.dazone.crewemail.event.ReloadListMail;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.StaticsBundle;
import com.dazone.crewemail.utils.TimeUtils;
import com.dazone.crewemail.utils.Util;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.greenrobot.eventbus.EventBus;


public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }

    boolean isEnableSound = true, isEnableVibrate = true, isNewMail = true, isTime = true;
    private String strFromTime, strToTime;

    private String channelId = "0011001";
    private String channelName = "CrewMail 0011001";
    private String channelIdNonSound = "0022002";
    private String channelNameNonSound = "CrewMail 0022002";
    private NotificationChannel channel1, channel2;

    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        /** GET PREFERENCES */
        isEnableVibrate = new Prefs().getBooleanValue(Statics.KEY_PREFERENCES_NOTIFICATION_VIBRATE, true);
        isEnableSound = new Prefs().getBooleanValue(Statics.KEY_PREFERENCES_NOTIFICATION_SOUND, true);
        isNewMail = new Prefs().getBooleanValue(Statics.KEY_PREFERENCES_NOTIFICATION_NEW_MAIL, true);
        isTime = new Prefs().getBooleanValue(Statics.KEY_PREFERENCES_NOTIFICATION_TIME, true);
        strFromTime = new Prefs().getStringValue(Statics.KEY_PREFERENCES_NOTIFICATION_TIME_FROM_TIME, Util.getString(R.string.setting_notification_from_time));
        strToTime = new Prefs().getStringValue(Statics.KEY_PREFERENCES_NOTIFICATION_TIME_TO_TIME, Util.getString(R.string.setting_notification_to_time));


        if (extras != null) {
            if (!extras.isEmpty()) {
                Log.d(Statics.LOG_TAG, extras.toString());

                if (GoogleCloudMessaging.
                        MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                    //TODO sendNotification("Send error",extras.toString());
                } else if (GoogleCloudMessaging.
                        MESSAGE_TYPE_DELETED.equals(messageType)) {
                    //TODO sendNotification("Deleted messages on server ",
                } else if (GoogleCloudMessaging.
                        MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                    try {
                        Util.printLogs(extras.toString());
                        String title = extras.getString("Title");
                        String fromName = extras.getString("FromName");
                        String content = extras.getString("Content");
                        String receivedDate = extras.getString("ReceivedDate");
                        String toAddress = extras.getString("ToAddress");
                        String mailNo = extras.getString("MailNo");
                        String mailBoxNo = extras.getString("MailBoxNo");

                        EventBus.getDefault().post(new ReloadListMail());
                        ShowNotification(title, fromName, content, receivedDate, toAddress, Long.parseLong(mailNo), mailBoxNo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                Log.d(Statics.LOG_TAG, "empty");
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel1 = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel2 = new NotificationChannel(channelIdNonSound, channelNameNonSound, NotificationManager.IMPORTANCE_LOW);
        }
        startForeground((int) System.currentTimeMillis(), getNotification());
    }

    public Notification getNotification() {
        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel).setSmallIcon(android.R.drawable.ic_menu_mylocation).setContentTitle("crewChat");
        Notification notification = mBuilder
                .setPriority(isEnableSound ? PRIORITY_HIGH : PRIORITY_LOW)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();


        return notification;
    }

    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mChannel = null;
        String chanelId = isEnableSound ? channelId : channelIdNonSound;
        if (isEnableSound) {
            mChannel = channel1;
        } else {
            mChannel = channel2;
        }

        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            stopSelf();
        }

        return chanelId;
    }

    private void ShowNotification(String title, String fromName, String content, String receivedDate, String toAddress, long mailNo, String mailBoxNo) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        /** PendingIntent */
        Intent intent = new Intent(this, ActivityMailDetail.class);
        intent.putExtra(StaticsBundle.BUNDLE_MAIL_NO, mailNo);
        intent.putExtra(StaticsBundle.BUNDLE_MAIL_FROM_NOTIFICATION, true);
        intent.putExtra(StaticsBundle.BUNDLE_MAIL_FROM_NOTIFICATION_MAILBOX_NO, mailBoxNo);
        intent.putExtra(StaticsBundle.PREFS_KEY_ISREAD, false);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final long[] vibrate = new long[]{1000, 1000, 1000, 1000, 1000};
            final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel mChannel = isEnableSound ? channel1 : channel2;
            mChannel.setShowBadge(true);
            mNotificationManager.createNotificationChannel(mChannel);


            String idChanel = isEnableSound ? channelId : channelIdNonSound;
            mBuilder = new NotificationCompat.Builder(getApplicationContext(), idChanel);
            mBuilder.setPriority(isEnableSound ? PRIORITY_HIGH : PRIORITY_LOW)
                    .setChannelId(idChanel)
                    .setAutoCancel(false)
                    .setSmallIcon(R.drawable.small_icon_email)
                    .setTicker(getString(R.string.the_new_mail_has_arrived))
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.launcher_icon))
                    .setContentTitle(fromName)
                    .setContentText(title)
                    .setContentIntent(contentIntent);

            // Check notification setting and config notification
            if (isEnableSound) {
                mBuilder.setSound(soundUri);
            } else {
                mBuilder.setSound(null);
            }

            if (isEnableVibrate) {
                mBuilder.setVibrate(vibrate);
                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(1000);
            } else {
                final long[] noVibrate = new long[]{0, 0, 0, 0, 0};
                mBuilder.setVibrate(noVibrate);
                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(0);
            }

            Notification notification = mBuilder.build();
            notification.number = 100;

            /** STYLE BIG TEXT */
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            String bigText = "<font color='#878787'>" + title + "</font>";
            if (!TextUtils.isEmpty(content.replaceAll("&nbsp;", "").trim())) {
                bigText = bigText + "<br/>" + content;
            }

            bigTextStyle.bigText(Html.fromHtml(bigText));
            bigTextStyle.setSummaryText(toAddress);
            mBuilder.setStyle(bigTextStyle);
            //consider using setTicker of Notification.Builder
            if (isNewMail) {
                if (isTime) {
                    if (TimeUtils.isBetweenTime(strFromTime, strToTime)) {
                        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
                        startForeground((int) System.currentTimeMillis(), notification);
                    }
                } else {
                    mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
                    startForeground((int) System.currentTimeMillis(), notification);
                }
            }

        } else {
            final long[] vibrate = new long[]{1000, 1000, 0, 0, 0};
            final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


            mBuilder = new NotificationCompat.Builder(getApplicationContext());
            mBuilder.setPriority(isEnableSound ? PRIORITY_HIGH : PRIORITY_LOW)
                    .setAutoCancel(false)
                    .setSmallIcon(R.drawable.small_icon_email)
                    .setTicker(getString(R.string.the_new_mail_has_arrived))
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.launcher_icon))
                    .setContentTitle(fromName)
                    .setContentText(title)
                    .setContentIntent(contentIntent);

            // Check notification setting and config notification
            if (isEnableSound) {
                mBuilder.setSound(soundUri);
            } else {
                mBuilder.setSound(null);
            }

            if (isEnableVibrate) {
                mBuilder.setVibrate(vibrate);
                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(1000);
            } else {
                final long[] noVibrate = new long[]{0, 0, 0, 0, 0};
                mBuilder.setVibrate(noVibrate);
                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(0);
            }

            Notification notification = mBuilder.build();
            notification.number = 100;



            /** STYLE BIG TEXT */
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            String bigText = "<font color='#878787'>" + title + "</font>";
            if (!TextUtils.isEmpty(content.replaceAll("&nbsp;", "").trim())) {
                bigText = bigText + "<br/>" + content;
            }

            bigTextStyle.bigText(Html.fromHtml(bigText));
            bigTextStyle.setSummaryText(toAddress);
            mBuilder.setStyle(bigTextStyle);
            //consider using setTicker of Notification.Builder
            if (isNewMail) {
                if (isTime) {
                    if (TimeUtils.isBetweenTime(strFromTime, strToTime)) {
                        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
                        startForeground((int) System.currentTimeMillis(), notification);
                    }
                } else {
                    mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
                    startForeground((int) System.currentTimeMillis(), notification);
                }
            }
        }
    }

}