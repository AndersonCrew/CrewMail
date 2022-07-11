package com.dazone.crewemail.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.data.AccountData;
import com.dazone.crewemail.data.MailBoxMenuData;
import com.dazone.crewemail.data.PersonData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class Prefs {
    private static final String PREF_FLAG_GMC_ID = "flag_gmc_id";
    private final String SHARED_PREFERENCES_NAME = "crew_mail_pref";
    private final String ACCESS_TOKEN = "accesstoken";
    private final String USER_JSON_INFO = "user_json";
    private final String ACCESSTOKEN = "accesstoken";
    private final String MAIL_MENU_LIST = "mail_menu_list";
    private final String LAST_SYNC = "last_sync";
    private final String SYNC_MAIL_BOX = "sync_box_no";
    private final String MAIL_ACCOUNT_LIST = "mail_account_list";
    private final String MOD_DATE = "modify_date";
    private final String LIST_ORGANIZATION = "LIST_ORGANIZATION";

    private final String MAIL_IST = "data_list";
    private SharedPreferences prefs;

    public Prefs() {
        prefs = DaZoneApplication.getInstance().getApplicationContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void putBooleanValue(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    public boolean getBooleanValue(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

    public String getAccessToken() {
        return prefs.getString(ACCESS_TOKEN, "");
    }

    public void putAccessToken(String key) {
        prefs.edit().putString(ACCESS_TOKEN, key).apply();
    }

    public String getServerSite() {
        return prefs.getString(Constants.DOMAIN, "");
    }

    public String getDomainCheckVersion() {
        return prefs.getString(Constants.COMPANY_NAME, "");
    }

    public void putUserData(String userDataJson, String accessToken) {
        if (!TextUtils.isEmpty(userDataJson)) {

            prefs.edit().putString(USER_JSON_INFO, userDataJson).apply();
        }
        if (!TextUtils.isEmpty(accessToken)) {
            prefs.edit().putString(ACCESS_TOKEN, accessToken).apply();
        }
    }

    public String getUserJson() {
        return prefs.getString(USER_JSON_INFO, "");
    }

    public void removeUserData() {
        prefs.edit().remove(ACCESS_TOKEN).apply();
        prefs.edit().remove(USER_JSON_INFO).apply();
    }

    public void putMenuListData(String menuListJson) {
        prefs.edit().putString(MAIL_MENU_LIST, menuListJson).apply();
    }

    public void putListOrganization(ArrayList<PersonData> list) {
        Gson gson = new Gson();
        String arrayData = gson.toJson(list);
        putStringValue(Constants.ORGANIZATION, arrayData);
    }

    public ArrayList<PersonData> getListOrganization() {
        Gson gson = new Gson();
        Type userListType = new TypeToken<ArrayList<PersonData>>(){}.getType();
        return gson.fromJson(new Prefs().getStringValue(Constants.ORGANIZATION, ""), userListType);
    }

    public void removeMenuData() {
        prefs.edit().remove(MAIL_MENU_LIST).apply();
    }

    public void removeSetting() {
        prefs.edit().remove(Statics.KEY_PREFERENCES_PIN).apply();
        prefs.edit().remove(LAST_SYNC).apply();
        prefs.edit().remove(SYNC_MAIL_BOX).apply();
        prefs.edit().remove(MAIL_ACCOUNT_LIST).apply();
    }

    public void removeOrganization() {
        prefs.edit().remove(Constants.ORGANIZATION).apply();
    }

    public String getMenuListData() {
        return prefs.getString(MAIL_MENU_LIST, null);
    }

    public String getStringValue(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }

    public void putStringValue(String KEY, String value) {
        prefs.edit().putString(KEY, value).apply();
    }

    public String getaccesstoken() {
        return getStringValue(ACCESSTOKEN, "");
    }


    public void setGCMregistrationid(String value) {
        putStringValue(PREF_FLAG_GMC_ID, value);
    }

    public void putLongValue(String KEY, long value) {
        prefs.edit().putLong(KEY, value).apply();
    }

    public long getLongValue(String KEY, long defvalue) {
        return prefs.getLong(KEY, defvalue);
    }

    public void putMailBoxNo(int boxNo) {
        prefs.edit().putInt(SYNC_MAIL_BOX, boxNo).apply();
    }

    public int getMailBoxNo() {
        return prefs.getInt(SYNC_MAIL_BOX, 0);
    }

    public ArrayList<Integer> getAllMailBox() {
        ArrayList<Integer> result = new ArrayList<>();
        String cachedMenuJson = DaZoneApplication.getInstance().getPrefs().getMenuListData();
        String accessToken = DaZoneApplication.getInstance().getPrefs().getAccessToken();
        if (!TextUtils.isEmpty(cachedMenuJson) && cachedMenuJson.contains(accessToken + "#@#")) {
            String[] splitString = cachedMenuJson.split("#@#");
            ArrayList<MailBoxMenuData> mailBoxList = MailHelper.getDefaultMailBox(splitString[1]);
            if (mailBoxList != null && mailBoxList.size() > 0)
                for (MailBoxMenuData mailBox : mailBoxList) {
                    result.add(mailBox.getBoxNo());
                }
        }
        return result;
    }

    public long getLastSyncTime() {
        return prefs.getLong(LAST_SYNC, 0);
    }

    public void putLastSyncTime(long lastSyncTime) {
        prefs.edit().putLong(LAST_SYNC, lastSyncTime).apply();
    }


    public ArrayList<AccountData> getMailAccount() {
        ArrayList<AccountData> accountData;
        String data = prefs.getString(MAIL_ACCOUNT_LIST, "");
        Type type = new TypeToken<ArrayList<AccountData>>() {
        }.getType();

        accountData = new Gson().fromJson(data, type);

        return accountData;
    }

    public void putMailAccount(ArrayList<AccountData> accountData) {
        prefs.edit().putString(MAIL_ACCOUNT_LIST, new Gson().toJson(accountData)).apply();
    }

    public String getListData() {
        return prefs.getString(MAIL_IST, "");
    }

    public void putListData(String ListData) {
        prefs.edit().putString(MAIL_IST, ListData).apply();
    }

    public String getModDate() {
        String date = prefs.getString(MOD_DATE, "");
        if(date.isEmpty()){
            date = TimeUtils.showTimeWithoutTimeZone(Calendar.getInstance().getTimeInMillis(), Statics.yyyy_MM_dd_HH_mm_ss_SSS);
            setModDate(date);
        }
        return date;
    }

    public void setModDate(String date) {
        prefs.edit().putString(MOD_DATE, date).apply();
    }
}
