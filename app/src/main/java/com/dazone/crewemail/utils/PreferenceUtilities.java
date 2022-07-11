package com.dazone.crewemail.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.dazone.crewemail.DaZoneApplication;

public class PreferenceUtilities {
    private SharedPreferences mPreferences;

    private final String USER_ID = "user_id";
    private final String PASS = "password";
    private final String DOMAIN = "domain";
    private final String ENTRANCE_DISPLAY = "EntranceDateDisplay ";
    private final String BIRTHDAY_DISPLAY  = "BirthDateDisplay ";
    private final String IS_MAIL = "is_mail";

    public PreferenceUtilities() {
        mPreferences = DaZoneApplication.getInstance().getApplicationContext().getSharedPreferences("CrewBoard_Prefs", Context.MODE_PRIVATE);
    }

    public void setIsMail(String mail) {
        mPreferences.edit().putString(IS_MAIL, mail).apply();
    }

    public String getIsMail() {
        return mPreferences.getString(IS_MAIL, "");
    }

    public void setUserId(String userId) {
        mPreferences.edit().putString(USER_ID, userId).apply();
    }

    public String getUserId() {
        return mPreferences.getString(USER_ID, "");
    }

    public void setPass(String pass) {
        mPreferences.edit().putString(PASS, pass).apply();
    }

    public String getPass() {
        return mPreferences.getString(PASS, "");
    }

    public void setDomain(String domain) {
        mPreferences.edit().putString(DOMAIN, domain).apply();
    }

    public void setDisPlayEntrance(boolean setDisPlayEntrance) {
        mPreferences.edit().putBoolean(ENTRANCE_DISPLAY, setDisPlayEntrance).apply();
    }

    public void setDisPlayBirthday(boolean setDisPlayBirthday) {
        mPreferences.edit().putBoolean(BIRTHDAY_DISPLAY, setDisPlayBirthday).apply();
    }

    public boolean getDisPlayEntrance() {
        return mPreferences.getBoolean(ENTRANCE_DISPLAY, true);
    }

    public boolean getDisPlayBirthday() {
        return mPreferences.getBoolean(BIRTHDAY_DISPLAY, true);
    }

    public String getDomain() {
        return mPreferences.getString(DOMAIN, "");
    }

}