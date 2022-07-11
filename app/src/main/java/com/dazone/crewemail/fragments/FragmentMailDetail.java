package com.dazone.crewemail.fragments;

import static com.dazone.crewemail.utils.DialogUtils.dismissProgressDialog;
import static com.dazone.crewemail.utils.DialogUtils.showProgressDialog;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.dazone.crewemail.BuildConfig;
import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.activities.ActivityMailCreate;
import com.dazone.crewemail.activities.BaseActivity;
import com.dazone.crewemail.activities.LoginActivity;
import com.dazone.crewemail.activities.MoveMailBoxActivity;
import com.dazone.crewemail.adapter.AdapterMailCreateAttachLinear;
import com.dazone.crewemail.customviews.AlertDialogView;
import com.dazone.crewemail.customviews.MessageWebView;
import com.dazone.crewemail.data.AttachData;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.MailBoxData;
import com.dazone.crewemail.data.MailTagMenuData;
import com.dazone.crewemail.data.PersonData;
import com.dazone.crewemail.database.DataManager;
import com.dazone.crewemail.interfaces.BaseHTTPCallBack;
import com.dazone.crewemail.interfaces.IDownloadFileAgain;
import com.dazone.crewemail.interfaces.OnMailDetailCallBack;
import com.dazone.crewemail.interfaces.OnMenuListCallBack;
import com.dazone.crewemail.utils.EmailBoxStatics;
import com.dazone.crewemail.utils.MailHelper;
import com.dazone.crewemail.utils.PreferenceUtilities;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.StaticsBundle;
import com.dazone.crewemail.utils.TimeUtils;
import com.dazone.crewemail.utils.Util;
import com.dazone.crewemail.webservices.HttpRequest;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class FragmentMailDetail extends BaseFragment implements OnMailDetailCallBack {
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 1;
    String fileName, url;
    private View view, viewAttach;
    private TextView txtMailDetailSubject, txtMailDetailFrom, txtMailDetailTo, txtMailDetailCc, txtMailDetailBcc, txtMailDetailDate;
    private MessageWebView webMailDetailContent;
    private ImageButton imgMailDetailImportant, imgMailDetailCollage, imgMailDetailTag;
    private LinearLayout linearMailDetailCc, linearMailDetailBcc, linearMailDetailAttach, linearMailDetailAll, linearMailDetailTo;
    private MailBoxData mailBoxData = new MailBoxData();
    private String className;
    private boolean isRead;

    public static FragmentMailDetail newInstance(long mailNo, String isOutBox, boolean isRead) {
        Bundle bundle = new Bundle();
        bundle.putLong(StaticsBundle.BUNDLE_MAIL_NO, mailNo);
        bundle.putString(StaticsBundle.BUNDLE_MAIL_BOX_CLASS_NAME, isOutBox);
        bundle.putBoolean(StaticsBundle.PREFS_KEY_ISREAD, isRead);

        FragmentMailDetail fragmentMailDetail = new FragmentMailDetail();
        fragmentMailDetail.setArguments(bundle);
        return fragmentMailDetail;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Fresco.initialize(getActivity());
        if (getArguments() != null) {
            if (!TextUtils.isEmpty(DaZoneApplication.getInstance().getPrefs().getAccessToken().trim())) {
                mailBoxData.setMailNo(getArguments().getLong(StaticsBundle.BUNDLE_MAIL_NO));
                className = getArguments().getString(StaticsBundle.BUNDLE_MAIL_BOX_CLASS_NAME);
                isRead = getArguments().getBoolean(StaticsBundle.PREFS_KEY_ISREAD);
            } else {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                ActivityMailCreate.Instance.finish();
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mail_detail, container, false);
        initControl(view);
        initClick();
        return view;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void initControl(View view) {
        webMailDetailContent = view.findViewById(R.id.webMailDetailContent);
        webMailDetailContent.configure();

        imgMailDetailImportant = view.findViewById(R.id.imgMailDetailImportant);
        imgMailDetailCollage = view.findViewById(R.id.imgMailDetailCollage);
        imgMailDetailTag = view.findViewById(R.id.imgMailDetailTag);
        linearMailDetailAttach = view.findViewById(R.id.linear_attach);
        linearMailDetailCc = view.findViewById(R.id.linearMailDetailCc);
        linearMailDetailBcc = view.findViewById(R.id.linearMailDetailBcc);
        linearMailDetailTo = view.findViewById(R.id.linearMailDetailTo);
        linearMailDetailAll = view.findViewById(R.id.linearMailDetailInfo);

        txtMailDetailSubject = view.findViewById(R.id.txtMailDetailSubject);
        txtMailDetailFrom = view.findViewById(R.id.txtMailDetailFrom);
        txtMailDetailTo = view.findViewById(R.id.txtMailDetailTo);
        txtMailDetailCc = view.findViewById(R.id.txtMailDetailCc);
        txtMailDetailBcc = view.findViewById(R.id.txtMailDetailBcc);
        txtMailDetailDate = view.findViewById(R.id.txtMailDetailDate);

        webMailDetailContent.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webMailDetailContent.setScrollbarFadingEnabled(false);

        viewAttach = view.findViewById(R.id.viewDetailAttach);
    }

    private void initClick() {
        imgMailDetailCollage.setOnClickListener(v -> {
            if (linearMailDetailAll.getVisibility() == View.GONE) {
                linearMailDetailAll.setVisibility(View.VISIBLE);
                imgMailDetailCollage.setImageResource(R.drawable.dropdow_arr_ic_02);
                MailHelper.setEmailAddress(mailBoxData.getListPersonDataTo(), txtMailDetailTo, 0, linearMailDetailTo);
            } else {
                imgMailDetailCollage.setImageResource(R.drawable.dropdow_arr_ic);
                linearMailDetailAll.setVisibility(View.GONE);
                MailHelper.setEmailAddress(mailBoxData.getListPersonDataTo(), txtMailDetailTo, 1, linearMailDetailTo);
            }
        });

        imgMailDetailImportant.setOnClickListener(v -> {
            if (mailBoxData.isImportant()) {
                mailBoxData.setImportant(false);
            } else {
                mailBoxData.setImportant(true);
            }
            setImportant(mailBoxData, 1);
        });

        imgMailDetailTag.setOnClickListener(v -> GetTagMenu(false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        callAPI(mailBoxData.getMailNo());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mail_detail_menu, menu);
        if (EmailBoxStatics.MAIL_CLASS_OUT_BOX.equals(className)) {
            // out box
            menu.findItem(R.id.menuMailDetailReplyAll).setVisible(false);
            menu.findItem(R.id.menuMailDetailForward).setVisible(true);
            menu.findItem(R.id.menuMailDetailReply).setTitle(R.string.string_title_mail_detail_resend);

        } else {
            // other mail box
            menu.findItem(R.id.menuMailDetailReplyAll).setVisible(true);
            menu.findItem(R.id.menuMailDetailForward).setVisible(true);
            menu.findItem(R.id.menuMailDetailReply).setTitle(R.string.string_title_mail_detail_reply);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                break;
            case R.id.menuMailDetailReply:
                CallReply(mailBoxData, 1);
                break;
            case R.id.menuMailDetailReplyAll:
                CallReply(mailBoxData, 2);
                break;
            case R.id.menuMailDetailForward:
                CallReply(mailBoxData, 0);
                break;
            case R.id.menuMailDetailDelete:
                AlertDialogView.normalAlertDialogWithCancel(getActivity(), getString(R.string.app_name), getString(R.string.string_alert_delete_mail), getString(R.string.yes), getString(R.string.no), new AlertDialogView.OnAlertDialogViewClickEvent() {
                    @Override
                    public void onOkClick(DialogInterface alertDialog) {
                        HttpRequest.getInstance().moveEmailToTrash(mailBoxData.getMailNo(), false, new BaseHTTPCallBack() {
                            @Override
                            public void onHTTPSuccess() {
                                new Prefs().putBooleanValue(StaticsBundle.PREFS_KEY_RELOAD_LIST, true);
                                if (isVisible()) {
                                    getActivity().finish();
                                }
                            }

                            @Override
                            public void onHTTPFail(ErrorData errorDto) {
                                Util.showMessage(getString(R.string.string_update_fail));
                            }
                        });
                    }

                    @Override
                    public void onCancelClick() {

                    }
                });
                break;
            case R.id.menuMailDetailIsRead:
                HttpRequest.getInstance().updateEmailReadUnRead(false, mailBoxData.getMailNo(), new BaseHTTPCallBack() {
                    @Override
                    public void onHTTPSuccess() {
                        new Prefs().putBooleanValue(StaticsBundle.PREFS_KEY_RELOAD_LIST, true);
                        if (isVisible()) {
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onHTTPFail(ErrorData errorDto) {
                        Util.showMessage(getString(R.string.string_update_fail));
                    }
                });
                break;
            case R.id.menuMailDetailMore:
                break;
            case R.id.menuMailDetailMoveTo:
                Intent intent = new Intent(getActivity(), MoveMailBoxActivity.class);
                intent.putExtra(StaticsBundle.BUNDLE_TASK, 1);
                startActivityForResult(intent, Statics.EMAIL_MOVE_ACTIVITY);
                break;
            case R.id.menu_filter_address:
                Intent intent1 = new Intent(getActivity(), MoveMailBoxActivity.class);
                intent1.putExtra(StaticsBundle.BUNDLE_TASK, 2);
                startActivityForResult(intent1, Statics.EMAIL_FILTER_ADDRESS_ACTIVITY);
                break;
            case R.id.menu_filter_sender:
                Intent intent2 = new Intent(getActivity(), MoveMailBoxActivity.class);
                intent2.putExtra(StaticsBundle.BUNDLE_TASK, 2);
                startActivityForResult(intent2, Statics.EMAIL_FILTER_SENDER_ACTIVITY);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Set up data
    public void callAPI(long mailNo) {
        if (Util.isNetworkAvailable() && !DaZoneApplication.getInstance().offlineMode) {
            showProgressDialog(getContext());
            HttpRequest.getInstance().getMaillDetail(this, mailNo);
        } else {
            MailBoxData data = DataManager.Instance().getMailDetail(mailNo);
            dismissProgressDialog();
            if (data != null) {
                mailBoxData = data;
                bindData(mailBoxData);
            } else {
                Util.showMessage("Dữ liệu rỗng");
            }
            if (!isRead)
                new Prefs().putBooleanValue(StaticsBundle.PREFS_KEY_RELOAD_LIST, true);
        }
    }


    private void setUpMailAddress() {
        if (mailBoxData.getListPersonDataTo() != null && mailBoxData.getListPersonDataTo().size() > 0) {
            for (PersonData personData : mailBoxData.getListPersonDataTo())
                personData.setTypeAddress(0);
        }

        if (mailBoxData.getListPersonDataCc() != null && mailBoxData.getListPersonDataCc().size() > 0) {
            for (PersonData personData : mailBoxData.getListPersonDataCc())
                personData.setTypeAddress(1);
        }

        if (mailBoxData.getListPersonDataBcc() != null && mailBoxData.getListPersonDataBcc().size() > 0) {
            for (PersonData personData : mailBoxData.getListPersonDataBcc())
                personData.setTypeAddress(2);
        }
    }

    public void bindData(final MailBoxData mailBoxData) {
        if (mailBoxData != null) {
            txtMailDetailSubject.setText(mailBoxData.getSubject());

            setImportant(mailBoxData, 0);

            imgMailDetailTag.setImageResource(MailHelper.getColorTag(mailBoxData.getmImageNo()));

            MailHelper.setEmailTextViewDetail(mailBoxData.getMailFrom(), txtMailDetailFrom);
            MailHelper.setEmailAddress(mailBoxData.getListPersonDataTo(), txtMailDetailTo, 1, linearMailDetailTo);
            MailHelper.setEmailAddress(mailBoxData.getListPersonDataBcc(), txtMailDetailBcc, 0, linearMailDetailBcc);
            MailHelper.setEmailAddress(mailBoxData.getListPersonDataCc(), txtMailDetailCc, 0, linearMailDetailCc);

            if (mailBoxData.getListPersonDataTo().size() == 1 && mailBoxData.getListPersonDataBcc().size() == 0 && mailBoxData.getListPersonDataCc().size() == 0) {
                imgMailDetailCollage.setVisibility(View.INVISIBLE);
            } else {
                imgMailDetailCollage.setVisibility(View.VISIBLE);
            }

            setUpMailAddress();

            if (Locale.getDefault().getLanguage().toUpperCase().equalsIgnoreCase("KO")) {
                if (new PreferenceUtilities().getDomain().equals("dazone")) {
                    txtMailDetailDate.setText(TimeUtils.displayTimeWithoutOffset(getActivity(), mailBoxData.getDateCreate(), 1));
                } else {
                    txtMailDetailDate.setText(TimeUtils.displayTimeWithoutOffset(getActivity(), TimeUtils.getTimeWithTimezone(mailBoxData.getDateCreate()), 1));
                }

            } else {
                if (new PreferenceUtilities().getDomain().equals("dazone")) {
                    txtMailDetailDate.setText(TimeUtils.displayTimeWithoutOffset(getActivity(), mailBoxData.getDateCreate(), 0));
                } else {
                    txtMailDetailDate.setText(TimeUtils.displayTimeWithoutOffset(getActivity(), TimeUtils.getTimeWithTimezone(mailBoxData.getDateCreate()), 0));
                }
            }

            if (!TextUtils.isEmpty(mailBoxData.getContent())) {
                String mailContent = mailBoxData.getContent();
                webMailDetailContent.setText(mailContent);
            } else {
                webMailDetailContent.setVisibility(View.GONE);
            }

            if (mailBoxData.getListAttachMent().size() > 0) {
                viewAttach.setVisibility(View.VISIBLE);
                linearMailDetailAttach.setVisibility(View.VISIBLE);

                for (final AttachData attachData : mailBoxData.getListAttachMent()) {
                    final AdapterMailCreateAttachLinear itemView = new AdapterMailCreateAttachLinear(getActivity(), attachData);
                    itemView.setTag(attachData);

                    itemView.getImageButtonDownLoad().setOnClickListener(v -> {
                    });

                    itemView.setOnClickListener(v -> {
                        url = MailHelper.getUrl((AttachData) v.getTag());
                        fileName = ((AttachData) v.getTag()).getFileName();
                        File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/" + fileName);
                        if (!file1.exists()) {
                            //check permission
                            if (ContextCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {

                                // Should we show an explanation?
                                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                    // Show an explanation to the user *asynchronously* -- don't block

                                } else {

                                    // No explanation needed, we can request the permission.
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            EXTERNAL_STORAGE_PERMISSION_CONSTANT);

                                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                                    // app-defined int constant. The callback method gets the
                                    // result of the request.
                                }
                            } else {
                                MailHelper.displayDownloadFileDialog(getActivity(), url, fileName);
                            }

                        } else {
                            MailHelper.displayDownloadFileAgainDialog(getContext(), url, fileName , new IDownloadFileAgain() {
                                @Override
                                public void onOpenFile() {
                                    openFile(file1);
                                }
                            });

                        }
                    });

                    linearMailDetailAttach.addView(itemView);
                }
            } else {
                viewAttach.setVisibility(View.GONE);
                linearMailDetailAttach.setVisibility(View.GONE);
            }
        }
    }


    void openFile(File file) {
        try {
            String type = Statics.getMimeType(file.getAbsolutePath());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri apkUri = FileProvider.getUriForFile(BaseActivity.Instance, BuildConfig.APPLICATION_ID + ".provider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, type);
                startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), type);
                startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(BaseActivity.Instance, "No Application available to view this file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    //Task 0: binData
    //1: onClick imgMailDetailImportant
    public void setImportant(MailBoxData data, int Task) {
        if (data.isImportant()) {
            imgMailDetailImportant.setImageResource(R.drawable.list_star_yelow_ic);
        } else {
            imgMailDetailImportant.setImageResource(R.drawable.list_star_white_ic);
        }
        if (Task == 1) {
            HttpRequest.getInstance().updateEmailImportant(data.isImportant(), data.getMailNo(), new BaseHTTPCallBack() {
                @Override
                public void onHTTPSuccess() {
                    if (isVisible()) {
                        new Prefs().putBooleanValue(StaticsBundle.PREFS_KEY_RELOAD_LIST, true);
                        Util.showMessage(getString(R.string.string_success));
                    }
                }

                @Override
                public void onHTTPFail(ErrorData errorDto) {
                    Util.showMessage(getString(R.string.string_update_fail));
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                MailHelper.displayDownloadFileDialog(getActivity(), url, fileName);
            } else {
                // permission denied, boo! Disable the
                Toast.makeText(getActivity(), " permission denied, try again!", Toast.LENGTH_LONG).show();
            }
        }
    }

    //task: 0-forward 1-reply 2-replyAll
    private void CallReply(MailBoxData data, int task) {
        data.setContentReply(data.getContent());
        Intent intent = new Intent(getActivity(), ActivityMailCreate.class);
        intent.putExtra(StaticsBundle.BUNDLE_MAIL_DETAIL_TASK, task);
        intent.putExtra(StaticsBundle.BUNDLE_MAIL_CREATE_FROM_DETAIL, data);
        intent.putExtra(StaticsBundle.BUNDLE_MAIL_BOX_CLASS_NAME, className);
        getActivity().startActivity(intent);
    }

    //Show Tag mail as dialog
    public void GetTagMenu(final boolean isSkipCache) {
        final LinkedHashMap<String, Object> menuList = null;
        String cachedMenuJson = DaZoneApplication.getInstance().getPrefs().getMenuListData();
        String accessToken = DaZoneApplication.getInstance().getPrefs().getAccessToken();
        // get menu from cache if exist
        if (!isSkipCache && !TextUtils.isEmpty(cachedMenuJson) && cachedMenuJson.contains(accessToken + "#@#")) {
            String[] splitString = cachedMenuJson.split("#@#");
            ShowDialog(MailHelper.convertJsonStringToMap(splitString[1]));
        } else {
            // don't have any menu in cache , request server for the new one
            HttpRequest.getInstance().getEmailMenuList(new OnMenuListCallBack() {
                @Override
                public void onMenuListSuccess(LinkedHashMap menuMap) {
                    if (isVisible()) {
                        ShowDialog(menuMap);
                    }
                }

                @Override
                public void onMenuListFail(ErrorData errorData) {
                    if (isVisible()) {
                        ShowDialog(menuList);
                    }
                }
            });
        }
    }

    public void ShowDialog(LinkedHashMap menuMap) {
        final List<MailTagMenuData> itemList = new ArrayList<>();
        if (menuMap != null) {
            Iterator<?> keySets = menuMap.keySet().iterator();
            while (keySets.hasNext()) {
                String key = (String) keySets.next();
                List<?> menuList = (List<?>) menuMap.get(key);
                for (Object menuData : menuList) {
                    if (menuData instanceof MailTagMenuData) {
                        itemList.add((MailTagMenuData) menuData);
                    }
                }
            }
        }
        itemList.add(0, new MailTagMenuData(0, 0, getString(R.string.string_title_mail_no_tag), false));
        MailHelper.displaySingleChoiceList(getActivity(), itemList, (dialog, which) -> HttpRequest.getInstance().updateTagOfMail(itemList.get(which).getTagNo(), mailBoxData.getMailNo(), new BaseHTTPCallBack() {
            @Override
            public void onHTTPSuccess() {
                if (isVisible()) {
                    imgMailDetailTag.setImageResource(MailHelper.getColorTag(itemList.get(which).getImageNo()));
                    new Prefs().putBooleanValue(StaticsBundle.PREFS_KEY_RELOAD_LIST, true);
                }
                Util.showMessage(getString(R.string.string_success));
            }

            @Override
            public void onHTTPFail(ErrorData errorDto) {
                Util.showMessage(getString(R.string.string_update_fail));
            }
        }), getString(R.string.app_name));
    }

    //Move To Mail
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int boxNoAfter, boxNoBefore;
        String address, sender;
        if (resultCode == Activity.RESULT_OK) {
            boxNoBefore = mailBoxData.getBoxNo();
            address = mailBoxData.getMailFrom().getEmail();
            sender = mailBoxData.getMailFrom().getFullName();
            switch (requestCode) {
                case Statics.EMAIL_MOVE_ACTIVITY:
                    Bundle b = data.getExtras();
                    if (b != null) {
                        moveEmailFromSelectedList(b.getInt(StaticsBundle.BUNDLE_MAIL_BOX_NO));
                    }
                    break;
                case Statics.EMAIL_FILTER_ADDRESS_ACTIVITY:
                    Bundle bc = data.getExtras();
                    if (bc != null) {
                        boxNoAfter = bc.getInt(StaticsBundle.BUNDLE_MAIL_BOX_NO);
                        if (!TextUtils.isEmpty(address)) {
                            HttpRequest.getInstance().filterAddressMail(boxNoBefore, boxNoAfter, address, new BaseHTTPCallBack() {
                                @Override
                                public void onHTTPSuccess() {
                                    Util.showMessage(getString(R.string.string_title_filter_success));
                                    if (isVisible()) {
                                        new Prefs().putBooleanValue(StaticsBundle.PREFS_KEY_RELOAD_LIST, true);
                                    }
                                }

                                @Override
                                public void onHTTPFail(ErrorData errorDto) {
                                    Util.showMessage(getString(R.string.string_title_filter_fail));
                                }
                            });
                        }
                    }
                    break;
                case Statics.EMAIL_FILTER_SENDER_ACTIVITY:
                    Bundle bd = data.getExtras();
                    if (bd != null) {
                        boxNoAfter = bd.getInt(StaticsBundle.BUNDLE_MAIL_BOX_NO);
                        if (!TextUtils.isEmpty(sender)) {
                            HttpRequest.getInstance().filterSenderMail(boxNoBefore, boxNoAfter, sender, new BaseHTTPCallBack() {
                                @Override
                                public void onHTTPSuccess() {
                                    Util.showMessage(getString(R.string.string_title_filter_success));
                                    if (isVisible()) {
                                        new Prefs().putBooleanValue(StaticsBundle.PREFS_KEY_RELOAD_LIST, true);
                                    }
                                }

                                @Override
                                public void onHTTPFail(ErrorData errorDto) {
                                    Util.showMessage(getString(R.string.string_title_filter_fail));
                                }
                            });
                        }
                    }
                    break;
            }
        }
    }

    public void moveEmailFromSelectedList(int mailBoxNo) {
        HttpRequest.getInstance().moveEmailToBox(mailBoxData.getMailNo(), mailBoxNo, new BaseHTTPCallBack() {
            @Override
            public void onHTTPSuccess() {
                Util.showMessage(getString(R.string.string_move_to_success));
                if (isVisible()) {
                    new Prefs().putBooleanValue(StaticsBundle.PREFS_KEY_RELOAD_LIST, true);
                }
            }

            @Override
            public void onHTTPFail(ErrorData errorDto) {
                Util.showMessage(getString(R.string.string_move_to_fail));
            }
        });
    }

    //Web Service
    @Override
    public void OnMailDetailSuccess(MailBoxData data) {
        dismissProgressDialog();
        if (isVisible()) {
            if (data != null) {
                mailBoxData = data;
                bindData(mailBoxData);
            } else {
                Util.showMessage("Dữ liệu rỗng online");
            }
            if (!isRead)
                new Prefs().putBooleanValue(StaticsBundle.PREFS_KEY_RELOAD_LIST, true);
        }

    }

    @Override
    public void OnMaillDetailFail(ErrorData errorData) {
        Util.showMessage("Dữ liệu rỗng online");
        try {
            dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isVisible()) {
            if (TextUtils.isEmpty(errorData.getMessage())) {
            } else {
                Util.showMessageShort(errorData.getMessage());
                Util.printLogs(errorData.getMessage());
            }
        }
    }
}