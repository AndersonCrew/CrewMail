package com.dazone.crewemail.activities.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dazone.crewemail.R;
import com.dazone.crewemail.activities.BaseActivity;
import com.dazone.crewemail.event.PinEvent;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Dat on 5/5/2016.
 */
public class GeneralSettingActivity extends BaseActivity implements View.OnClickListener {
    /**
     * VIEW
     */
    private RelativeLayout layoutSetPassCode;
    private RelativeLayout layoutAdjust;
    private ImageView btnBack;
    private ImageView statusPasscode;
    private ImageView statusAdjust;
    private LinearLayout layoutChangePasscode;
    private TextView tvSetPasscode;

    /**
     * PARAMS
     */
    private String strPIN = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_general);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        tvSetPasscode = findViewById(R.id.activity_setting_general_tv_set_passcode);
        layoutSetPassCode = findViewById(R.id.layout_set_pass_code);
        layoutSetPassCode.setOnClickListener(this);
        statusPasscode = findViewById(R.id.status_set_passcode);
        layoutChangePasscode = findViewById(R.id.layout_change_passcode);
        layoutChangePasscode.setOnClickListener(this);
        statusAdjust = findViewById(R.id.status_adjust);
        layoutAdjust = findViewById(R.id.layout_adjust);
        layoutAdjust.setOnClickListener(this);

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
        /** SET STATUS PASSCODE*/
        strPIN = new Prefs().getStringValue(Statics.KEY_PREFERENCES_PIN, "");
        if (TextUtils.isEmpty((strPIN.trim()))) {
            layoutChangePasscode.setVisibility(View.GONE);
            statusPasscode.setBackgroundResource(R.drawable.bg_circle_gray);
            tvSetPasscode.setText(getString(R.string.setting_general_set_password_code));

        } else {
            layoutChangePasscode.setVisibility(View.VISIBLE);
            statusPasscode.setBackgroundResource(R.drawable.bg_circle_blue);
            tvSetPasscode.setText(getString(R.string.remove_passcode));

        }

        /** SET STATUS ADJUST TO SCREEN WIDTH */
        boolean isAdjust = new Prefs().getBooleanValue(Statics.KEY_PREFERENCES_ADJUST_TO_SCREEN_WIDTH, true);
        if (isAdjust) {
            statusAdjust.setBackgroundResource(R.drawable.bg_circle_blue);
        } else {
            statusAdjust.setBackgroundResource(R.drawable.bg_circle_gray);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBack();
                break;
            case R.id.layout_set_pass_code:
                Intent intent = new Intent(GeneralSettingActivity.this, PinActivity.class);
                intent.putExtra(Statics.KEY_INTENT_TYPE_PIN, TextUtils.isEmpty(strPIN.trim()) ? Statics.TYPE_PIN_SET : Statics.TYPE_PIN_REMOVE);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.layout_change_passcode:
                Intent intent2 = new Intent(GeneralSettingActivity.this, PinActivity.class);
                intent2.putExtra(Statics.KEY_INTENT_TYPE_PIN, Statics.TYPE_PIN_CHANGE);
                startActivity(intent2);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.layout_adjust:
                boolean isAdjust = new Prefs().getBooleanValue(Statics.KEY_PREFERENCES_ADJUST_TO_SCREEN_WIDTH, true);
                if (isAdjust) {
                    new Prefs().putBooleanValue(Statics.KEY_PREFERENCES_ADJUST_TO_SCREEN_WIDTH, false);
                    statusAdjust.setBackgroundResource(R.drawable.bg_circle_gray);
                } else {
                    new Prefs().putBooleanValue(Statics.KEY_PREFERENCES_ADJUST_TO_SCREEN_WIDTH, true);
                    statusAdjust.setBackgroundResource(R.drawable.bg_circle_blue);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    private void onBack() {
        overridePendingTransition(R.anim.finish_activity_show, R.anim.finish_activity_hide);
        finish();
    }

    @Subscribe
    public void onPinEvent(PinEvent event) {
        if (event != null) {
            tvSetPasscode.setText(event.getTitle());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
