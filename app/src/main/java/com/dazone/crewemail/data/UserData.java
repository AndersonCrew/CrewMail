package com.dazone.crewemail.data;

import android.content.Context;
import androidx.databinding.BaseObservable;
import android.text.TextUtils;
import android.util.Log;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.database.OrganizationUserDBHelper;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.Util;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sherry on 12/8/15.
 */
public class UserData extends BaseObservable {

    private static UserData _instance;

    @SerializedName("Id")
    public int mId;
    @SerializedName("BirthDateDisplay")
    public boolean BirthDateDisplay;
    @SerializedName("EntranceDateDisplay")
    public boolean EntranceDateDisplay;
    @SerializedName("CompanyNo")
    private int mCompanyNo;
    @SerializedName("PermissionType")
    private int mPermissionType;//0 normal, 1 admin
    @SerializedName("userID")
    private String mUserId;
    @SerializedName("FullName")
    private String mFullName;
    @SerializedName("session")
    private String mSession;
    @SerializedName("avatar")
    private String mAvatar;
    @SerializedName("NameCompany")
    private String mCompanyName;
    @SerializedName("MailAddress")
    private String mEmail;

    protected String pass;
    private String phone;
    public String domain;

    public boolean isBirthDateDisplay() {
        return BirthDateDisplay;
    }

    public void setBirthDateDisplay(boolean birthDateDisplay) {
        BirthDateDisplay = birthDateDisplay;
    }

    public boolean isEntranceDateDisplay() {
        return EntranceDateDisplay;
    }

    public void setEntranceDateDisplay(boolean entranceDateDisplay) {
        EntranceDateDisplay = entranceDateDisplay;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public synchronized static UserData getInstance() {
        if (_instance == null) {
            _instance = new UserData();
        }
        return _instance;
    }

    public synchronized static UserData getUserInformation() {
        getInstance();
        if (_instance.mId == 0) {
            String userJson = DaZoneApplication.getInstance().getPrefs().getUserJson();
            if (!TextUtils.isEmpty(userJson)) {
                Gson gson = new Gson();
                _instance = gson.fromJson(userJson, UserData.class);
            }
        }
        return _instance;
    }


    public void logout(Context context) {
        Log.d(">>>sssDebugData", " ACCESS_TOKEN logout");
        DaZoneApplication.getInstance().getPrefs().removeUserData();
        DaZoneApplication.getInstance().getPrefs().removeMenuData();
        DaZoneApplication.getInstance().getPrefs().removeSetting();
        DaZoneApplication.getInstance().getPrefs().removeOrganization();
        DaZoneApplication.getInstance().getPrefs().putLongValue(Statics.SAVE_BOX_NO_PREF, 0);
        OrganizationUserDBHelper.clearData();
        Util.clearList(context);
        _instance = null;

    }


    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getCompanyNo() {
        return mCompanyNo;
    }

    public void setCompanyNo(int companyNo) {
        mCompanyNo = companyNo;
    }

    public int getPermissionType() {
        return mPermissionType;
    }

    public void setPermissionType(int permissionType) {
        mPermissionType = permissionType;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String fullName) {
        mFullName = fullName;
    }

    public String getSession() {
        if (mSession == null) {
            return "";
        } else {
            return mSession;
        }
    }

    public void setSession(String session) {
        mSession = session;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public String getCompanyName() {
        return mCompanyName;
    }

    public void setCompanyName(String companyName) {
        mCompanyName = companyName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }
}
