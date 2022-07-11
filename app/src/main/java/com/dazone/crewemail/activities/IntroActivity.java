package com.dazone.crewemail.activities;

import static com.dazone.crewemail.utils.Util.compareVersionNames;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.dazone.crewemail.BuildConfig;
import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.activities.setting.PinActivity;
import com.dazone.crewemail.data.PersonData;
import com.dazone.crewemail.data.UserData;
import com.dazone.crewemail.database.DataManager;
import com.dazone.crewemail.event.PinEvent;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.StaticsBundle;
import com.dazone.crewemail.utils.Util;
import com.dazone.crewemail.webservices.WebClient;
import com.fasterxml.jackson.databind.JsonNode;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class IntroActivity extends BaseActivity {

    public static final int ACTIVITY_HANDLER_NEXT_ACTIVITY = 1111;
    public static final int ACTIVITY_HANDLER_START_UPDATE = 1112;
    private final ActivityHandler mActivityHandler = new ActivityHandler(this);
    private ArrayList<PersonData> personDatasRecycle = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        EventBus.getDefault().register(this);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.app_base_color_login));
        }
    }

    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            intent = new Intent(this, ActivityMailDetail.class);
            intent.putExtra(StaticsBundle.BUNDLE_MAIL_NO, Long.valueOf(sharedText));
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        handleLogin();
    }

    private void handleLogin() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        checkLogout();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                if (TextUtils.isEmpty(DaZoneApplication.getInstance().getPrefs().getServerSite())) {
                    DaZoneApplication.getInstance().getPrefs().putUserData("", "");
                    DaZoneApplication.getInstance().getPrefs().putAccessToken("");
                    Intent intent1 = new Intent(IntroActivity.this, LoginActivity.class);
                    startActivity(intent1);
                    finish();
                } else {
                    handleSendText(intent); // Handle text being sent
                }
            }
        } else {
            if (!checkPermissions()) {
                setPermissions();
            } else {
                mActivityHandler.sendEmptyMessageDelayed(ACTIVITY_HANDLER_NEXT_ACTIVITY, 1);
            }
        }
    }
    private void checkLogout() {
        String appVersion = BuildConfig.VERSION_NAME;
        if (compareVersionNames(appVersion, "2.2.9") == -1) {
            UserData.getInstance().logout(getApplicationContext());
            DataManager.Instance().clearData();
        }
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        return true;
    }

    private void setPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_WIFI_STATE
        }, MY_PERMISSIONS_REQUEST_CODE);
    }

    private final int MY_PERMISSIONS_REQUEST_CODE = 1;
    String filePath = "";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != MY_PERMISSIONS_REQUEST_CODE) {
            return;
        }

        boolean isGranted = true;

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
                break;
            }
        }

        if (isGranted) {
            startApplication();
        } else {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void startApplication() {
        Prefs prefs = DaZoneApplication.getInstance().getPrefs();
        if (!TextUtils.isEmpty(prefs.getAccessToken())) {
            String pin = new Prefs().getStringValue(Statics.KEY_PREFERENCES_PIN, "");
            if (!TextUtils.isEmpty(pin)) {
                Intent intent = new Intent(this, PinActivity.class);
                intent.putExtra(Statics.KEY_INTENT_TYPE_PIN, Statics.TYPE_PIN_CONFIRM);
                startActivity(intent);
            } else {
                if (DaZoneApplication.getInstance().offlineMode) {
                    Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    new WebClientAsync_HasApplication_v2().execute();
            }
        } else {
            Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(PinEvent event) {
        if (event != null) {
            String pin = event.getPin();
            if (pin.equals(new Prefs().getStringValue(Statics.KEY_PREFERENCES_PIN, ""))) {
                callActivity(ListEmailActivity.class);
                finish();
            }
        }
    }

    private class WebClientAsync_HasApplication_v2 extends AsyncTask<Void, Void, Void> {
        private boolean mIsFailed;
        private boolean mHasApplication;
        private String mMessage;

        @Override
        protected Void doInBackground(Void... params) {
            Prefs prefs = DaZoneApplication.getInstance().getPrefs();
            if (TextUtils.isEmpty(prefs.getServerSite())) {
                prefs.putUserData("", "");
                prefs.putAccessToken("");
                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                WebClient.HasApplication_v2(Util.getLanguageCode(), Util.getTimeZoneOffset(), "Mail3", prefs.getServerSite(), new WebClient.OnWebClientListener() {
                    @Override
                    public void onSuccess(JsonNode jsonNode) {
                        try {
                            mIsFailed = false;
                            mHasApplication = jsonNode.get("HasApplication").asBoolean();
                            mMessage = jsonNode.get("Message").asText();
                        } catch (Exception e) {
                            e.printStackTrace();

                            mIsFailed = true;
                            mHasApplication = false;
                            mMessage = getString(R.string.loginActivity_message_wrong_server_site);
                        }
                    }

                    @Override
                    public void onFailure() {
                        mIsFailed = true;
                        mHasApplication = false;

                        mMessage = getString(R.string.loginActivity_message_wrong_server_site);
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mIsFailed) {
                Toast.makeText(IntroActivity.this, mMessage, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(IntroActivity.this, ListEmailActivity.class);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                startActivity(intent);
                finish();
            } else {
                if (mHasApplication) {
                    new WebClientAsync_CheckSessionUser_v2().execute();
                } else {
                    Toast.makeText(IntroActivity.this, mMessage, Toast.LENGTH_LONG).show();
                    new WebClientAsync_Logout_v2().execute();
                }
            }
        }
    }

    private class WebClientAsync_CheckSessionUser_v2 extends AsyncTask<Void, Void, Void> {
        private boolean mIsFailed;
        private boolean mIsSuccess;

        @Override
        protected Void doInBackground(Void... params) {
            Prefs prefs = DaZoneApplication.getInstance().getPrefs();
            WebClient.CheckSessionUser_v2(Util.getLanguageCode(), Util.getTimeZoneOffset(), prefs.getAccessToken(), prefs.getServerSite(), new WebClient.OnWebClientListener() {
                @Override
                public void onSuccess(JsonNode jsonNode) {
                    mIsFailed = false;

                    try {
                        mIsSuccess = (jsonNode.get("success").asInt() == 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mIsSuccess = false;
                    }
                }

                @Override
                public void onFailure() {
                    mIsFailed = true;
                    mIsSuccess = false;
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (mIsSuccess) {
                loginSuccess(mIsSuccess);
            } else {
                Prefs prefs = DaZoneApplication.getInstance().getPrefs();
                prefs.putUserData("", "");

                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void loginSuccess(boolean mIsSuccess) {
        if (mIsSuccess) {
            callActivity(ListEmailActivity.class);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
    }

    private class WebClientAsync_Logout_v2 extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            Prefs prefs = DaZoneApplication.getInstance().getPrefs();
            WebClient.Logout_v2(prefs.getAccessToken(), prefs.getServerSite(), new WebClient.OnWebClientListener() {
                @Override
                public void onSuccess(JsonNode jsonNode) {
                }

                @Override
                public void onFailure() {
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Prefs prefs = DaZoneApplication.getInstance().getPrefs();
            prefs.putUserData("", "");
            finish();
        }
    }

    private class ActivityHandler extends Handler {
        private final WeakReference<IntroActivity> mWeakActivity;

        private ActivityHandler(IntroActivity activity) {
            mWeakActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final IntroActivity activity = mWeakActivity.get();

            if (activity != null) {
                if (msg.what == ACTIVITY_HANDLER_NEXT_ACTIVITY) {
                    startApplication();
                } else if (msg.what == ACTIVITY_HANDLER_START_UPDATE) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage(R.string.string_update_content);
                    builder.setPositiveButton(R.string.auto_login_button_yes, (dialog, which) -> {
                        new Async_DownloadApkFile(IntroActivity.this, "CrewMail").execute();
                        dialog.dismiss();
                    });
                    builder.setNegativeButton(R.string.auto_login_button_no, (dialog, which) -> {
                        dialog.dismiss();
                        startApplication();
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
        }
    }

    private class Async_DownloadApkFile extends AsyncTask<Void, Void, Void> {
        private final WeakReference<IntroActivity> mWeakActivity;
        private String mApkFileName;
        private ProgressDialog mProgressDialog = null;

        public Async_DownloadApkFile(IntroActivity activity, String apkFileName) {
            mWeakActivity = new WeakReference<>(activity);
            mApkFileName = apkFileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            IntroActivity activity = mWeakActivity.get();

            if (activity != null) {
                mProgressDialog = new ProgressDialog(activity);
                mProgressDialog.setMessage(getString(R.string.mailActivity_message_download_apk));
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            BufferedInputStream bufferedInputStream = null;
            FileOutputStream fileOutputStream = null;

            try {
                URL apkUrl = new URL("http://www.crewcloud.net/Android/Package/" + mApkFileName + ".apk");
                urlConnection = (HttpURLConnection) apkUrl.openConnection();
                inputStream = urlConnection.getInputStream();
                bufferedInputStream = new BufferedInputStream(inputStream);

                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/" + mApkFileName + "_new.apk";
                fileOutputStream = new FileOutputStream(filePath);

                byte[] buffer = new byte[4096];
                int readCount;

                while (true) {
                    readCount = bufferedInputStream.read(buffer);
                    if (readCount == -1) {
                        break;
                    }

                    fileOutputStream.write(buffer, 0, readCount);
                    fileOutputStream.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (bufferedInputStream != null) {
                    try {
                        bufferedInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (urlConnection != null) {
                    try {
                        urlConnection.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            IntroActivity activity = mWeakActivity.get();

            if (activity != null) {
                filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/" + mApkFileName + ".apk";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (checkPermissions()) {
                        startIntentUpdate(activity, filePath);
                    } else {
                        setPermissions();
                    }

                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
                    activity.startActivity(intent);
                }
            }

            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }
    }

    private void startIntentUpdate(Activity activity, String filePath) {
        Uri apkUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", new File(filePath));
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.setData(apkUri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivity(intent);
    }
}