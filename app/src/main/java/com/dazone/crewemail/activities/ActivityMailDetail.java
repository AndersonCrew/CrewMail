package com.dazone.crewemail.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.dazone.crewemail.fragments.FragmentMailDetail;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.StaticsBundle;
import com.dazone.crewemail.utils.Util;

/**
 * Created by THANHTUNG on 21/12/2015.
 */
public class ActivityMailDetail extends ToolBarActivity {
    FragmentMailDetail fragmentMailDetail;
    private long mailNo = 2505;
    private String a = "";
    private boolean isRead = false;
    private boolean isFromNotification;
    private String mailBoxNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayToolBarBackButton(true);
        setTitle("");
    }

    @Override
    protected void addFragment(Bundle bundle, int mainContainer) {
        a = getIntent().getStringExtra(StaticsBundle.BUNDLE_MAIL_BOX_CLASS_NAME);
        mailNo = getIntent().getLongExtra(StaticsBundle.BUNDLE_MAIL_NO, 2505);
        isRead = getIntent().getBooleanExtra(StaticsBundle.PREFS_KEY_ISREAD, true);

        isFromNotification = getIntent().getBooleanExtra(StaticsBundle.BUNDLE_MAIL_FROM_NOTIFICATION, false);

        if (isFromNotification) {
            mailBoxNo = getIntent().getStringExtra(StaticsBundle.BUNDLE_MAIL_FROM_NOTIFICATION_MAILBOX_NO);
            new Prefs().putLongValue(Statics.SAVE_BOX_NO_PREF, Long.parseLong(mailBoxNo));
        }

        if (bundle == null) {
            fragmentMailDetail = FragmentMailDetail.newInstance(mailNo, a, isRead);
            Util.addFragmentToActivity(getSupportFragmentManager(), fragmentMailDetail, mainContainer, false);
        }
    }

    @Override
    public void onBackPressed() {
        if (isFromNotification) {
            Intent intent = new Intent(this, ListEmailActivity.class);
            intent.putExtra(StaticsBundle.BUNDLE_MAIL_FROM_NOTIFICATION_MAILBOX_NO, mailBoxNo);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                    getSupportFragmentManager().popBackStackImmediate();
                } else {
                    if (isFromNotification) {
                        Intent intent = new Intent(this, ListEmailActivity.class);
                        intent.putExtra(StaticsBundle.BUNDLE_MAIL_FROM_NOTIFICATION_MAILBOX_NO, mailBoxNo);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        finish();
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
