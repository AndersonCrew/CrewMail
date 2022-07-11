package com.dazone.crewemail.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.dazone.crewemail.BuildConfig;
import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.activities.setting.GeneralSettingActivity;
import com.dazone.crewemail.activities.setting.NotificationSettingActivity;
import com.dazone.crewemail.activities.setting.SettingActivity;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.MailProfileData;
import com.dazone.crewemail.data.UserData;
import com.dazone.crewemail.database.DataManager;
import com.dazone.crewemail.dialog.DialogUtil;
import com.dazone.crewemail.interfaces.BaseHTTPCallBack;
import com.dazone.crewemail.interfaces.OnGetInfoUser;
import com.dazone.crewemail.utils.Util;
import com.dazone.crewemail.webservices.HttpRequest;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import de.hdodenhof.circleimageview.CircleImageView;

public class MailProfile extends BaseActivity implements View.OnClickListener {
    public ImageLoader imageLoader = ImageLoader.getInstance();
    private CircleImageView imgAvatar;
    private MailProfileData item;
    private UserData userData;
    private ProgressDialog pdia;
    private ImageView btn_back;
    private LinearLayout ln_general;
    private LinearLayout ln_notify;
    private LinearLayout ln_logout;
    private LinearLayout ln_about;
    private LinearLayout ln_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_profile);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        pdia = new ProgressDialog(this);
        setTitle("");
        Fresco.initialize(this);
        userData = UserData.getUserInformation();
        initControl();
        getDataFromServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initControl() {
        imgAvatar = findViewById(R.id.img_avatar);
        btn_back = findViewById(R.id.btn_back);
        ln_general = findViewById(R.id.ln_general);
        ln_notify = findViewById(R.id.ln_notify);
        ln_profile = findViewById(R.id.ln_profile);
        ln_about = findViewById(R.id.ln_about);
        ln_logout = findViewById(R.id.ln_logout);

        ln_notify.setOnClickListener(this);
        ln_profile.setOnClickListener(this);
        ln_logout.setOnClickListener(this);
        ln_general.setOnClickListener(this);
        ln_about.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    private void getDataFromServer() {
        HttpRequest.getInstance().getInfoUser(new OnGetInfoUser() {
            @Override
            public void onGetInfoUserSuccess(MailProfileData commentList) {
                if (commentList != null) {
                    item = commentList;
                    bindingData();
                }
                pdia.dismiss();
            }

            @Override
            public void onGetInfoUserFail(ErrorData errorDto) {
                pdia.dismiss();
                setDataTemp();
            }

        }, userData.getId());
    }

    private void bindingData() {
        String rootLink = DaZoneApplication.getInstance().getPrefs().getServerSite();
        Uri imageUri = Uri.parse(rootLink + item.getAvatar());
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(5)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())
                .build();
        imageLoader.init(config);
        imageLoader.displayImage(String.valueOf(imageUri), imgAvatar);
    }

    public void setDataTemp() {
        String rootLink = DaZoneApplication.getInstance().getPrefs().getServerSite();
        Uri imageUri = Uri.parse(rootLink + userData.getAvatar());
        imgAvatar.setImageURI(imageUri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_logout:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.ln_profile:
                Intent intent3 = new Intent(MailProfile.this, SettingActivity.class);
                intent3.putExtra("PROFILE", item);
                startActivity(intent3);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.ln_about:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.about));

                String versionName = BuildConfig.VERSION_NAME;
                String user_version = getResources().getString(R.string.user_version) + " " + versionName;
                DialogUtil.oneButtonAlertDialog(this, getString(R.string.about), user_version, getString(R.string.confirm));
                break;
            case R.id.ln_general:
                Intent intent = new Intent(MailProfile.this, GeneralSettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.ln_notify:
                Intent intent2 = new Intent(MailProfile.this, NotificationSettingActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.ln_logout:
                logoutAlert();
                break;
        }
    }

    private void logoutAlert() {
        DialogUtil.customAlertDialog(this, getString(R.string.are_you_sure_loguot),
                getString(R.string.auto_login_button_yes), getString(R.string.auto_login_button_no), new DialogUtil.OnAlertDialogViewClickEvent() {
                    @Override
                    public void onOkClick(DialogInterface alertDialog) {
                        HttpRequest.getInstance().Logout(new BaseHTTPCallBack() {
                            @Override
                            public void onHTTPSuccess() {
                                Util.printLogs("SUCCESS.");
                                UserData.getInstance().logout(getApplicationContext());
                                DataManager.Instance().clearData();
                                Intent intent = new Intent(MailProfile.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }

                            @Override
                            public void onHTTPFail(ErrorData errorDto) {
                                Util.printLogs("FAIL.");
                            }
                        });
                    }

                    @Override
                    public void onCancelClick() {

                    }
                });
    }
}
