package com.dazone.crewemail.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.customviews.MailMenuView;
import com.dazone.crewemail.data.UserData;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Created by Sherry on 12/9/15.
 */
public abstract class ToolBarActivity extends BaseActivity {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean isCheckCompose = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        findViewById(R.id.fab).setVisibility(View.GONE);
        ((DrawerLayout) findViewById(R.id.drawer_layout)).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        addFragment(savedInstanceState, R.id.main_container);
    }

    /***** tool bar function ****/
    public void displayToolBarBackButton(boolean enableHome) {
        ActionBar actionBar = getSupportActionBar();
        if (enableHome && actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    public void displayToolBarBackButton(boolean enableHome, boolean isCustomBack) {
        ActionBar actionBar = getSupportActionBar();
        if (enableHome && actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            if (isCustomBack)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_white_24dp);
        }
    }

    public void displayToolBarBackButtonNew(boolean enableHome, boolean isCheckCompose) {
        ActionBar actionBar = getSupportActionBar();
        if (enableHome && actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            this.isCheckCompose = isCheckCompose;
        }
    }

    public void hideToolBarBackButton() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
    }


    public void setToolBarTitle(String title) {
        if (getSupportActionBar() != null) {
            if (TextUtils.isEmpty(title)) {
                getSupportActionBar().setTitle("");
            } else {
                getSupportActionBar().setTitle(title);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isCheckCompose) {
            int id = item.getItemId();
            switch (id) {
                case android.R.id.home:
                    if (getSupportFragmentManager().getBackStackEntryCount() != 0) {

                        getSupportFragmentManager().popBackStackImmediate();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    /***** end tool bar function ****/

    /**
     * Show navigation header user information
     */
    private void displayUserInformation() {
        UserData userData = UserData.getUserInformation();
        ((TextView) findViewById(R.id.nav_header_full_name)).setText(userData.getFullName());
        ((TextView) findViewById(R.id.nav_header_email)).setText(userData.getmEmail());

        String rootLink = DaZoneApplication.getInstance().getPrefs().getServerSite();
        Uri imageUri = Uri.parse(rootLink + userData.getAvatar());

        SimpleDraweeView draweeView = findViewById(R.id.nav_header_avatar);
        draweeView.setImageURI(imageUri);
        RelativeLayout imgSetting = findViewById(R.id.setting);
        imgSetting.setOnClickListener(v -> {
            Intent intent = new Intent(ToolBarActivity.this, MailProfile.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    /**
     * show the navigation bar
     */
    public MailMenuView displayNavigationBar(MailMenuView.OnMenuItemClickListener clickListener) {
        displayUserInformation();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setDrawerState(true);
        mDrawerToggle.syncState();
        return addItemToNavigationBar(mDrawerLayout, clickListener);
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return mDrawerToggle;
    }

    /**
     * Override this method if you want to change displayAsFolder navigation item
     */
    protected MailMenuView addItemToNavigationBar(DrawerLayout drawer, MailMenuView.OnMenuItemClickListener clickListener) {
        // get menu item data and displayAsFolder to navigation
        LinearLayout menuLayout = findViewById(R.id.nav_menu_item_wrapper);
        final MailMenuView menuView = new MailMenuView(ToolBarActivity.this, menuLayout, drawer);
        menuView.setOnMenuItemClickListener(clickListener);
        menuView.initMenu(false);
        return menuView;
    }

    public void setDrawerState(boolean isEnabled) {
        if (mDrawerLayout == null) return;
        if (isEnabled) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerToggle.syncState();

        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mDrawerToggle.syncState();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * displayAsFolder floating button to view and return itself
     */
    protected FloatingActionButton displayFloatingButton(boolean task) {
        FloatingActionButton fab = findViewById(R.id.fab);
        if (task)
            fab.setVisibility(View.VISIBLE);
        else
            fab.setVisibility(View.GONE);
        fab.setOnClickListener(view -> {
            callActivity(ActivityMailCreate.class);
        });

        return fab;
    }

    protected abstract void addFragment(Bundle bundle, int mainContainer);

}
