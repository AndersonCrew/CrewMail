package com.dazone.crewemail.activities;

import static com.dazone.crewemail.utils.Statics.TYPE_OUTBOX;
import static com.dazone.crewemail.utils.StaticsBundle.PREFS_KEY_COMPOSE;
import static com.dazone.crewemail.utils.Util.compareVersionNames;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.dazone.crewemail.BuildConfig;
import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.activities.new_refactor.OrganizationViewModel;
import com.dazone.crewemail.customviews.AlertDialogView;
import com.dazone.crewemail.customviews.MailMenuView;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.MailBoxMenuData;
import com.dazone.crewemail.data.MailTagMenuData;
import com.dazone.crewemail.data.PersonData;
import com.dazone.crewemail.data.UserData;
import com.dazone.crewemail.dto.CheckUpdateDto;
import com.dazone.crewemail.event.reloadTitle;
import com.dazone.crewemail.fragments.ListEmailFragment;
import com.dazone.crewemail.interfaces.BaseHTTPCallBack;
import com.dazone.crewemail.interfaces.BaseHTTPCallBackWithString;
import com.dazone.crewemail.utils.EmailBoxStatics;
import com.dazone.crewemail.utils.PermissionUtils;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.StaticsBundle;
import com.dazone.crewemail.utils.TimeUtils;
import com.dazone.crewemail.utils.Urls;
import com.dazone.crewemail.utils.Util;
import com.dazone.crewemail.webservices.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ListEmailActivity extends ToolBarActivity implements MailMenuView.OnMenuItemClickListener {
    public static ListEmailActivity instance = null;
    private ListEmailFragment fm;
    private int mMainContainer = 0;
    private MailMenuView mailMenuView;
    public static boolean isSendMail;
    public static String urlDownload = "";
    private final ActivityHandler2 mActivityHandler2 = new ActivityHandler2(this);
    private final int MY_PERMISSIONS_REQUEST_CODE = 1;
    public static String title = "";
    private static boolean isInstallingApk = false;
    private static File apkFile = null;
    private OrganizationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(OrganizationViewModel.class);
        checkOrganization();
        displayFloatingButton(true);
        mailMenuView = displayNavigationBar(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (!DaZoneApplication.getInstance().offlineMode)
            HttpRequest.getInstance().CheckAccount(new BaseHTTPCallBack() {
                @Override
                public void onHTTPSuccess() {
                }

                @Override
                public void onHTTPFail(ErrorData errorDto) {
                    AlertDialogView.normalAlertDialogNotBack(ListEmailActivity.this, Util.getString(R.string.app_name), Util.getString(R.string.string_no_id), Util.getString(R.string.string_title_mail_create_ok),
                            Util.getString(R.string.logout), new AlertDialogView.OnAlertDialogViewClickEvent() {
                                @Override
                                public void onOkClick(DialogInterface alertDialog) {
                                    finish();
                                }

                                @Override
                                public void onCancelClick() {
                                    HttpRequest.getInstance().Logout(new BaseHTTPCallBack() {
                                        @Override
                                        public void onHTTPSuccess() {
                                            UserData.getInstance().logout(getApplicationContext());
                                            Intent intent = new Intent(ListEmailActivity.this, LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        }

                                        @Override
                                        public void onHTTPFail(ErrorData errorDto) {
                                            Util.showMessageShort(errorDto.getMessage());
                                            UserData.getInstance().logout(getApplicationContext());
                                            Intent intent = new Intent(ListEmailActivity.this, LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        }
                                    });

                                }
                            });
                }
            });

        if (BuildConfig.FLAVOR.equals("serverVersion")) {
            checkVersion();
        }
    }

    private void checkOrganization() {
        Gson gson = new Gson();

        showProgressDialog();
        ArrayList<PersonData> list = new Prefs().getListOrganization();

        if (list != null && list.size() > 0) {
            //Check Modify
            JsonObject params = new JsonObject();
            params.add("sessionId", gson.toJsonTree(DaZoneApplication.getInstance().getPrefs().getaccesstoken()));
            params.add("languageCode", gson.toJsonTree(Locale.getDefault().getLanguage().toUpperCase()));
            params.add("timeZoneOffset", gson.toJsonTree(TimeUtils.getTimezoneOffsetInMinutes() + ""));
            params.add("moddate", gson.toJsonTree(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(new Date(System.currentTimeMillis()))));
            viewModel.getDepartmentsMod(params);
        } else {
            JsonObject params = new JsonObject();
            params.add("sessionId", gson.toJsonTree(DaZoneApplication.getInstance().getPrefs().getaccesstoken()));
            params.add("languageCode", gson.toJsonTree(Locale.getDefault().getLanguage().toUpperCase()));
            params.add("timeZoneOffset", gson.toJsonTree(TimeUtils.getTimezoneOffsetInMinutes() + ""));
            viewModel.getDepartments(params);
        }

        viewModel.getGetOrganizationLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean != null && aBoolean) {
                    dismissProgressDialog();
                }
            }
        });
    }

    private void checkVersion() {
        HttpRequest.getInstance().checkVersionUpdate(new BaseHTTPCallBackWithString() {
            @Override
            public void onHTTPSuccess(String response) {
                Gson gson = new Gson();
                CheckUpdateDto checkUpdateDto = gson.fromJson(response, CheckUpdateDto.class);
                urlDownload = checkUpdateDto.getPackageUrl();
                Thread thread = new Thread(new UpdateRunnable(checkUpdateDto.getVersion()));
                thread.setDaemon(true);
                thread.start();
            }

            @Override
            public void onHTTPFail(ErrorData errorData) {

            }

        });

    }

    private class UpdateRunnable implements Runnable {
        String version;

        UpdateRunnable(String version) {
            this.version = version;
        }

        @Override
        public void run() {
            try {
                String appVersion = BuildConfig.VERSION_NAME;
                if (compareVersionNames(appVersion, version) == -1) {
                    mActivityHandler2.sendEmptyMessage(Statics.ACTIVITY_HANDLER_START_UPDATE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int REQUEST_CODE_EXTERNAL = 111;
    private int REQUEST_INSTALL_UNKNOW_APPS = 112;

    private class ActivityHandler2 extends Handler {
        private final WeakReference<ListEmailActivity> mWeakActivity;

        public ActivityHandler2(ListEmailActivity activity) {
            mWeakActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final ListEmailActivity activity = mWeakActivity.get();

            if (activity != null) {
                if (msg.what == Statics.ACTIVITY_HANDLER_NEXT_ACTIVITY) {
                    //TODO Nothing
                } else if (msg.what == Statics.ACTIVITY_HANDLER_START_UPDATE) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage(R.string.string_update_content);
                    builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                        dialog.dismiss();
                        if (PermissionUtils.checkPermisstion(ListEmailActivity.this, PermissionUtils.READ_WRITE_EXTERNAL)) {
                            new Async_DownloadApkFile(ListEmailActivity.this, "CrewMail").execute();
                        } else
                            PermissionUtils.setPermisstions(ListEmailActivity.this, REQUEST_CODE_EXTERNAL, PermissionUtils.READ_WRITE_EXTERNAL);

                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class Async_DownloadApkFile extends AsyncTask<Void, Void, Void> {
        private String mApkFileName;
        private final WeakReference<ListEmailActivity> mWeakActivity;
        private ProgressDialog mProgressDialog = null;

        public Async_DownloadApkFile(ListEmailActivity activity, String apkFileName) {
            mWeakActivity = new WeakReference<>(activity);
            mApkFileName = apkFileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ListEmailActivity activity = mWeakActivity.get();
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
                URL apkUrl = new URL(urlDownload);
                urlConnection = (HttpURLConnection) apkUrl.openConnection();
                inputStream = urlConnection.getInputStream();
                bufferedInputStream = new BufferedInputStream(inputStream);


                String filename = getString(R.string.app_name) + "_" + BuildConfig.VERSION_NAME + ".apk";
                apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
                fileOutputStream = new FileOutputStream(apkFile);

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

            ListEmailActivity activity = mWeakActivity.get();

            if (activity != null) {
                isInstallingApk = true;
                String filename = getString(R.string.app_name) + "_" + BuildConfig.VERSION_NAME + ".apk";
                apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!getPackageManager().canRequestPackageInstalls()) {
                        startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", getPackageName()))), REQUEST_INSTALL_UNKNOW_APPS);
                    } else {
                        callInstallProcess(apkFile);
                    }
                } else {
                    callInstallProcess(apkFile);
                }
            }

            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }
    }

    private void callInstallProcess(File apkFile) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!checkPermissions()) {
                setPermissions();
            } else {
                isInstallingApk = false;
                Uri apkUri = FileProvider.getUriForFile(ListEmailActivity.this, BuildConfig.APPLICATION_ID + ".provider", apkFile);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(apkUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                startActivity(intent);
            }
        } else {
            isInstallingApk = false;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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

    public void reloadMenu(int isTask) {
        mailMenuView.reloadMenu(isTask);
    }


    @Override
    protected void addFragment(Bundle bundle, int mainContainer) {
        if (bundle == null) {
            mMainContainer = mainContainer;
            fm = ListEmailFragment.newInstance(0, "", null, 0);
            Util.replaceFragment(getSupportFragmentManager(), fm, mMainContainer, false);
        } else {
            int mailBoxNo = Integer.parseInt(bundle.getString(StaticsBundle.BUNDLE_MAIL_FROM_NOTIFICATION_MAILBOX_NO, "0"));
            fm = ListEmailFragment.newInstance(mailBoxNo, "", null, 0);
            try {
                Util.replaceFragment(getSupportFragmentManager(), fm, mMainContainer, false);
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }
    }

    @Override
    public void onMenuItemClick(Object object) {
        if (mMainContainer != 0) {
            if (object instanceof MailBoxMenuData) {
                MailBoxMenuData menuData = (MailBoxMenuData) object;

                if (!isSendMail) {
                    Log.d("sssDebugmenuData", isSendMail + "");
                    title = menuData.getName();
                    setToolBarTitle(menuData.getName());
                    fm = ListEmailFragment.newInstance(menuData.getBoxNo(), menuData.getName(), menuData.getClassName(), EmailBoxStatics.NORMAL_MAIL_BOX);
                    Util.replaceFragment(getSupportFragmentManager(), fm, mMainContainer, false);
                } else {
                    reloadMenu(3);
                    Log.d("sssDebugmenuData", isSendMail + "");
                    isSendMail = false;
                    setToolBarTitle(getString(R.string.string_title_menu_outbox));
                    fm = ListEmailFragment.newInstance(TYPE_OUTBOX, "Out Box", "OutBoxIcon", EmailBoxStatics.NORMAL_MAIL_BOX);
                    Util.replaceFragment(getSupportFragmentManager(), fm, mMainContainer, false);
                    new Prefs().putLongValue(Statics.SAVE_BOX_NO_PREF_V2, new Prefs().getLongValue(PREFS_KEY_COMPOSE, 0));
                }
            } else if (object instanceof MailTagMenuData) {
                MailTagMenuData menuData = (MailTagMenuData) object;
                setToolBarTitle(menuData.getName());
                title = menuData.getName();
                fm = ListEmailFragment.newInstance(menuData.getTagNo(), menuData.getName(), null, EmailBoxStatics.TAG_MAIL_BOX);
                Util.replaceFragment(getSupportFragmentManager(), fm, mMainContainer, false);
            }
        }
    }

    @Subscribe
    public void getReloadtitle(reloadTitle reloadTitle) {
        setToolBarTitle(title);
    }

    @Override
    public void onBackPressed() {
        if (fm != null) {
            if (!fm.releaseAllSelectedItem()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        DaZoneApplication.getInstance().cancelPendingRequests(Urls.URL_GET_EMAIL_LIST);
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Prefs prefs = new Prefs();
        if (prefs.getBooleanValue(StaticsBundle.PREFS_KEY_RELOAD_LIST, false)) {
            prefs.putBooleanValue(StaticsBundle.PREFS_KEY_RELOAD_LIST, false);
            if (fm != null)
                fm.refreshData(true);
        }

        if(isInstallingApk) {
            callInstallProcess(apkFile);
        }
    }

    public void setFab(boolean task) {
        displayFloatingButton(task);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_EXTERNAL && PermissionUtils.checkPermisstion(ListEmailActivity.this, permissions) && isInstallingApk) {
            callInstallProcess(apkFile);
        } else if(requestCode == REQUEST_INSTALL_UNKNOW_APPS && isInstallingApk && getPackageManager().canRequestPackageInstalls()) {
            callInstallProcess(apkFile);
        }
    }
}
