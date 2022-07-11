package com.dazone.crewemail.activities.setting;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.activities.BaseActivity;
import com.dazone.crewemail.activities.ChangePasswordActivity;
import com.dazone.crewemail.data.MailProfileData;
import com.dazone.crewemail.data.UserData;
import com.dazone.crewemail.databinding.ActivityNewProfileBinding;
import com.dazone.crewemail.interfaces.OnclickCallBack;
import com.dazone.crewemail.utils.PreferenceUtilities;
import com.dazone.crewemail.viewmodel.MyProfileViewModel;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.picasso.Picasso;

/**
 * Created by dazone on 4/27/2017.
 */

public class SettingActivity extends BaseActivity implements OnclickCallBack {
    private static final int REQUEST_CALL = 1;
    private ProgressDialog pdia;
    private UserData userData;
    private Intent callIntent;
    private MailProfileData mailProfileData;
    LinearLayout ln_entrance,ln_birthday;
    public SettingActivity(MailProfileData mailProfileData) {
        this.mailProfileData = mailProfileData;
    }

    public SettingActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNewProfileBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_new_profile);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        Fresco.initialize(this);
        mailProfileData = (MailProfileData) getIntent().getExtras().getSerializable("PROFILE");
        userData = UserData.getUserInformation();
        PreferenceUtilities preferenceUtilities = DaZoneApplication.getInstance().getPreferenceUtilities();
        userData.setDomain(preferenceUtilities.getDomain());
        binding.setProfile(mailProfileData);
        binding.setUser(userData);

        MyProfileViewModel myProfileViewModel = new MyProfileViewModel(mailProfileData, this);
        binding.setMyprofile(myProfileViewModel);
        binding.setCallback(this);

        ln_entrance= findViewById(R.id.ln_entrance);
        ln_birthday= findViewById(R.id.ln_birthday);
        if(new PreferenceUtilities().getDisPlayEntrance()){
            ln_entrance.setVisibility(View.VISIBLE);
        }else {
            ln_entrance.setVisibility(View.INVISIBLE);
        }
        if(new PreferenceUtilities().getDisPlayBirthday()){
            ln_birthday.setVisibility(View.VISIBLE);
        }else {
            ln_birthday.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClickCall(View view, String phonenumber) {
        if (!TextUtils.isEmpty(phonenumber)) {
            callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phonenumber));


            if (ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SettingActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                startActivity(callIntent);
            }
        }

    }

    @Override
    public void onClickMessage(View view, String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)));
    }

    @Override
    public void onClickSendEmail(View view, String Email) {

    }

    @Override
    public void onClickBack(View view) {
        onBackPressed();
    }

    @Override
    public void onClickChangePass(View view, MailProfileData data) {

        Intent intent3 = new Intent(SettingActivity.this, ChangePasswordActivity.class);
        startActivity(intent3);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, String url) {
        Picasso.with(imageView.getContext()).load(DaZoneApplication.getInstance().getPrefs().getServerSite() + url).into(imageView);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent);
                } else {
                }
            }
        }
    }


}
