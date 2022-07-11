package com.dazone.crewemail.activities;

import android.content.Intent;
import android.os.Bundle;

import com.dazone.crewemail.R;
import com.dazone.crewemail.data.MailBoxData;
import com.dazone.crewemail.data.PersonData;
import com.dazone.crewemail.fragments.FragmentMailCreate;
import com.dazone.crewemail.utils.Constants;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.StaticsBundle;
import com.dazone.crewemail.utils.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by THANHTUNG on 16/12/2015.
 */
public class ActivityMailCreate extends ToolBarActivity {
    private FragmentMailCreate fm;
    //task: 0- forward  1- Reply  2- Reply All 3- Draft 4- Compose
    private int task = 4;
    private MailBoxData mailBoxData;
    private String className;
    private long mailNo;
    public static boolean isBack=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayToolBarBackButtonNew(true, true);
    }

    @Override
    protected void addFragment(Bundle bundle, int mainContainer) {
        Bundle bundle1 = getIntent().getExtras();
        if (bundle1 != null) {
            try {
                task = bundle1.getInt(StaticsBundle.BUNDLE_MAIL_DETAIL_TASK);
            } catch (Exception e) {
                task = 4;
            }

            try {
                mailBoxData = (MailBoxData) bundle1.getSerializable(StaticsBundle.BUNDLE_MAIL_CREATE_FROM_DETAIL);
            } catch (Exception e) {
                mailBoxData = null;
            }

            try {
                className = bundle1.getString(StaticsBundle.BUNDLE_MAIL_BOX_CLASS_NAME);
            } catch (Exception e) {
                className = "";
            }

            try {
                mailNo = bundle1.getLong(StaticsBundle.BUNDLE_MAIL_NO);
            } catch (Exception e) {
                mailNo = 0;
            }
        }

        if (task == 0)
            setTitle(getString(R.string.string_title_mail_detail_forward));
        else if (task == 1)
            setTitle(getString(R.string.string_title_mail_detail_reply));
        else if (task == 2)
            setTitle(getString(R.string.string_title_mail_detail_reply_all));
        else
            setTitle(getString(R.string.string_title_compose_email));

        if (bundle == null) {
            fm = FragmentMailCreate.newInstance(task, mailBoxData, className, mailNo);
            Util.addFragmentToActivity(getSupportFragmentManager(), fm, mainContainer, false);
        }
    }

    @Override
    public void onBackPressed() {
        if (fm != null) {
            if (fm.checkDraft(1)) {
                fm.DrafMail();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }
}
