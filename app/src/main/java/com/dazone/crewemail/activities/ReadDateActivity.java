package com.dazone.crewemail.activities;

import android.os.Bundle;

import com.dazone.crewemail.R;
import com.dazone.crewemail.fragments.ReadDateFragment;
import com.dazone.crewemail.utils.StaticsBundle;
import com.dazone.crewemail.utils.Util;

/**
 * Created by THANHTUNG on 02/02/2016.
 */
public class ReadDateActivity extends ToolBarActivity {
    private ReadDateFragment fm;
    private long mailNo;
    private String time, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolBarTitle(getString(R.string.string_confirm));
        displayToolBarBackButtonNew(true, true);
    }

    @Override
    protected void addFragment(Bundle bundle, int mainContainer) {
        Bundle bundleq = getIntent().getExtras();
        if (bundleq != null) {
            try {
                mailNo = bundleq.getLong(StaticsBundle.BUNDLE_MAIL_NO_NEW);
            } catch (Exception e) {
                mailNo = 0;
            }

            try {
                time = bundleq.getString(StaticsBundle.BUNDLE_TIME);
            } catch (Exception e) {
                time = "";
            }

            try {
                title = bundleq.getString(StaticsBundle.BUNDLE_TITLE);
            } catch (Exception e) {
                title = "";
            }
        }
        if (bundle == null) {
            fm = ReadDateFragment.newInstance(mailNo, time, title);
            Util.replaceFragment(getSupportFragmentManager(), fm, mainContainer, false);
        }
    }

    @Override
    public void onBackPressed() {
        if (fm != null) {
            if (fm.checkSelect() == true) {
                fm.cancelSelect();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }
}
