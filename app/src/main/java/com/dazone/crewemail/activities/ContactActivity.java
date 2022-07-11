package com.dazone.crewemail.activities;

import android.os.Bundle;

import com.dazone.crewemail.fragments.ContactFragment;
import com.dazone.crewemail.utils.Util;

/**
 * Created by Sherry on 12/30/15.
 */
public class ContactActivity extends ToolBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayToolBarBackButton(true);
    }

    @Override
    protected void addFragment(Bundle bundle, int mainContainer) {
        if(bundle == null){
            Bundle myBundle = getIntent().getExtras();
            int type = myBundle.getInt("TYPE");
            ContactFragment fm = ContactFragment.newInstance(type);
            Util.replaceFragment(getSupportFragmentManager(), fm, mainContainer, false);
        }
    }
}
