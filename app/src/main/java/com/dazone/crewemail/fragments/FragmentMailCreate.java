package com.dazone.crewemail.fragments;

import static com.dazone.crewemail.activities.ActivityMailCreate.isBack;
import static com.dazone.crewemail.activities.ListEmailActivity.isSendMail;
import static com.dazone.crewemail.utils.Statics.FILE_PICKER_SELECT;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.Service.UploadFileToServer;
import com.dazone.crewemail.Service.fileUploadDownload;
import com.dazone.crewemail.activities.ActivityMailCreate;
import com.dazone.crewemail.activities.ContactActivity;
import com.dazone.crewemail.activities.ListEmailActivity;
import com.dazone.crewemail.activities.LoginActivity;
import com.dazone.crewemail.activities.OrganizationChartActivity;
import com.dazone.crewemail.adapter.AdapterMailCreateAttachLinear;
import com.dazone.crewemail.adapter.AdapterMailCreateFromSpiner;
import com.dazone.crewemail.customviews.AlertDialogView;
import com.dazone.crewemail.customviews.PersonCompleteView;
import com.dazone.crewemail.customviews.Utility;
import com.dazone.crewemail.data.AccountData;
import com.dazone.crewemail.data.AttachData;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.EventRequest;
import com.dazone.crewemail.data.MailBoxData;
import com.dazone.crewemail.data.PersonData;
import com.dazone.crewemail.data.UserData;
import com.dazone.crewemail.database.DataManager;
import com.dazone.crewemail.database.DraftMailDataSource;
import com.dazone.crewemail.dialog.DialogUtil;
import com.dazone.crewemail.event.NewMailEvent;
import com.dazone.crewemail.interfaces.BaseHTTPCallBack;
import com.dazone.crewemail.interfaces.OnGetListOfMailAccount;
import com.dazone.crewemail.interfaces.OnMailDetailCallBack;
import com.dazone.crewemail.interfaces.pushlishProgressInterface;
import com.dazone.crewemail.utils.Constants;
import com.dazone.crewemail.utils.EmailBoxStatics;
import com.dazone.crewemail.utils.MailHelper;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.StaticsBundle;
import com.dazone.crewemail.utils.Util;
import com.dazone.crewemail.view.tokenautocomplete.FilteredArrayAdapter;
import com.dazone.crewemail.view.tokenautocomplete.TokenCompleteTextView;
import com.dazone.crewemail.webservices.HttpRequest;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nononsenseapps.filepicker.FilePickerActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


public class FragmentMailCreate extends BaseFragment implements pushlishProgressInterface, TokenCompleteTextView.TokenListener, BaseHTTPCallBack, OnGetListOfMailAccount, View.OnClickListener, OnMailDetailCallBack {
    private String TAG = FragmentMailCreate.class.getName();
    private ImageButton imgDropDown, imgAddAttach;
    private LinearLayout linearMainBccAndCc, linearAttach;
    private EditText edtMailCreateSubject, edtMailCreateContent;
    private TextView txtMailCreateOriginalMessage;
    private PersonCompleteView edtMailCreateTo, edtMailCreateBcc, edtMailCreateCc;
    private Spinner spnMailFrom;
    private CheckBox chkMailCreateQuote;
    private ArrayList<PersonData> people = new ArrayList<>();
    private ArrayAdapter<PersonData> adapter;
    private MailBoxData mailBoxData = new MailBoxData();

    private MailBoxData mailBoxDataNew = new MailBoxData();
    private MailBoxData dataCreate;
    private View v;
    private AdapterMailCreateFromSpiner adapterMailCreateFromSpiner;
    private ArrayList<AccountData> listUser = new ArrayList<>();
    private int task = 4;
    private boolean isSend = true;
    private int isUpload = 0;
    private int isFail = 0;
    private int isTask = 0;
    private String className;
    private long MailNo;
    private LinearLayout lnCC, lnBCC;
    private ArrayList<AttachData> tp = new ArrayList<>();
    private ProgressBar mProgressBar;
    private TextView txtPercentage;
    private ProgressBar progress_bar;
    int countFile;
    private boolean isChange = false;

    public static FragmentMailCreate newInstance(int task, MailBoxData data, String className, long MailNo) {
        Bundle args = new Bundle();
        args.putInt(StaticsBundle.BUNDLE_MAIL_DETAIL_TASK, task);
        args.putSerializable(StaticsBundle.BUNDLE_MAIL_CREATE_FROM_DETAIL, data);
        args.putString(StaticsBundle.BUNDLE_MAIL_BOX_CLASS_NAME, className);
        args.putLong(StaticsBundle.BUNDLE_MAIL_NO, MailNo);
        FragmentMailCreate fragment = new FragmentMailCreate();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Fresco.initialize(getActivity());

        EventBus.getDefault().register(this);
        try {
            AccountData.getAllAccount(this);
            HttpRequest.getInstance().removeTempFile(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            task = (bundle.getInt(StaticsBundle.BUNDLE_MAIL_DETAIL_TASK));
            dataCreate = (MailBoxData) bundle.getSerializable(StaticsBundle.BUNDLE_MAIL_CREATE_FROM_DETAIL);
            className = bundle.getString(StaticsBundle.BUNDLE_MAIL_BOX_CLASS_NAME);
            MailNo = bundle.getLong(StaticsBundle.BUNDLE_MAIL_NO);
            if (dataCreate != null) {
                setUpCreate(dataCreate, className);
            } else {
                setUpDraft(MailNo);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_mail_create, container, false);
        initControl(v);
        getOrganization();
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initClick();
        if (task != 3) {
            bindData(dataCreate);
        }
        if (dataCreate == null) {
            linearMainBccAndCc.setVisibility(View.GONE);
        } else {
            if (dataCreate.getListPersonDataTo().size() > 0 && dataCreate.getListPersonDataTo() != null) {
                if (dataCreate.getListPersonDataCc() != null && dataCreate.getListPersonDataCc().size() > 0) {
                    lnCC.setVisibility(View.VISIBLE);
                    if (dataCreate.getListPersonDataBcc() != null && dataCreate.getListPersonDataBcc().size() > 0) {
                        lnBCC.setVisibility(View.VISIBLE);
                    } else {
                        lnBCC.setVisibility(View.GONE);
                    }
                } else {
                    if (dataCreate.getListPersonDataBcc() != null && dataCreate.getListPersonDataBcc().size() > 0) {
                        lnBCC.setVisibility(View.VISIBLE);
                    } else {
                        lnBCC.setVisibility(View.GONE);
                    }
                    lnCC.setVisibility(View.GONE);
                }
            }
        }
        if (!TextUtils.isEmpty(DaZoneApplication.getInstance().getPrefs().getAccessToken().trim())) {
            // Get intent, action and MIME type
            Intent intent = getActivity().getIntent();
            String action = intent.getAction();
            String type = intent.getType();
            if (type != null) {
                final ArrayList<PersonData> arrayList = new ArrayList<>();

                Bundle bundle = intent.getExtras();
                progress_bar.setVisibility(View.VISIBLE);
                if (bundle != null) {
                    final String[] emails = bundle.getStringArray(Intent.EXTRA_EMAIL);
//                    HttpRequest.getInstance().getDepartmentV2(new OnGetAllOfUser() {
//                        @Override
//                        public void onGetAllOfUserSuccess(ArrayList<PersonData> list) {
//                            progress_bar.setVisibility(View.GONE);
//                            if (list.size() >= 0) {
//                                if (emails != null && emails.length >= 1) {
//                                    for (int i = 0; i <= emails.length - 1; i++) {
//                                        if (getPersonDataWithQueryUserNo(emails[i], arrayList, list).size() >= 1) {
//                                            for (int j = 0; j <= getPersonDataWithQueryUserNo(emails[i], arrayList, list).size() - 1; j++) {
//                                                edtMailCreateTo.addObject(getPersonDataWithQueryUserNo(emails[i], arrayList, list).get(j));
//                                            }
//                                        } else {
//                                            PersonData P = new PersonData();
//                                            P.setEmail(emails[i]);
//                                            P.setFullName(emails[i]);
//                                            edtMailCreateTo.addObject(P);
//                                        }
//
//                                    }
//                                    progress_bar.setVisibility(View.GONE);
//                                    mailBoxData.getListPersonDataTo().addAll(edtMailCreateTo.getObjects());
//                                }
//                            } else {
//                                for (int i = 0; i < emails.length - 1; i++) {
//                                    PersonData P = new PersonData();
//                                    P.setEmail(emails[i]);
//                                    P.setFullName("a");
//                                    edtMailCreateTo.addObject(P);
//                                }
//                                mailBoxData.getListPersonData().addAll(edtMailCreateTo.getObjects());
//                            }
//                        }
//
//                        @Override
//                        public void onGetAllOfUserFail(ErrorData errorData) {
//                            progress_bar.setVisibility(View.GONE);
//                        }
//
//                    });
                }
            }
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    handleSendText(intent); // Handle text being sent
                } else {
                    handleSendImage(intent); // Handle single image being sent
                }
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            ActivityMailCreate.Instance.finish();
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private void getOrganization() {
        ArrayList<PersonData> data = new Prefs().getListOrganization();
        if(data == null || data.size() <= 0) {
           requireActivity().finish();
        } else {
            people = new ArrayList<>();
            for(PersonData personData : data) {
                if(personData.getListMembers() != null && personData.getListMembers().size() > 0) {
                    for(PersonData member : personData.getListMembers()) {
                        if(!people.contains(member)) {
                            people.add(member);
                        }
                    }
                }

                if(personData.getPersonList() != null && personData.getPersonList().size() > 0) {
                    checkDepartment(people, personData.getPersonList());
                }
            }

            initAdapter();
        }
    }

    private void checkDepartment(ArrayList<PersonData> listMember, ArrayList<PersonData> listDepartment) {
        for(PersonData department : listDepartment) {
            if(department.getListMembers() != null && department.getListMembers().size() > 0) {
                for(PersonData member : department.getListMembers()) {
                    if(!listMember.contains(member)) {
                        listMember.add(member);
                    }
                }
            }

            if(department.getPersonList() != null && department.getPersonList().size() > 0) {
                checkDepartment(listMember, department.getPersonList());
            }
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
            try {
                edtMailCreateContent.setText(sharedText);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            GetFile(imageUri);
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
            for (Uri path : imageUris) {
                // Do something with the URI
                GetFile(path);
            }
        }
    }

    private void initControl(View v) {
        imgDropDown = v.findViewById(R.id.imgMailCreateImageDropDown);
        lnCC = v.findViewById(R.id.ln_mail_cc);
        lnBCC = v.findViewById(R.id.ln_mail_bcc);
        progress_bar = v.findViewById(R.id.progress_bar);
        imgAddAttach = v.findViewById(R.id.imgMailCreateAttach);
        linearMainBccAndCc = v.findViewById(R.id.linear_main_bcc_cc);
        linearAttach = v.findViewById(R.id.linear_attach);
        edtMailCreateTo =
                v.findViewById(R.id.edtMailCreateTo);
        edtMailCreateTo.setType(0);
        edtMailCreateCc =
                v.findViewById(R.id.edtMailCreateCc);
        edtMailCreateCc.setType(1);
        edtMailCreateBcc =
                v.findViewById(R.id.edtMailCreateBcc);
        edtMailCreateBcc.setType(2);


        edtMailCreateSubject = v.findViewById(R.id.edtMailCreateSubject);
        edtMailCreateContent = v.findViewById(R.id.edtMailCreateContent);
        spnMailFrom = v.findViewById(R.id.spnMailCreateAccountFrom);
        chkMailCreateQuote = v.findViewById(R.id.chkMailCreateCheck);
        txtMailCreateOriginalMessage = v.findViewById(R.id.txtMailCreateOriginalMessage);
        txtPercentage = v.findViewById(R.id.txtPercentage);
        mProgressBar = v.findViewById(R.id.progressBar);
        v.findViewById(R.id.imgMailCreateOrgTo).setOnClickListener(this);
        v.findViewById(R.id.imgMailCreateContactTo).setOnClickListener(this);
        v.findViewById(R.id.imgMailCreateOrgCc).setOnClickListener(this);
        v.findViewById(R.id.imgMailCreateContactCc).setOnClickListener(this);
        v.findViewById(R.id.imgMailCreateOrgBcc).setOnClickListener(this);
        v.findViewById(R.id.imgMailCreateContactBcc).setOnClickListener(this);
    }

    private void initAdapter() {
        setAdapter(edtMailCreateTo);
        setAdapter(edtMailCreateBcc);
        setAdapter(edtMailCreateCc);
        adapterMailCreateFromSpiner = new AdapterMailCreateFromSpiner(getActivity(), R.layout.fragment_mail_create_item_spiner_address_to, listUser);
        spnMailFrom.setAdapter(adapterMailCreateFromSpiner);
    }

    private void setAdapter(TokenCompleteTextView tokenCompleteTextView) {
        adapter = new FilteredArrayAdapter<PersonData>(getActivity(), R.layout.person_layout, people) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {

                    LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(R.layout.person_layout, parent, false);
                }
                PersonData p = getItem(position);
                ((TextView) convertView.findViewById(R.id.name)).setText(p.getFullName());
                ((TextView) convertView.findViewById(R.id.email)).setText(p.getmEmail());
                return convertView;
            }

            @Override
            protected boolean keepObject(PersonData person, String mask) {
                mask = mask.toLowerCase();
                return person.getFullName().toLowerCase().contains(mask) || person.getFullName().toLowerCase().contains(mask);
            }
        };
        tokenCompleteTextView.setAdapter(adapter);
        tokenCompleteTextView.setTokenListener(this);
        tokenCompleteTextView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);
    }

    private void initClick() {
        imgDropDown.setOnClickListener(v -> {
            if (linearMainBccAndCc.getVisibility() == View.VISIBLE) {
                imgDropDown.setImageResource(R.drawable.dropdow_arr_ic);
                linearMainBccAndCc.setVisibility(View.GONE);
                lnCC.setVisibility(View.VISIBLE);
                lnBCC.setVisibility(View.VISIBLE);
            } else {
                imgDropDown.setImageResource(R.drawable.dropdow_arr_ic_02);
                linearMainBccAndCc.setVisibility(View.VISIBLE);
                lnCC.setVisibility(View.VISIBLE);
                lnBCC.setVisibility(View.VISIBLE);
            }
        });

        imgAddAttach.setOnClickListener(v -> {
            browseClick();
        });

        spnMailFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(listUser.size() > 0) {
                    PersonData personData = new PersonData();
                    personData.setFullName(listUser.get(position).getName());
                    personData.setEmail(listUser.get(position).getMailAddress());
                    mailBoxData.setMailFrom(personData);
                    mailBoxData.setUserNo(listUser.get(position).getAccountNo());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        chkMailCreateQuote.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                txtMailCreateOriginalMessage.setVisibility(View.VISIBLE);
            } else {
                txtMailCreateOriginalMessage.setVisibility(View.GONE);
            }
        });

    }

    public void browseClick() {
        if (checkPermissionsReadExternalStorage()) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(Intent.createChooser(intent, ""), FILE_PICKER_SELECT);
            } catch (Exception ex) {
                System.out.println("browseClick :" + ex);//android.content.ActivityNotFoundException ex
            }
        } else {
            setPermissionsReadExternalStorage();
        }
    }

    private void setUpCreate(MailBoxData data, String className) {
        ArrayList<PersonData> temp = new ArrayList<>();
        ArrayList<PersonData> temp2 = new ArrayList<>();
        ArrayList<PersonData> temp3 = new ArrayList<>();
        mailBoxData.setMailNo(data.getMailNo());
        mailBoxData.setContentReply(data.getContentReply());
        if (task == 0) {
            mailBoxData.setSubject("FWD: " + data.getSubject());
            if (data.getListAttachMent() != null) {
                mailBoxData.setListAttachMent(data.getListAttachMent());
                tp.addAll(data.getListAttachMent());
            }
        } else {
            if (EmailBoxStatics.MAIL_CLASS_OUT_BOX.equals(className)) {
                // out box
                mailBoxData.setListPersonDataTo(data.getListPersonDataTo());
                mailBoxData.setSubject(data.getSubject());
                mailBoxData.setListPersonDataCc(data.getListPersonDataCc());
                mailBoxData.setListPersonDataBcc(data.getListPersonDataBcc());
            } else {
                // other mail box
                temp.add(data.getMailFrom());
                mailBoxData.setSubject("RE: " + data.getSubject());
                mailBoxData.setListPersonDataTo(temp);
            }
            if (task == 2) {
                temp.addAll(MailHelper.removeMe(data.getListPersonDataTo()));
                mailBoxData.setListPersonDataTo(temp);
                temp2.addAll(MailHelper.removeMe(data.getListPersonDataCc()));
                mailBoxData.setListPersonDataCc(temp2);
                temp3.addAll(MailHelper.removeMe(data.getListPersonDataBcc()));
                mailBoxData.setListPersonDataBcc(temp3);
            }
        }
    }

    private void setUpDraft(long mailNo) {
        if (task == 3) {
            HttpRequest.getInstance().getMaillDetail(this, mailNo);
        }
    }

    AttachData attachData;

    public void GetFile(Uri uri) {
        String Path = Util.getPathFromUri(getActivity(), uri);
        if(mailBoxData.getListAttachMent() != null && mailBoxData.getListAttachMent().size() > 0) {
            for(AttachData attachData : mailBoxData.getListAttachMent()) {
                if(attachData.getPath().equals(Path)) {
                    return;
                }
            }
        }

        String fileName = Util.getFileName(uri, getActivity());
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        long fileSize = Util.getFileSize(Path);
        if (linearAttach != null) {
            linearAttach.setVisibility(View.VISIBLE);
        }
        attachData = new AttachData(fileName, Path, fileSize, fileType, System.currentTimeMillis());
        mailBoxData.getListAttachMent().add(attachData);
        final AdapterMailCreateAttachLinear itemView = new AdapterMailCreateAttachLinear(getActivity(), attachData);
        itemView.setTag(attachData);
        itemView.getImageButtonDelete().setVisibility(View.VISIBLE);
        itemView.getImageButtonDelete().setOnClickListener(v -> {
            mailBoxData.getListAttachMent().remove(itemView.getTag());
            linearAttach.removeView(itemView);
            isUpload--;
            updateTextView();
            if (mailBoxData.getListAttachMent().size() == 0) {
                attachData = null;
                mProgressBar.setVisibility(View.GONE);
                if (progress_bar != null) {
                    progress_bar.setVisibility(View.GONE);
                }
                txtPercentage.setVisibility(View.GONE);
            }
            Util.printLogs("Còn mấy file: " + mailBoxData.getListAttachMent().size());
        });
        itemView.setOnLongClickListener(view -> false);
        itemView.setOnClickListener(v -> MailHelper.OpenFile(getActivity(), (AttachData) itemView.getTag()));
        linearAttach.addView(itemView);

        new Upload().execute(attachData.getPath(), attachData.getFileName());
    }

    public void bindReceiver(PersonCompleteView etReceiver, ArrayList<PersonData> resultList) {
        if(resultList != null && resultList.size() > 0) {
            for (PersonData personData : resultList) {
                personData.setTypeAddress(getTypeAddress(etReceiver));
                UserData userDto = UserData.getUserInformation();
                if (!personData.getEmail().equals(userDto.getmEmail())) {
                    etReceiver.addObject(personData);
                }
            }

            adapter.notifyDataSetChanged();
        }

    }

    private int getTypeAddress(PersonCompleteView etReceiver) {
        if(etReceiver.getId() == edtMailCreateTo.getId()) {
            return 0;
        } else if(etReceiver.getId() == edtMailCreateCc.getId()) {
            return 1;
        } else return 2;
    }

    public void handleSelectedOrganizationResult(int type, ArrayList<PersonData> resultList) {
        ArrayList<PersonData> uniqueList = new ArrayList<>();
        PersonCompleteView etReceiver;
        if(type == Statics.ORGANIZATION_TO_ACTIVITY) {
            etReceiver = edtMailCreateTo;
        } else if(type == Statics.ORGANIZATION_CC_ACTIVITY) {
            etReceiver = edtMailCreateCc;
        } else {
            etReceiver = edtMailCreateBcc;
        }

        etReceiver.clear();

        for(PersonData personData : resultList) {
            if(!uniqueList.contains(personData)) {
                uniqueList.add(personData);
            }
        }

        for (PersonData personData : uniqueList) {
            personData.setTypeAddress(getTypeAddress(etReceiver));
            UserData userDto = UserData.getUserInformation();
            if (!personData.getEmail().equals(userDto.getmEmail())) {
                etReceiver.addObject(personData);
            }
        }

        etReceiver.allowCollapse(true);
        etReceiver.performCollapse(true);
        adapter.notifyDataSetChanged();


    }

    public void bindData(MailBoxData data) {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (data != null && data.getMailNo() > 0) {
            edtMailCreateContent.requestFocus();
            txtMailCreateOriginalMessage.setVisibility(View.VISIBLE);
            txtMailCreateOriginalMessage.setText(MailHelper.getOriginalMessage(getActivity(), dataCreate));
            if (task == 0 || task == 3) {
                chkMailCreateQuote.setVisibility(View.GONE);
            } else {
                chkMailCreateQuote.setVisibility(View.VISIBLE);
            }

            edtMailCreateSubject.setText(Utility.preFixSubject(task, data.getSubject()));
            ArrayList<PersonData> list = new ArrayList<>();
            PersonData personReply = data.getMailFrom();
            personReply.setmEmail(personReply.getEmail());
            list.add(data.getMailFrom());
            if(task == 1) {
                bindReceiver(edtMailCreateTo, list);
            } else if(task == 2) {
                bindReceiver(edtMailCreateTo, list);
                bindReceiver(edtMailCreateCc, data.getListPersonDataCc());
                bindReceiver(edtMailCreateBcc, data.getListPersonDataBcc());
            }

            if (task == 3 || task == 0) {
                if (task == 3) {
                    showDialogDraftMail();
                    String content = Html.fromHtml(mailBoxData.getContent()).toString().replace("* {line-height:1.2;}", "");
                    edtMailCreateContent.setText(content);
                }

                if (mailBoxData.getListAttachMent().size() > 0) {
                    mailBoxDataNew.setListAttachMent(new ArrayList<>(mailBoxData.getListAttachMent()));
                    linearAttach.setVisibility(View.VISIBLE);
                    for (final AttachData attachData : mailBoxDataNew.getListAttachMent()) {
                        final AdapterMailCreateAttachLinear itemView = new AdapterMailCreateAttachLinear(getActivity(), attachData);
                        itemView.setTag(attachData);
                        itemView.getImageButtonDownLoad().setOnClickListener(v -> {
                        });
                        linearAttach.addView(itemView);
                    }
                } else {
                    linearAttach.setVisibility(View.GONE);
                }

                if (task == 0) {
                    edtMailCreateTo.setText("");
                    mailBoxData.setListAttachMent(new ArrayList<>());
                }
            }
        }

        edtMailCreateBcc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isChange = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtMailCreateCc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isChange = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtMailCreateContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isChange = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtMailCreateSubject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isChange = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtMailCreateTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isChange = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            isChange = true;
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }, 1500);
    }

    private void showDialogDraftMail() {
        DialogUtil.customAlertDialog(getActivity(),
                getResources().getString(R.string.open_draff_mail),
                getResources().getString(R.string.string_title_mail_create_ok),
                getResources().getString(R.string.string_title_mail_create_cancel), new DialogUtil.OnAlertDialogViewClickEvent() {
                    @Override
                    public void onOkClick(DialogInterface alertDialog) {
                        alertDialog.dismiss();
                    }

                    @Override
                    public void onCancelClick() {
                        if (getActivity() != null)
                            getActivity().finish();
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mail_create_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (checkDraft(1)) {
                    DrafMail();
                } else {
                    getActivity().finish();
                }
                break;
            case R.id.menuMailCreateSend:
                sendAction();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public AlertDialog myDialog;

    public void normalAlertDialogV2(final Context context, String title, String message, String okButton, final AlertDialogView.OnAlertDialogViewClickEvent clickEvent) {
        if (myDialog != null && myDialog.isShowing()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(okButton, (dialog, arg1) -> {
            if (clickEvent != null) {
                clickEvent.onOkClick(dialog);
            } else {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        myDialog = builder.create();
        myDialog.show();
    }

    private void sendAction() {
        if (isSend) {
            if (mailBoxData != null) {
                isBack = true;
                String subject = edtMailCreateSubject.getText().toString();
                String content = edtMailCreateContent.getText().toString();
                if (TextUtils.isEmpty(subject)) {
                    normalAlertDialogV2(getActivity(), getString(R.string.app_name), getString(R.string.string_lost_subjet), getString(R.string.string_title_mail_create_ok), null);
                    isSend = true;
                } else if (checkDraft(0)) {
                    if (task != 4) {
                        normalAlertDialogV2(getActivity(), getString(R.string.app_name), getString(R.string.string_title_mail_create_error), getString(R.string.string_title_mail_create_ok), null);
                        isSend = true;
                    } else {
                         if (edtMailCreateTo.getObjects().size() == 0) {
                            ArrayList<PersonData> list = new ArrayList<>();
                            list.addAll(edtMailCreateBcc.getObjects());
                            list.addAll(edtMailCreateCc.getObjects());
                            int value = MailHelper.checkEmailV2(list);
                            checkCaseErr(value, list, content, subject);
                        } else {
                            ArrayList<PersonData> list = new ArrayList<>();
                            list.addAll(edtMailCreateBcc.getObjects());
                            list.addAll(edtMailCreateCc.getObjects());
                            list.addAll(edtMailCreateTo.getObjects());
                            int value = MailHelper.checkEmailV2(list);
                            checkCaseErr(value, null, content, subject);
                        }
                    }

                } else {
                    if (edtMailCreateTo.getObjects().size() == 0) {
                        ArrayList<PersonData> list = new ArrayList<>();
                        list.addAll(edtMailCreateBcc.getObjects());
                        list.addAll(edtMailCreateCc.getObjects());
                        int value = MailHelper.checkEmailV2(list);
                        checkCaseErr(value, list, content, subject);
                    } else {
                        ArrayList<PersonData> list = new ArrayList<>();
                        list.addAll(edtMailCreateBcc.getObjects());
                        list.addAll(edtMailCreateCc.getObjects());
                        list.addAll(edtMailCreateTo.getObjects());
                        int value = MailHelper.checkEmailV2(list);
                        checkCaseErr(value, null, content, subject);
                    }
                }
            }
        }
    }

    private void checkCaseErr(int errValue, List<PersonData> listPer, String content, String subject) {
        if (errValue == 0) {
            normalAlertDialogV2(getActivity(), getString(R.string.app_name), getString(R.string.string_title_mail_create_error), getString(R.string.string_title_mail_create_ok), null);
        } else if (errValue == 1) {
            Util.showMessage(getString(R.string.string_email_error_new));
        } else if (errValue == 2) {
            if (listPer == null) {
                SendMail(content, subject, 0);
            } else {
                SendMail2(listPer, content, subject, 0);
            }
        }
    }

    private void SendMail2(List<PersonData> listPer, String content, String subject, int isTask) {
        this.isTask = isTask;
        isSend = false;
        String dataContent;
        dataContent = content;
        mailBoxData.setListPersonDataBcc(new ArrayList<>());
        mailBoxData.setListPersonDataCc(new ArrayList<>());
        mailBoxData.setListPersonDataTo(new ArrayList<>());
        mailBoxData.getListPersonDataBcc().addAll(edtMailCreateBcc.getObjects());
        mailBoxData.getListPersonDataTo().addAll(listPer);
        mailBoxData.getListPersonDataCc().addAll(edtMailCreateCc.getObjects());
        mailBoxData.setContent(dataContent);
        mailBoxData.setSubject(subject);
        mailBoxData.setPriority("3");
        if (task == 0)
            if (dataContent != null) {
                if (tp != null && tp.size() > 0)
                    for (AttachData attachData : tp)
                        mailBoxData.getListAttachMent().remove(attachData);
            }
        if ((isUpload + isFail) == mailBoxData.getListAttachMent().size()) {
            if (isTask == 0) {
                if (task == 4 || task == 3) {
                    MailNo = mailBoxData.getMailNo();
                    HttpRequest.getInstance().ComposeMail(mailBoxData, new BaseHTTPCallBack() {
                        @Override
                        public void onHTTPSuccess() {
                            HttpRequest.getInstance().deleteEmail(MailNo, new BaseHTTPCallBack() {
                                @Override
                                public void onHTTPSuccess() {
                                    if (isVisible()) {
                                        Util.showMessage(getString(R.string.string_alert_send_mail_success));
                                        EventBus.getDefault().post(new NewMailEvent("1"));
                                        isSendMail = true;
                                        Intent intent = new Intent(getContext(), ListEmailActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                }

                                @Override
                                public void onHTTPFail(ErrorData errorDto) {
                                    if (isVisible()) {
                                        Util.showMessageShort(errorDto.getMessage());
                                        getActivity().finish();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onHTTPFail(ErrorData errorDto) {
                            if (isVisible()) {
                                Util.showMessageShort(errorDto.getMessage());
                            }
                        }
                    });
                } else if (task == 0) {
                    HttpRequest.getInstance().ForwardMail(mailBoxData, this);
                } else if (task == 1 || task == 2) {
                    HttpRequest.getInstance().ReplyMail(mailBoxData, this);
                }
            } else if (isTask == 1) {
                HttpRequest.getInstance().DrafComposeMail(mailBoxData, FragmentMailCreate.this);
            } else if (isTask == 2) {
                HttpRequest.getInstance().DrafReplyMail(mailBoxData, FragmentMailCreate.this);
            } else {
                HttpRequest.getInstance().DrafForwardMail(mailBoxData, FragmentMailCreate.this);
            }
        } else {
            String notice = getString(R.string.string_alert_send_mail_fail);
            Util.showMessage(notice);
            isSend = true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case FILE_PICKER_SELECT:
                        if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                            // For JellyBean and above
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                ClipData clip = data.getClipData();
                                if (clip != null) {
                                    for (int i = 0; i < clip.getItemCount(); i++) {
                                        Uri uri = clip.getItemAt(i).getUri();
                                        GetFile(uri);
                                        // Do something with the URI
                                    }
                                }
                                // For Ice Cream Sandwich
                            } else {
                                ArrayList<String> paths = data.getStringArrayListExtra
                                        (FilePickerActivity.EXTRA_PATHS);

                                if (paths != null) {
                                    for (String path : paths) {
                                        Uri uri = Uri.parse(path);
                                        // Do something with the URI
                                        GetFile(uri);
                                    }
                                }
                            }

                        } else {
                            Uri uri = data.getData();
                            GetFile(uri);
                            // Do something with the URI
                        }
                        break;
                    case Statics.ORGANIZATION_TO_ACTIVITY:
                    case Statics.ORGANIZATION_CC_ACTIVITY:
                    case Statics.ORGANIZATION_BCC_ACTIVITY:

                        Gson gson = new Gson();

                        Type userListType = new TypeToken<ArrayList<PersonData>>(){}.getType();

                        ArrayList<PersonData> listSelected = gson.fromJson(data.getExtras().getString(StaticsBundle.BUNDLE_LIST_PERSON), userListType);

                        handleSelectedOrganizationResult(requestCode, listSelected);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTokenAdded(Object token) {
        if (edtMailCreateTo.getObjects() != null && edtMailCreateTo.getObjects().size() > 1) {
            edtMailCreateTo.allowCollapse(true);
        }
    }

    @Override
    public void onTokenRemoved(Object token) {

    }

    @Override
    public void onClick(View v) {
        ArrayList<PersonData> selectedList = new ArrayList<>();
        switch (v.getId()) {
            case R.id.imgMailCreateOrgTo:
                startOrganizationActivity(new ArrayList<>(edtMailCreateTo.getObjects()), Statics.ORGANIZATION_TO_ACTIVITY);
                break;
            case R.id.imgMailCreateOrgCc:
                startOrganizationActivity(new ArrayList<>(edtMailCreateCc.getObjects()), Statics.ORGANIZATION_CC_ACTIVITY);
                break;

            case R.id.imgMailCreateOrgBcc:
                startOrganizationActivity(new ArrayList<>(edtMailCreateBcc.getObjects()), Statics.ORGANIZATION_BCC_ACTIVITY);
                break;
            //add new function contact 231018
            case R.id.imgMailCreateContactTo:
                startContactActivity(selectedList, Statics.ORGANIZATION_TO_ACTIVITY);
                break;
            case R.id.imgMailCreateContactCc:
                startContactActivity(selectedList, Statics.ORGANIZATION_CC_ACTIVITY);
                break;
            case R.id.imgMailCreateContactBcc:
                startContactActivity(selectedList, Statics.ORGANIZATION_BCC_ACTIVITY);
                break;
        }
    }

    private void startOrganizationActivity(ArrayList<PersonData> selectedList, int type) {
        Log.d(TAG, "startOrganizationActivity");
        Intent i = new Intent(getActivity(), OrganizationChartActivity.class);
        Gson gson = new Gson();
        String arrayData = gson.toJson(selectedList);
        i.putExtra(Constants.SELECTED_LIST, arrayData);
        startActivityForResult(i, type);
    }

    private void startContactActivity(ArrayList<PersonData> selectedList, int type) {
        Log.d(TAG, "startOrganizationActivity");
        Intent i = new Intent(getActivity(), ContactActivity.class);
        i.putExtra("TYPE", type);
        startActivity(i);
    }

    //isTask: 0-Compose 1-DrafCompose
    public void SendMail(String content, String subject, int isTask) {
        isSend = false;
        String dataContent;
        dataContent = content;
        mailBoxData.setListPersonDataBcc(new ArrayList<>());
        mailBoxData.setListPersonDataCc(new ArrayList<>());
        mailBoxData.setListPersonDataTo(new ArrayList<>());
        mailBoxData.getListPersonDataBcc().addAll(edtMailCreateBcc.getObjects());
        mailBoxData.getListPersonDataTo().addAll(edtMailCreateTo.getObjects());
        mailBoxData.getListPersonDataCc().addAll(edtMailCreateCc.getObjects());
        mailBoxData.setContent(dataContent);
        mailBoxData.setSubject(subject);
        mailBoxData.setPriority("3");

        if (task == 0)
            if (dataContent != null) {
                if (tp != null && tp.size() > 0)
                    for (AttachData attachData : tp)
                        mailBoxData.getListAttachMent().remove(attachData);
            }
        if ((isUpload + isFail) == mailBoxData.getListAttachMent().size()) {
            if (isTask == 0) {
                if (task == 4 || task == 3) {
                    MailNo = mailBoxData.getMailNo();
                    HttpRequest.getInstance().ComposeMail(mailBoxData, new BaseHTTPCallBack() {
                        @Override
                        public void onHTTPSuccess() {
                            HttpRequest.getInstance().deleteEmail(MailNo, new BaseHTTPCallBack() {
                                @Override
                                public void onHTTPSuccess() {
                                    if (isVisible()) {
                                        Util.showMessage(getString(R.string.string_alert_send_mail_success));
                                        EventBus.getDefault().post(new NewMailEvent("1"));
                                        isSendMail = true;
                                        Intent intent = new Intent(getContext(), ListEmailActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                }

                                @Override
                                public void onHTTPFail(ErrorData errorDto) {
                                    if (isVisible()) {
                                        Util.showMessageShort(errorDto.getMessage());
                                        getActivity().finish();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onHTTPFail(ErrorData errorDto) {
                            if (isVisible()) {
                                Util.showMessageShort(errorDto.getMessage());
                            }
                            askToSave(mailBoxData, 0);
                        }
                    });
                } else if (task == 0) {
                    HttpRequest.getInstance().ForwardMail(mailBoxData, this);
                } else if (task == 1 || task == 2) {
                    HttpRequest.getInstance().ReplyMail(mailBoxData, this);
                }
            } else if (isTask == 1 || isTask == 3) {
                HttpRequest.getInstance().DrafComposeMail(mailBoxData, FragmentMailCreate.this);
            } else if (isTask == 2) {
                HttpRequest.getInstance().DrafReplyMail(mailBoxData, FragmentMailCreate.this);
            } else {
                HttpRequest.getInstance().DrafForwardMail(mailBoxData, FragmentMailCreate.this);
            }
        } else {
            String notice = getString(R.string.string_alert_send_mail_fail);
            Util.showMessage(notice);
            isSend = true;
        }
    }

    //task : 0 - Create, reply, forward
    //1 - draft
    public boolean checkDraft(int task) {
        String subject = edtMailCreateSubject.getText().toString();
        String content = edtMailCreateContent.getText().toString();

        if (this.task == 4) {
            if (edtMailCreateTo.getObjects().size() != 0 && edtMailCreateBcc.getObjects().size() != 0 && edtMailCreateCc.getObjects().size() != 0) {
                return false;
            } else {
                return !(TextUtils.isEmpty(content) && attachData == null && TextUtils.isEmpty(subject));
            }
        } else if (this.task == 0) {
            if ((edtMailCreateTo.getObjects().size() != 0)) {
                return task != 0;
            } else {
                return !(TextUtils.isEmpty(content) && attachData == null);
            }
        } else if (this.task == 3) {
            if ((edtMailCreateTo.getObjects().size() != 0)) {
                return task != 0;
            } else {
                return !(TextUtils.isEmpty(content) && attachData == null);
            }
        } else {
            return task != 0 && !(TextUtils.isEmpty(content) && attachData == null);

        }
    }

    public void DrafMail() {
        final List<String> itemList = new ArrayList<>();
        itemList.add(getString(R.string.string_save_draft));
        if (task == 3)
            itemList.add(getString(R.string.string_delete_draft));
        else {
            itemList.add(getString(R.string.string_discard_draft));
        }

        if (!isChange) {
            MailHelper.showConfirmDialogYesNo(getActivity(), getString(R.string.content_draft), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String subject = edtMailCreateSubject.getText().toString();
                    String content = edtMailCreateContent.getText().toString();
                    if (task == 4) {
                        Log.d("sssDraft", "task4");
                        SendMail(content, subject, 1);
                    } else if (task == 0) {
                        Log.d("sssDraft", "task0");
                        SendMail(content, subject, 3);
                    } else if (task == 2) {
                        Log.d("sssDraft", "task1/2");
                        SendMail(content, subject, 2);
                    } else if (task == 1 || task == 3) {
                        SendMail(content, subject, 3);
                    }
                }
            }, view -> {
                getActivity().finish();
            });
        } else {
            getActivity().finish();
        }
    }

    @Override
    public void OnMailDetailSuccess(MailBoxData mailBoxData) {
        if (isVisible()) {
            if (mailBoxData != null)
                this.mailBoxData = mailBoxData;
            bindData(mailBoxData);
        }
    }

    @Override
    public void OnMaillDetailFail(ErrorData errorData) {
        if (isVisible()) {
            Util.showMessageShort(errorData.getMessage());
        }
    }

    @Override
    public void onHTTPSuccess() {
        if (isBack) {
            isBack = false;
            Util.showMessage(getString(R.string.string_alert_send_mail_success));
            isSendMail = true;
        } else {
            Util.showMessage(getString(R.string.string_alert_save_mail_success));
        }
        if (isVisible()) {
            Intent intent = new Intent(getActivity(), ListEmailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            if (ActivityMailCreate.Instance != null) {
                ActivityMailCreate.Instance.finish();
            }
        }
    }

    @Override
    public void onHTTPFail(ErrorData errorDto) {
        Util.showMessage(getString(R.string.string_alert_send_mail_fail));
        isSend = true;
        askToSave(mailBoxData, 1);
    }

    @Override
    public void OnGetListOfMailAccountSuccess(ArrayList<AccountData> accountData) {
        if (accountData != null) {
            DaZoneApplication.getInstance().getPrefs().putMailAccount(accountData);
            listUser.addAll(accountData);
            if (adapterMailCreateFromSpiner != null)
                adapterMailCreateFromSpiner.notifyDataSetChanged();
        }
    }

    @Override
    public void OnGetListOfMailAccountFail(ErrorData errorData) {
        ArrayList<AccountData> accountData = DaZoneApplication.getInstance().getPrefs().getMailAccount();
        if (accountData != null) {
            listUser.addAll(accountData);
            if (adapterMailCreateFromSpiner != null)
                adapterMailCreateFromSpiner.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onEvent(EventRequest eventPersion) {
        if (eventPersion != null) {
            switch (eventPersion.getType()) {
                case Statics.ORGANIZATION_TO_ACTIVITY:
                    edtMailCreateTo.clear();
                    mailBoxData.getListPersonDataTo().clear();
                    mailBoxData.getListPersonDataTo().addAll(eventPersion.getDatas());
                    for (PersonData data : eventPersion.getDatas()) {
                        edtMailCreateTo.addObject(data);
                    }


                    break;
                case Statics.ORGANIZATION_BCC_ACTIVITY:
                    edtMailCreateBcc.clear();
                    mailBoxData.getListPersonDataTo().clear();
                    mailBoxData.getListPersonDataTo().addAll(eventPersion.getDatas());
                    for (PersonData data : eventPersion.getDatas()) {
                        edtMailCreateBcc.addObject(data);
                    }
                    break;
                case Statics.ORGANIZATION_CC_ACTIVITY:
                    edtMailCreateCc.clear();
                    mailBoxData.getListPersonDataTo().clear();
                    mailBoxData.getListPersonDataTo().addAll(eventPersion.getDatas());
                    for (PersonData data : eventPersion.getDatas()) {
                        edtMailCreateCc.addObject(data);
                    }
                    break;

            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public void push(double percent, String nameFile) {
        if (mProgressBar != null) {
            mProgressBar.setProgress((int) percent);
        }
    }

    protected String handleBackground(String... params) {
        String filePath = params[0];
        String fileName = params[1];
        return fileUploadDownload.uploadFileToServer(filePath, fileName, this);
    }

    private void updateTextView() {
        txtPercentage.setText(countFile + " / " + mailBoxData.getListAttachMent().size());
    }

    private class Upload extends UploadFileToServer {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            txtPercentage.setVisibility(View.VISIBLE);
            countFile = isUpload + 1;
            updateTextView();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            List<AttachData> list = new ArrayList<>();
            try {
                JSONObject jo = new JSONObject(result);
                String namefile = jo.getString("name");
                boolean status = jo.getBoolean("success");
                long sizefile = jo.getLong("size");
                AttachData attachData = new AttachData();
                attachData.setFileName(namefile);
                attachData.setFileSize(sizefile);
                list.add(attachData);
                mailBoxDataNew.getListAttachMent().addAll(list);
                if (status) {
                    isUpload++;
                    int countFile = isUpload + 1;
                    txtPercentage.setText(countFile + " / " + mailBoxData.getListAttachMent().size());
                    if (isUpload == mailBoxData.getListAttachMent().size()) {
                        mProgressBar.setVisibility(View.GONE);
                        if (progress_bar != null) {
                            progress_bar.setVisibility(View.GONE);
                        }
                        txtPercentage.setVisibility(View.GONE);
                    }
                } else {
                    isFail++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            return handleBackground(params);
        }
    }

    private void askToSave(MailBoxData data, int type) {
        AlertDialogView.normalAlertDialogWithCancel(getActivity(), getString(R.string.save), getString(R.string.txt_ask_to_save), getString(R.string.string_title_mail_create_ok), getString(R.string.no), new AlertDialogView.OnAlertDialogViewClickEvent() {
            @Override
            public void onOkClick(DialogInterface alertDialog) {
                if (isTask == 0) {
                    if (task == 4 || task == 3) {
                        DataManager.Instance().saveDraftMail(mailBoxData, DraftMailDataSource.COMPOSE_TYPE_NEW);
                    } else if (task == 0) {
                        DataManager.Instance().saveDraftMail(mailBoxData, DraftMailDataSource.COMPOSE_TYPE_FORWARRD);
                    } else if (task == 1 || task == 2) {
                        DataManager.Instance().saveDraftMail(mailBoxData, DraftMailDataSource.COMPOSE_TYPE_REPLY);
                    }
                } else if (isTask == 1) {
                    DataManager.Instance().saveDraftMail(mailBoxData, DraftMailDataSource.COMPOSE_TYPE_NEW_DRAFT);
                } else if (isTask == 2) {
                    DataManager.Instance().saveDraftMail(mailBoxData, DraftMailDataSource.COMPOSE_TYPE_REPLY_DRAFT);
                } else {
                    DataManager.Instance().saveDraftMail(mailBoxData, DraftMailDataSource.COMPOSE_TYPE_FORWARRD_DRAFT);
                }
                getActivity().finish();
            }

            @Override
            public void onCancelClick() {
            }
        });
    }
}
