package com.dazone.crewemail.activities.setting;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.dazone.crewemail.R;
import com.dazone.crewemail.activities.BaseActivity;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.interfaces.BaseHTTPCallBack;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.TimeUtils;
import com.dazone.crewemail.utils.Util;
import com.dazone.crewemail.webservices.HttpRequest;

import java.util.Calendar;

/**
 * Created by Dat on 5/5/2016.
 */
public class NotificationSettingActivity extends BaseActivity implements View.OnClickListener, SwitchCompat.OnCheckedChangeListener {
    /**
     * VIEW
     */
    private ImageView btnBack;
    private SwitchCompat swNewMail;
    private SwitchCompat swSound;
    private SwitchCompat swVibrate;
    private SwitchCompat swTime;
    private TextView tvFromTime;
    private TextView tvToTime,tv_notification_time;

    private String strToTime;
    private String strFromTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notification);
        initView();
    }

    private void initView() {
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        swNewMail = findViewById(R.id.switch_new_mail);
        swSound = findViewById(R.id.switch_sound);
        swVibrate = findViewById(R.id.switch_vibrate);
        swTime = findViewById(R.id.switch_time);
        swNewMail.setOnCheckedChangeListener(this);
        swSound.setOnCheckedChangeListener(this);
        swVibrate.setOnCheckedChangeListener(this);
        swTime.setOnCheckedChangeListener(this);
        tvFromTime = findViewById(R.id.tv_from_time);
        tvToTime = findViewById(R.id.tv_to_time);
        tv_notification_time = findViewById(R.id.tv_notification_time);
        tvFromTime.setOnClickListener(this);
        tvToTime.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    /**
     * GET DATA FROM PREFERENCES
     */
    private void getData() {
        HttpRequest.getInstance().getNotificationSetting(new BaseHTTPCallBack() {
            @Override
            public void onHTTPSuccess() {
                swNewMail.setChecked(new Prefs().getBooleanValue(Statics.KEY_PREFERENCES_NOTIFICATION_NEW_MAIL, true));
                swSound.setChecked(new Prefs().getBooleanValue(Statics.KEY_PREFERENCES_NOTIFICATION_SOUND, true));
                swVibrate.setChecked(new Prefs().getBooleanValue(Statics.KEY_PREFERENCES_NOTIFICATION_VIBRATE, true));
                swTime.setChecked(new Prefs().getBooleanValue(Statics.KEY_PREFERENCES_NOTIFICATION_TIME, false));

                strToTime = new Prefs().getStringValue(Statics.KEY_PREFERENCES_NOTIFICATION_TIME_TO_TIME, Util.getString(R.string.setting_notification_to_time));
                strFromTime = new Prefs().getStringValue(Statics.KEY_PREFERENCES_NOTIFICATION_TIME_FROM_TIME, Util.getString(R.string.setting_notification_from_time));

                tvToTime.setText(strToTime);
                tvFromTime.setText(strFromTime);
                setEnable(swNewMail.isChecked());
                tv_notification_time.setText(Html.fromHtml("<font color=#878787>"
                        + getResources().getString(R.string.setting_notification_time) + "<br />" + "<small>" + getResources().getString(R.string.setting_notification_time_2)
                        + "</small>"));
            }

            @Override
            public void onHTTPFail(ErrorData errorDto) {

            }

        });


    }

    @Override
    protected void onPause() {
        super.onPause();

        String notiTime = "\"notitime\":";
        String endTime = "\"endtime\":";
        String startTime = "{\"starttime\":";
        String enable = "\"enabled\":";
        String StrTrue = "true,";
        String StrFalse = "false,";
        String timeStart = "\"" + strFromTime + "\",";
        String endStart = "\"" + strToTime + "\"}";
        String notificationStr = startTime + timeStart + enable +  (swNewMail.isChecked() ? StrTrue : StrFalse)
                + notiTime +  (swTime.isChecked() ? StrTrue : StrFalse) + endTime + endStart;
        String deviceId = new Prefs().getStringValue(Statics.KEY_DEVICE_ID, "");

        HttpRequest.getInstance().updateNotification(notificationStr, deviceId, new BaseHTTPCallBack() {
            @Override
            public void onHTTPSuccess() {

            }

            @Override
            public void onHTTPFail(ErrorData errorDto) {

            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBack();
                break;
            case R.id.tv_from_time:
            case R.id.tv_to_time:
                if (swTime.isChecked()&&swNewMail.isChecked())
                showTimeDialog((TextView) v);
                break;
        }
    }

    private void showTimeDialog(final TextView v) {
        Calendar mCurrentTime = TimeUtils.getTimeFromStr(v.getText().toString());
        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mCurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this,android.R.style.Theme_Material_Light_Dialog_Alert, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                switch (v.getId()) {
                    case R.id.tv_from_time:
                        strToTime = new Prefs().getStringValue(Statics.KEY_PREFERENCES_NOTIFICATION_TIME_TO_TIME, Util.getString(R.string.setting_notification_to_time));
                        strFromTime = TimeUtils.getStrFromTime(selectedHour, selectedMinute);
                        if (TimeUtils.compareTime(strFromTime, strToTime)) {
                            new Prefs().putStringValue(Statics.KEY_PREFERENCES_NOTIFICATION_TIME_FROM_TIME, strFromTime);
                            v.setText(strFromTime);
                        } else {
                            Toast.makeText(NotificationSettingActivity.this, Util.getString(R.string.setting_notification_time_error), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.tv_to_time:
                        strToTime = TimeUtils.getStrFromTime(selectedHour, selectedMinute);
                        strFromTime = new Prefs().getStringValue(Statics.KEY_PREFERENCES_NOTIFICATION_TIME_FROM_TIME, Util.getString(R.string.setting_notification_from_time));
                        if (TimeUtils.compareTime(strFromTime, strToTime)) {
                            new Prefs().putStringValue(Statics.KEY_PREFERENCES_NOTIFICATION_TIME_TO_TIME, strToTime);
                            v.setText(strToTime);
                        } else {
                            Toast.makeText(NotificationSettingActivity.this, Util.getString(R.string.setting_notification_time_error), Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle(Util.getString(R.string.time_picker_title));
        mTimePicker.show();
    }


    @Override
    public void onBackPressed() {
        onBack();
    }

    private void onBack() {
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_new_mail:
                new Prefs().putBooleanValue(Statics.KEY_PREFERENCES_NOTIFICATION_NEW_MAIL, isChecked);
                setEnable(isChecked);
                break;
            case R.id.switch_sound:
                new Prefs().putBooleanValue(Statics.KEY_PREFERENCES_NOTIFICATION_SOUND, isChecked);
                break;
            case R.id.switch_vibrate:
                new Prefs().putBooleanValue(Statics.KEY_PREFERENCES_NOTIFICATION_VIBRATE, isChecked);
                break;
            case R.id.switch_time:
                new Prefs().putBooleanValue(Statics.KEY_PREFERENCES_NOTIFICATION_TIME, isChecked);
                break;
        }
    }
    public void setEnable(boolean isChecked){
        swSound.setEnabled(isChecked);
        swVibrate.setEnabled(isChecked);
        swTime.setEnabled(isChecked);
    }
}
