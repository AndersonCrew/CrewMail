package com.dazone.crewemail.activities.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dazone.crewemail.R;
import com.dazone.crewemail.activities.BaseActivity;
import com.dazone.crewemail.event.PinEvent;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.Util;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Dat on 5/5/2016.
 */
public class PinActivity extends BaseActivity implements View.OnClickListener {
    /**
     * VIEW
     */
    private ImageView btnBack;
    private TextView number0;
    private TextView number1;
    private TextView number2;
    private TextView number3;
    private TextView number4;
    private TextView number5;
    private TextView number6;
    private TextView number7;
    private TextView number8;
    private TextView number9;
    private TextView tvTitle;
    private TextView tvInstruction;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioButton radioButton4;
    private RelativeLayout btnDelete;
    private RelativeLayout btnBack2;


    /**
     * PARAMS
     */
    public int typePIN;
    private int stepPIN = 1;
    private String strPIN = "";
    private String strPINStep1 = "";
    private String strPINStep2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        initView();
        getDataFromIntent();
        setTitlePIN();
        getData();
    }




    private void initView() {
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        number0 = findViewById(R.id.number_0);
        number1 = findViewById(R.id.number_1);
        number2 = findViewById(R.id.number_2);
        number3 = findViewById(R.id.number_3);
        number4 = findViewById(R.id.number_4);
        number5 = findViewById(R.id.number_5);
        number6 = findViewById(R.id.number_6);
        number7 = findViewById(R.id.number_7);
        number8 = findViewById(R.id.number_8);
        number9 = findViewById(R.id.number_9);
        number0.setOnClickListener(this);
        number1.setOnClickListener(this);
        number2.setOnClickListener(this);
        number3.setOnClickListener(this);
        number4.setOnClickListener(this);
        number5.setOnClickListener(this);
        number6.setOnClickListener(this);
        number7.setOnClickListener(this);
        number8.setOnClickListener(this);
        number9.setOnClickListener(this);
        tvTitle = findViewById(R.id.tv_title);
        tvInstruction = findViewById(R.id.tv_instruction);
        radioButton1 = findViewById(R.id.radio_1);
        radioButton2 = findViewById(R.id.radio_2);
        radioButton3 = findViewById(R.id.radio_3);
        radioButton4 = findViewById(R.id.radio_4);
        btnBack2 = findViewById(R.id.btn_back_2);
        btnBack2.setOnClickListener(this);
        btnDelete = findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(this);

    }

    /**
     * GET DATA FROM INTENT
     */
    private void getDataFromIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            typePIN = bundle.getInt(Statics.KEY_INTENT_TYPE_PIN, 0);
        }
    }

    /**
     * SET TITLE PIN
     */
    private void setTitlePIN() {
        switch (typePIN) {
            case Statics.TYPE_PIN_SET:
                tvTitle.setText(Util.getString(R.string.pin_title_1));
                tvInstruction.setText(Util.getString(R.string.pin_instruction_1_1));
                break;
            case Statics.TYPE_PIN_REMOVE:
                tvTitle.setText(Util.getString(R.string.pin_title_2));
                tvInstruction.setText(Util.getString(R.string.pin_instruction_2_1));
                break;
            case Statics.TYPE_PIN_CHANGE:
                tvTitle.setText(Util.getString(R.string.pin_title_3));
                tvInstruction.setText(Util.getString(R.string.pin_instruction_3_1));
                break;
            case Statics.TYPE_PIN_CONFIRM:
                tvTitle.setText(Util.getString(R.string.pin_title_4));
                tvInstruction.setText(Util.getString(R.string.pin_instruction_4_1));
                tvTitle.setPadding(Util.getDimenInPx(R.dimen.padding_block), 0, 0, 0);
                btnBack.setVisibility(View.GONE);
                btnBack2.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void getData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
            case R.id.btn_back_2:
                onBack();
                break;
            case R.id.number_0:
                addStrPIN("0");
                break;
            case R.id.number_1:
                addStrPIN("1");
                break;
            case R.id.number_2:
                addStrPIN("2");
                break;
            case R.id.number_3:
                addStrPIN("3");
                break;
            case R.id.number_4:
                addStrPIN("4");
                break;
            case R.id.number_5:
                addStrPIN("5");
                break;
            case R.id.number_6:
                addStrPIN("6");
                break;
            case R.id.number_7:
                addStrPIN("7");
                break;
            case R.id.number_8:
                addStrPIN("8");
                break;
            case R.id.number_9:
                addStrPIN("9");
                break;
            case R.id.btn_delete:
                if (strPIN.length() > 0) {
                    strPIN = strPIN.substring(0, strPIN.length() - 1);
                    setRadio(strPIN.length());
                }
                break;
        }
    }

    private void addStrPIN(String number) {
        if (strPIN.length() < 4) {
            strPIN += number + "";
        }
        if (strPIN.length() == 4) {
            switch (typePIN) {
                case Statics.TYPE_PIN_SET:
                    stepByStepSet();
                    break;
                case Statics.TYPE_PIN_REMOVE:
                    stepByStepRemove();
                    break;
                case Statics.TYPE_PIN_CHANGE:
                    stepByStepChange();
                case Statics.TYPE_PIN_CONFIRM:
                    stepByStepConfirm();
                    break;
            }

        }
        setRadio(strPIN.length());
    }

    /**
     * STEP BY STEP SET PASSCODE
     */
    private void stepByStepSet() {
        switch (stepPIN) {
            case 1:
                strPINStep1 = strPIN;
                strPIN = "";
                tvInstruction.setText(Util.getString(R.string.pin_instruction_1_2));
                stepPIN = 2;
                break;
            case 2:
                strPINStep2 = strPIN;
                if (strPINStep1.equals(strPINStep2)) {
                    new Prefs().putStringValue(Statics.KEY_PREFERENCES_PIN, strPINStep1);
                    PinEvent event = new PinEvent(this.strPIN);
                    event.setTitle(getString(R.string.remove_passcode));
                    EventBus.getDefault().post(event);
                    finish();
                } else {
                    strPIN = "";
                    tvInstruction.setText(Util.getString(R.string.pin_instruction_error));
                }
                break;
        }
    }

    /**
     * STEP BY STEP REMOVE PASSCODE
     */
    private void stepByStepRemove() {
        switch (stepPIN) {
            case 1:
                String strPIN = new Prefs().getStringValue(Statics.KEY_PREFERENCES_PIN, "");
                if (this.strPIN.equals(strPIN)) {
                    new Prefs().putStringValue(Statics.KEY_PREFERENCES_PIN, "");
                    PinEvent event = new PinEvent(this.strPIN);
                    event.setTitle(getString(R.string.setting_general_set_password_code));
                    EventBus.getDefault().post(event);
                    finish();
                } else {
                    this.strPIN = "";
                    tvInstruction.setText(Util.getString(R.string.pin_instruction_error));
                }
                break;
        }
    }

    /**
     * STEP BY STEP CHANGE PASSCODE
     */
    private void stepByStepChange() {
        switch (stepPIN) {
            case 1:
                String strPIN = new Prefs().getStringValue(Statics.KEY_PREFERENCES_PIN, "");
                if (strPIN.equals(this.strPIN)) {
                    stepPIN = 2;
                    tvInstruction.setText(Util.getString(R.string.pin_instruction_3_2));
                } else {
                    tvInstruction.setText(Util.getString(R.string.pin_instruction_error));
                }
                this.strPIN = "";
                break;
            case 2:
                strPINStep1 = this.strPIN;
                this.strPIN = "";
                tvInstruction.setText(Util.getString(R.string.pin_instruction_3_3));
                stepPIN = 3;
                break;
            case 3:
                strPINStep2 = this.strPIN;
                if (strPINStep1.equals(strPINStep2)) {
                    new Prefs().putStringValue(Statics.KEY_PREFERENCES_PIN, strPINStep1);
                    finish();
                } else {
                    this.strPIN = "";
                    tvInstruction.setText(Util.getString(R.string.pin_instruction_error));
                }
                break;
        }
    }

    /**
     * STEP BY STEP CONFIRM PASSCODE
     */
    private void stepByStepConfirm() {
        switch (stepPIN) {
            case 1:
                String strPIN = new Prefs().getStringValue(Statics.KEY_PREFERENCES_PIN, "");
                if (strPIN.equals(this.strPIN)) {
                    PinEvent event = new PinEvent(this.strPIN);
                    event.setTitle("Remove Passcode");
                    EventBus.getDefault().post(event);
                    finish();
                } else {
                    tvInstruction.setText(Util.getString(R.string.pin_instruction_error));
                }
                this.strPIN = "";
                break;
        }
    }

    private void setRadio(int lengthStr) {
        switch (lengthStr) {
            case 1:
                radioButton1.setChecked(true);
                radioButton2.setChecked(false);
                radioButton3.setChecked(false);
                radioButton4.setChecked(false);
                break;
            case 2:
                radioButton1.setChecked(true);
                radioButton2.setChecked(true);
                radioButton3.setChecked(false);
                radioButton4.setChecked(false);
                break;
            case 3:
                radioButton1.setChecked(true);
                radioButton2.setChecked(true);
                radioButton3.setChecked(true);
                radioButton4.setChecked(false);
                break;
            case 4:
                radioButton1.setChecked(true);
                radioButton2.setChecked(true);
                radioButton3.setChecked(true);
                radioButton4.setChecked(true);
                break;
            default:
                radioButton1.setChecked(false);
                radioButton2.setChecked(false);
                radioButton3.setChecked(false);
                radioButton4.setChecked(false);
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
}
