package com.dazone.crewemail.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dazone.crewemail.BuildConfig;
import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.adapter.TagAdapter;
import com.dazone.crewemail.customviews.AlertDialogView;
import com.dazone.crewemail.data.AttachData;
import com.dazone.crewemail.data.MailBoxData;
import com.dazone.crewemail.data.MailBoxMenuData;
import com.dazone.crewemail.data.MailTagMenuData;
import com.dazone.crewemail.data.MenuSortData;
import com.dazone.crewemail.data.PersonData;
import com.dazone.crewemail.data.UserData;
import com.dazone.crewemail.database.UserDBHelper;
import com.dazone.crewemail.interfaces.IDownloadFileAgain;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by THANHTUNG on 18/12/2015.
 */
public class MailHelper {

    //1: Image
    //2: Video
    //3: Audio
    //4: DOC
    //5: XLS
    //6: PDF
    //7: PPT
    //8: File
    public static int getTypeFile(AttachData attachData) {
        int type;
        if (attachData != null && !TextUtils.isEmpty(attachData.getFileType())) {
            switch (attachData.getFileType()) {
                case Statics.IMAGE_GIF:
                case Statics.IMAGE_JPEG:
                case Statics.IMAGE_JPG:
                case Statics.IMAGE_PNG:
                    type = 1;
                    break;
                case Statics.VIDEO_MP4:
                    type = 2;
                    break;
                case Statics.AUDIO_MP3:
                case Statics.AUDIO_AMR:
                case Statics.AUDIO_WMA:
                    type = 3;
                    break;
                case Statics.FILE_DOC:
                case Statics.FILE_DOCX:
                    type = 4;
                    break;
                case Statics.FILE_XLS:
                case Statics.FILE_XLSX:
                    type = 5;
                    break;
                case Statics.FILE_PDF:
                    type = 6;
                    break;
                case Statics.FILE_PPT:
                case Statics.FILE_PPTX:
                    type = 7;
                    break;
                default:
                    type = 8;
                    break;
            }
        } else {
            return 8;
        }
        return type;
    }

    public static void OpenFile(Context context, AttachData attachment) {
        if (attachment != null && !TextUtils.isEmpty(attachment.getPath())) {
            int Type = getTypeFile(attachment);
            String MimeType;
            File file = new File(attachment.getPath());
            if (file.exists()) {
                Uri path = Uri.fromFile(file);
                Intent intent3 = new Intent(Intent.ACTION_VIEW);
                switch (Type) {
                    case 1:
                        MimeType = Statics.MIME_TYPE_IMAGE;
                        break;
                    case 2:
                        MimeType = Statics.MIME_TYPE_VIDEO;
                        break;
                    case 3:
                        MimeType = Statics.MIME_TYPE_AUDIO;
                        break;

                    default:
                        MimeType = Statics.MIME_TYPE_APP;
                        break;
                }
                intent3.setDataAndType(path, MimeType);

                try {
                    context.startActivity(intent3);
                } catch (ActivityNotFoundException e) {
                }
            }
        }
    }

    //isTask 0: Thu gọn
    // 1: Chi tiết
    public static String getContentAddressMail(List<PersonData> personDataList, int isTask) {
        String mail = "";
        if (isTask == 0) {
            for (PersonData personData : personDataList) {
                if (TextUtils.isEmpty(personData.getFullName())) {
                    mail += personData.getEmail() + "\n";
                } else {
                    mail += personData.getFullName() + " <" + personData.getEmail() + ">\n";
                }
            }
        } else {
            for (int i = 0; i < personDataList.size(); i++) {
                PersonData personData = personDataList.get(0);
                if (TextUtils.isEmpty(personData.getFullName())) {
                    mail = personData.getEmail() + "\n";
                } else {
                    mail = personData.getFullName() + " <" + personData.getEmail() + ">\n";
                }
            }
        }
        mail = mail.substring(0, mail.length() - 1);
        return mail;
    }

    public static void setEmailAddress(List<PersonData> personDataList, TextView txtEmail, int isTask, LinearLayout linearLayout) {
        String emailString = "";
        if (personDataList != null && personDataList.size() > 0) {
            emailString = getContentAddressMail(personDataList, isTask);
            txtEmail.setText(emailString);
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }

    public static void setEmailTextViewDetail(PersonData personData, TextView textView) {
        if (personData != null) {
            textView.setText(personData.getFullName() + " <" + personData.getEmail() + ">");
        }
    }

    public static void displayDownloadFileDialog(final Context context, final String url, final String name) {
        AlertDialogView.normalAlertDialogWithCancel(context, context.getString(R.string.app_name), context.getString(R.string.string_alert_download_mail), context.getString(R.string.string_title_mail_create_ok), context.getString(R.string.string_title_mail_create_cancel), new AlertDialogView.OnAlertDialogViewClickEvent() {
            @Override
            public void onOkClick(DialogInterface alertDialog) {
                int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
                final NotificationTask downloadTask = new NotificationTask(context,name,m);
                downloadTask.execute(url,name);
            }

            @Override
            public void onCancelClick() {

            }
        });
    }

    public static void displayDownloadFileAgainDialog(final Context context, final String url, final String name, IDownloadFileAgain listener) {
        AlertDialogView.normalAlertDialogWithCancel(context, context.getString(R.string.app_name), context.getString(R.string.string_alert_download_mail_again), context.getString(R.string.download_again), context.getString(R.string.open_file), new AlertDialogView.OnAlertDialogViewClickEvent() {
            @Override
            public void onOkClick(DialogInterface alertDialog) {
                int num = 0;
                String fileName = name.split("\\.")[0];
                String strCount = "";
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/" + fileName + "." + name.split("\\.")[1]);
                while(file.exists()) {
                    num++;
                    strCount = "(" + num + ")";
                    String fn = fileName + strCount;
                    file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/" + fn + "." + name.split("\\.")[1]);
                }

                String finalName = fileName + strCount + "." + name.split("\\.")[1];
                int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
                final NotificationTask downloadTask = new NotificationTask(context,finalName,m);
                downloadTask.execute(url,finalName);
            }

            @Override
            public void onCancelClick() {
                listener.onOpenFile();
            }
        });
    }


    public static String getUrl(AttachData attachData) {
        String url = DaZoneApplication.getInstance().getPrefs().getServerSite() + Urls.URL_DOWNLOAD_ATTACH + "sessionId=" + new Prefs().getAccessToken() + "&languageCode=" + Locale.getDefault().getLanguage().toUpperCase() +
                "&timeZoneOffset=" + TimeUtils.getTimezoneOffsetInMinutes() + "&fileNo=" + attachData.getAttachNo();
        Util.printLogs("Link tải nè: " + url);
        return url;
    }

    public static String getOriginalMessage(Context context, MailBoxData data) {
        String message = "\n--------- Original Message ---------\n";
        if (data != null) {
            message += Util.getString(R.string.string_title_mail_create_from) + ": " + data.getMailFrom().getFullName() + "<" + data.getMailFrom().getEmail() + ">\n";
            if (data.getListPersonDataTo() != null && data.getListPersonDataTo().size() > 0) {
                message += Util.getString(R.string.string_title_mail_create_to) + ": ";
                for (PersonData personData : data.getListPersonDataTo())
                    message += personData.getEmail() + ", ";
                message += "\n";
            }

            if (data.getListPersonDataCc() != null && data.getListPersonDataCc().size() > 0) {
                message += Util.getString(R.string.string_title_mail_create_cc) + ": ";
                for (PersonData personData : data.getListPersonDataCc())
                    message += personData.getEmail() + ", ";
                message += "\n";
            }

            if (data.getListPersonDataBcc() != null && data.getListPersonDataBcc().size() > 0) {
                message += Util.getString(R.string.string_title_mail_create_bcc) + ": ";
                for (PersonData personData : data.getListPersonDataBcc())
                    message += personData.getEmail() + ", ";
                message += "\n";
            }
            if (Locale.getDefault().getLanguage().toUpperCase().equalsIgnoreCase("KO"))
                message += Util.getString(R.string.string_title_mail_send) + ": " + TimeUtils.displayTimeWithoutOffset(context, data.getDateCreate(), 1) + "\n";
            else
                message += Util.getString(R.string.string_title_mail_send) + ": " + TimeUtils.displayTimeWithoutOffset(context, data.getDateCreate(), 0) + "\n";
            message += Util.getString(R.string.string_title_mail_create_subject) + ": " + data.getSubject() + "\n";
        } else {
            message = "";
        }
        return message;
    }

    public static String getAddType(PersonData personData) {
        if (personData.getType() == 1) {
            return "depart";
        } else if (personData.getType() == 2) {
            return "user";
        } else {
            return "mail";
        }
    }

    public static ArrayList<PersonData> removeMe(ArrayList<PersonData> listTo) {
        UserData userData = UserDBHelper.getUser();
        ArrayList<PersonData> list = new ArrayList<>();
        try {
            if (listTo != null && listTo.size() > 0) {
                for (PersonData personData : listTo) {
                    boolean isCheck;
                    isCheck = userData.getmEmail().equalsIgnoreCase(personData.getEmail());
                    if (isCheck == false) {
                        list.add(personData);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean isValidEmail(String email) {
        Pattern emailPattern = Pattern.compile("^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z0-9._-]+)+");
        return (emailPattern.matcher(email).matches());
    }

    public static void displaySingleChoice(final Context context, List<MailTagMenuData> itemList, DialogInterface.OnClickListener listener, String title) {
        List<String> list = new ArrayList<>();
        final AlertDialog.Builder adb = new AlertDialog.Builder(context).setTitle(title);
        for (MailTagMenuData mailTagMenuData : itemList) {
            if (mailTagMenuData.getTagNo() == -1) {
                list.add(mailTagMenuData.getName());
            } else {
                list.add("Tag: " + mailTagMenuData.getName());
            }
        }
        ArrayAdapter<String> adapterspiner = new ArrayAdapter<>(context, android.R.layout.select_dialog_item, list);
        adb.setAdapter(adapterspiner, listener);
        adb.show();
    }

    public static void displaySingleChoiceList(final Activity context, List<MailTagMenuData> itemList, DialogInterface.OnClickListener listener, String title) {

        final AlertDialog.Builder adb = new AlertDialog.Builder(context).setTitle(title);
        TagAdapter adapter = new TagAdapter(context, R.layout.tag, itemList);
        adb.setAdapter(adapter, listener);
        adb.show();
    }

    public static Dialog showConfirmDialogYesNo(Context context, String message,
                                                final View.OnClickListener okListener, final View.OnClickListener cancleListener) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_confirm_draft_mail);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView content = dialog.findViewById(R.id.txt_dia);
        content.setText(message);
        TextView cancle = dialog.findViewById(R.id.btn_no);
        cancle.setOnClickListener(view -> cancleListener.onClick(view));

        TextView save = dialog.findViewById(R.id.btn_yes);
        save.setOnClickListener(view -> okListener.onClick(view));
        dialog.show();

        return dialog;

    }

    /**
     * Convert Json String to LinkedHashMap
     *
     * @param jsonString jsonString
     * @return map menu data
     */
    public static LinkedHashMap<String, Object> convertJsonStringToMap(String jsonString) {
        LinkedHashMap<String, Object> menuMap = new LinkedHashMap<>();
        try {
            Gson gson = new Gson();
            JSONObject jo = new JSONObject(jsonString);
            // tag mail box
            String mailTag = jo.getString("MailTags");
            Type listTagType = new TypeToken<List<MailTagMenuData>>() {
            }.getType();
            List<MailTagMenuData> tagList = gson.fromJson(mailTag, listTagType);
            menuMap.put("MailTags", tagList);
            // other mail box
            JSONObject mailBoxJo = jo.getJSONObject("MailBoxs");
            Iterator<String> keySets = mailBoxJo.keys();
            while (keySets.hasNext()) {
                String key = keySets.next();
                String value = mailBoxJo.getString(key);
                Type listType = new TypeToken<List<MailBoxMenuData>>() {
                }.getType();
                List<MailBoxMenuData> listMailBox = gson.fromJson(value, listType);
                menuMap.put(key, listMailBox);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
        }
        return menuMap;
    }

    public static int getColorTag(int ImageNo) {
        switch (ImageNo) {
            case 0:
                return R.drawable.tag_icon_01;
            case 1:
                return R.drawable.tag_icon_02;
            case 2:
                return R.drawable.tag_icon_03;
            case 3:
                return R.drawable.tag_icon_04;
            case 4:
                return R.drawable.tag_icon_05;
            case 5:
                return R.drawable.tag_icon_06;
            case 6:
                return R.drawable.tag_icon_07;
            case 7:
                return R.drawable.tag_icon_08;
            case 8:
                return R.drawable.tag_icon_09;
            case 9:
                return R.drawable.tag_icon_10;
            case 10:
                return R.drawable.tag_icon_11;
            case 11:
                return R.drawable.tag_icon_12;
            case 12:
                return R.drawable.tag_icon_13;
            case 13:
                return R.drawable.tag_icon_14;
            case 14:
                return R.drawable.tag_icon_15;
            case 15:
                return R.drawable.tag_icon_16;
            case 16:
                return R.drawable.tag_icon_17;
            case 17:
                return R.drawable.tag_icon_18;
            case 18:
                return R.drawable.tag_icon_19;
            case 19:
                return R.drawable.tag_icon_20;
            case 20:
                return R.drawable.tag_icon_21;
        }
        return 0;
    }


    //isTask : 1 - get index of Inbox
    //: 0 - get index of OutBox
    public static int getIndexOfMenu(List<MailBoxMenuData> menuList, int isTask) {
        int size = 0;
        if (menuList != null && menuList.size() > 0) {
            size = menuList.size() - 1;
            for (int i = 0; i < menuList.size(); i++) {
                MailBoxMenuData mailBoxMenuData = menuList.get(i);
                if (isTask == 1) {
                    if (mailBoxMenuData.getName().equalsIgnoreCase(Util.getString(R.string.string_title_menu_sortbox))) {
                        return i;
                    }
                } else {
                    if (mailBoxMenuData.getName().equalsIgnoreCase(Util.getString(R.string.string_title_menu_outbox))) {
                        return i;
                    }
                }
            }
        }
        return size;
    }

    public static int getSelectPosition(ArrayList<MenuSortData> list) {
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            MenuSortData menuSortData = list.get(i);
            if (menuSortData.getIsCheck() == 1 || menuSortData.getIsCheck() == 2) {
                index = i;
            }
        }
        return index;
    }

    public static int checkEmailV2(ArrayList<PersonData> list) {
        int value = -1;

        if (list.size() <= 0) {
            //todo mail khong duoc nhập
            value = 0;
        } else if (list.size() == 1) {
            //todo sai dinh dang
            value = checkValidateMail(list);

        } else if (list.size() > 1) {
            value = 2;
        }
        return value;
    }

    private static int checkValidateMail(ArrayList<PersonData> list) {
        String email = list.get(0).getEmail();
        if (email.isEmpty()) {
            email = list.get(0).getmEmail();
        }
        if (!MailHelper.isValidEmail(email)) {
            return 1;
        } else {
            return 2;
        }
    }

    public static ArrayList<MailBoxMenuData> getDefaultMailBox(String jsonString) {
        ArrayList<MailBoxMenuData> result = new ArrayList<>();
        try {
            Gson gson = new Gson();
            JSONObject jo = new JSONObject(jsonString);

            String mailBoxJo = jo.getJSONObject("MailBoxs").getString("DefaultMailBoxs");

            Type listType = new TypeToken<List<MailBoxMenuData>>() {
            }.getType();
            result = gson.fromJson(mailBoxJo, listType);


        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
        }
        return result;
    }

}
