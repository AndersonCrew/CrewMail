package com.dazone.crewemail.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.customviews.AlertDialogView;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.PersonData;
import com.dazone.crewemail.database.OrganizationUserDBHelper;
import com.dazone.crewemail.database.ServerSiteDBHelper;
import com.dazone.crewemail.interfaces.BaseHTTPCallBack;
import com.dazone.crewemail.interfaces.ICheckSSL;
import com.dazone.crewemail.interfaces.OnAutoLoginCallBack;
import com.dazone.crewemail.interfaces.OnCheckDevice;
import com.dazone.crewemail.utils.Constants;
import com.dazone.crewemail.utils.PreferenceUtilities;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.Util;
import com.dazone.crewemail.view.SoftKeyboardDetectorView;
import com.dazone.crewemail.webservices.HttpRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;

public class LoginActivity extends BaseActivity implements OnCheckDevice, BaseHTTPCallBack {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static String BROADCAST_ACTION = "com.dazone.crewcloud.account.receive";
    boolean firstLogin = true;
    private Context context;
    private EditText etUserName;
    private EditText etPassword;
    private EditText etServer;
    private ImageView img_login_logo;
    private TextView tv_login_logo_text;
    private GoogleCloudMessaging gcm;
    private String regID = "";
    private boolean isAutoLoginShow = false;
    private boolean mFirstLogin = true;
    private ArrayList<PersonData> personDatasRecycle = new ArrayList<>();
    private BroadcastReceiver mAccountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receiverPackageName = intent.getExtras().getString("receiverPackageName");
            if (LoginActivity.this.getPackageName().equals(receiverPackageName)) {
                String companyID = intent.getExtras().getString("companyID");
                String userID = intent.getExtras().getString("userID");
                if (!TextUtils.isEmpty(companyID) && !TextUtils.isEmpty(userID) && !isAutoLoginShow) {
                    isAutoLoginShow = true;
                    showPopupAutoLogin(companyID, userID);
                }
            }
        }
    };

    private boolean hasOpenNotificationSettingPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkOpenSettingNotificationPage();
    }

    @Override
    protected void onResume() {
        super.onResume();
       checkOpenSettingNotificationPage();
    }

    private void checkOpenSettingNotificationPage() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isNotificationServiceRunning()
                && !hasOpenNotificationSettingPage){
            hasOpenNotificationSettingPage = true;
            Intent intent = new Intent();
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            //for Android 5-7
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);
            // for Android O
            intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
            startActivity(intent);
        } else {
            continueCheckPermission();
        }
    }

    private void continueCheckPermission() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.app_base_color_login));
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ACTION);
        registerReceiver(mAccountReceiver, intentFilter);

        Intent intent = new Intent();
        intent.setAction("com.dazone.crewcloud.account.get");
        intent.putExtra("senderPackageName", this.getPackageName());
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);

        RelativeLayout logo = findViewById(R.id.logo);
        logo.setVisibility(View.VISIBLE);

        final SoftKeyboardDetectorView softKeyboardDetectorView = new SoftKeyboardDetectorView(this);
        addContentView(softKeyboardDetectorView, new FrameLayout.LayoutParams(-1, -1));

        softKeyboardDetectorView.setOnShownKeyboard(() -> {
            img_login_logo.setVisibility(View.GONE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv_login_logo_text.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            tv_login_logo_text.setLayoutParams(params);
        });

        softKeyboardDetectorView.setOnHiddenKeyboard(() -> {
            img_login_logo.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv_login_logo_text.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            tv_login_logo_text.setLayoutParams(params);
        });

        if (!TextUtils.isEmpty(DaZoneApplication.getInstance().getPrefs().getStringValue(Constants.DOMAIN, ""))) {
            doLogin();
        } else {
            DaZoneApplication.getInstance().getPrefs().putBooleanValue(Statics.PREFS_KEY_SESSION_ERROR, false);
            findViewById(R.id.logo).setVisibility(View.GONE);
            firstLogin = false;
            init();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mAccountReceiver);
    }

    @Override
    public void onDeviceSuccess() {
    }

    @Override
    public void onHTTPSuccess() {
        ServerSiteDBHelper.addServerSite(DaZoneApplication.getInstance().getPrefs().getStringValue(Constants.COMPANY_NAME, ""));

        createGMC();
        loginSuccess();
    }

    @Override
    public void onHTTPFail(ErrorData errorData) {
        if (firstLogin) {
            dismissProgressDialog();
            firstLogin = false;
            findViewById(R.id.logo).setVisibility(View.GONE);
            init();
        } else {
            dismissProgressDialog();
            String error_msg = errorData.getMessage();
            if (TextUtils.isEmpty(error_msg)) {
                error_msg = getString(R.string.connection_falsed);
            }
            AlertDialogView.normalAlertDialog(this, getString(R.string.app_name), error_msg, getString(R.string.string_title_mail_create_ok), null);
        }
    }

    private void loginSuccess() {
        OrganizationUserDBHelper.clearData();
        final Handler handler = new Handler();
        handler.postDelayed(() -> gotoMainScreen(), 500);
    }

    private void gotoMainScreen() {
        dismissProgressDialog();
        Intent i = new Intent(LoginActivity.this, ListEmailActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void doLogin() {
        if (Util.checkStringValue(DaZoneApplication.getInstance().getPrefs().getAccessToken()) && !DaZoneApplication.getInstance().getPrefs().getBooleanValue(Statics.PREFS_KEY_SESSION_ERROR, false)) {
            if (Util.isNetworkAvailable())
                HttpRequest.getInstance().checkLogin(DaZoneApplication.getInstance().getPrefs(), this);
            else {
                gotoMainScreen();
            }
        } else {
            DaZoneApplication.getInstance().getPrefs().putBooleanValue(Statics.PREFS_KEY_SESSION_ERROR, false);
            findViewById(R.id.logo).setVisibility(View.GONE);
            firstLogin = false;
            init();
        }
    }


    private void init() {
        img_login_logo = findViewById(R.id.img_login_logo);
        tv_login_logo_text = findViewById(R.id.tv_login_logo_text);
        etUserName = findViewById(R.id.login_edt_username);
        etPassword = findViewById(R.id.login_edt_password);
        etServer = findViewById(R.id.login_edt_server);

        etUserName.setPrivateImeOptions("defaultInputmode=english;");
        etServer.setPrivateImeOptions("defaultInputmode=english;");

        Button btnLogin = findViewById(R.id.login_btn_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnLogin.setBackgroundResource(R.drawable.ripple_effect);

        }
        etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result)) {
                    etUserName.setText(result);
                    etUserName.setSelection(result.length());
                }
            }
        });

        etPassword = findViewById(R.id.login_edt_password);
        etServer = findViewById(R.id.login_edt_server);

        etPassword.setText(new PreferenceUtilities().getPass());
        etServer.setText(new PreferenceUtilities().getDomain());
        etUserName.setText(new PreferenceUtilities().getUserId());
        etServer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result)) {
                    etServer.setText(result);
                    etServer.setSelection(result.length());
                }
            }
        });

        initForSignup();

        btnLogin.setOnClickListener(v -> {
            String username = etUserName.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String domain = etServer.getText().toString().trim();
            Util.setServerSite(domain);
            if (TextUtils.isEmpty(checkStringValue(domain, username, password))) {
                showProgressDialog();
                HttpRequest.getInstance().checkSSL(new ICheckSSL() {
                    @Override
                    public void hasSSL(boolean hasSSL) {
                        String domainCompany = Util.setServerSite(domain);
                        HttpRequest.getInstance().login(username, password, DaZoneApplication.getInstance().getPrefs(), LoginActivity.this);
                    }

                    @Override
                    public void checkSSLError(ErrorData errorData) {
                        dismissProgressDialog();
                        Toast.makeText(LoginActivity.this, "Cannot check ssl this domain!", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                AlertDialogView.normalAlertDialog(LoginActivity.this, getString(R.string.app_name), checkStringValue(domain, username, password), getString(R.string.string_title_mail_create_ok), null);
            }
        });
    }

    private boolean isNotificationServiceRunning() {
        return NotificationManagerCompat.from(this).areNotificationsEnabled();
    }

    private void initForSignup() {
        RelativeLayout btnSignUp = findViewById(R.id.login_btn_sign_up);
        btnSignUp.setOnClickListener(v -> callActivity(SignUpActivity.class));
    }

    private String checkStringValue(String server_site, String username, String password) {
        String result = "";
        if (TextUtils.isEmpty(server_site)) {
            result += getString(R.string.string_server_site);
        }

        if (TextUtils.isEmpty(username)) {
            if (TextUtils.isEmpty(result)) {
                result += getString(R.string.login_username);
            } else {
                result += ", " + getString(R.string.login_username);
            }
        }
        if (TextUtils.isEmpty(password)) {
            if (TextUtils.isEmpty(result)) {
                result += getString(R.string.password);
            } else {
                result += ", " + getString(R.string.password);
            }
        }
        if (TextUtils.isEmpty(result)) {
            return result;
        } else {
            return String.format(getString(R.string.login_empty_input), result);
        }
    }

    private void showPopupAutoLogin(final String companyID, final String userID) {
        String alert1 = Util.getString(R.string.auto_login_company_ID) + companyID;
        String alert2 = Util.getString(R.string.auto_login_user_ID) + userID;
        String alert3 = Util.getString(R.string.auto_login_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_auto_login, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        TextView tvTitle = dialogView.findViewById(R.id.tv_title_auto);
        tvTitle.setText(Util.getString(R.string.auto_login_title));
        TextView textView = dialogView.findViewById(R.id.tv_message);
        textView.setText(alert1 + "\n" + alert2 + "\n\n" + alert3);
        TextView btnYes = dialogView.findViewById(R.id.btn_yes_auto);
        TextView btnNo = dialogView.findViewById(R.id.btn_no_auto);
        btnYes.setOnClickListener(view -> {
            alertDialog.dismiss();
            autoLogin(companyID, userID);
        });
        btnNo.setOnClickListener(view -> alertDialog.dismiss());

        alertDialog.show();


    }

    public void autoLogin(final String companyID, final String userID) {
        Util.setServerSite(companyID);
        showProgressDialog();
        HttpRequest.getInstance().checkSSL(new ICheckSSL() {
            @Override
            public void hasSSL(boolean hasSSL) {
                HttpRequest.getInstance().AutoLogin(userID, DaZoneApplication.getInstance().getPrefs(), new OnAutoLoginCallBack() {
                    @Override
                    public void OnAutoLoginSuccess(String response) {
                        createGMC();
                        PreferenceUtilities preferenceUtilities = DaZoneApplication.getInstance().getPreferenceUtilities();
                        preferenceUtilities.setDomain(companyID);
                        preferenceUtilities.setPass("");
                        preferenceUtilities.setUserId(userID);
                        loginSuccess();
                    }

                    @Override
                    public void OnAutoLoginFail(ErrorData dto) {
                        if (mFirstLogin) {
                            dismissProgressDialog();

                            mFirstLogin = false;
                            init();
                        } else {
                            dismissProgressDialog();
                            String error_msg = dto.getMessage();

                            if (TextUtils.isEmpty(error_msg)) {
                                error_msg = getString(R.string.connection_falsed);
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage(error_msg).setPositiveButton("OK", null);
                            builder.create().show();
                        }
                    }
                });
            }

            @Override
            public void checkSSLError(ErrorData errorData) {
                dismissProgressDialog();
                Toast.makeText(context, "Cannot check ssl this domain!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void createGMC() {
        context = getApplicationContext();

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            if (regID.isEmpty()) {
                registerInBackground();
            } else {
                HttpRequest.getInstance().InsertDevice(regID, new BaseHTTPCallBack() {
                    @Override
                    public void onHTTPSuccess() {

                    }

                    @Override
                    public void onHTTPFail(ErrorData errorDto) {

                    }

                });
            }
        } else {
            dismissProgressDialog();
            callActivity(ListEmailActivity.class);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Util.printLogs("This device is not supported.");
            }
            return false;
        }
        return true;
    }

    private void registerInBackground() {
        new register().execute("");
    }

    public class register extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regID = gcm.register(Statics.GOOGLE_SENDER_ID_MAIL);

                new Prefs().putStringValue(Statics.KEY_DEVICE_ID, regID);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void unused) {
            new Prefs().setGCMregistrationid(regID);
            HttpRequest.getInstance().InsertDevice(regID, new BaseHTTPCallBack() {
                @Override
                public void onHTTPSuccess() {

                }

                @Override
                public void onHTTPFail(ErrorData errorDto) {

                }
            });

        }

    }
}
