package com.dazone.crewemail.activities;

import android.os.Bundle;

import com.dazone.crewemail.R;
import com.dazone.crewemail.fragments.MoveMailBoxFragment;
import com.dazone.crewemail.utils.StaticsBundle;
import com.dazone.crewemail.utils.Util;

/**
 * Created by Sherry on 1/6/16.
 */
public class MoveMailBoxActivity extends ToolBarActivity {

    private int task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayToolBarBackButton(true);
        setToolBarTitle(getString(R.string.string_title_select_box));
    }

    @Override
    protected void addFragment(Bundle bundle, int mainContainer) {
        Bundle bundleq = getIntent().getExtras();
        if (bundleq != null) {
            try {
                task = bundleq.getInt(StaticsBundle.BUNDLE_TASK);
            } catch (Exception e) {
                task = 1;
            }
        }

        MoveMailBoxFragment fm = MoveMailBoxFragment.newInstance(task);
        Util.replaceFragment(getSupportFragmentManager(), fm, mainContainer, false);
    }
}
