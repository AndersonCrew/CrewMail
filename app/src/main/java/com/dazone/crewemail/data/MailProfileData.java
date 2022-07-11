package com.dazone.crewemail.data;

import android.os.Parcel;
import android.text.TextUtils;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.utils.TimeUtils;
import com.dazone.crewemail.utils.Util;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by THANHTUNG on 26/01/2016.
 */
public class MailProfileData implements Serializable {
    @SerializedName("UserNo")
    public String UserNo;
    @SerializedName("UserID")
    public String UserId = "";
    @SerializedName("Name_Default")
    public String NameDefault = "";
    @SerializedName("Name_EN")
    public String NameEn = "";
    @SerializedName("Name")
    public String Name = "";
    @SerializedName("MailAddress")
    public String MailAddress = "";
    @SerializedName("CellPhone")
    public String CellPhone = "";
    @SerializedName("CompanyPhone")
    public String CompanyPhone = "";
    @SerializedName("AvatarUrl")
    public String Avatar = "";

    @SerializedName("CompanyId")
    public String CompanyId = "";

    @SerializedName("Belongs")
    public List<Belong> belongs;

    @SerializedName("DepartName")
    public String departName = "";
    @SerializedName("PositionName")
    public String positionName = "";
    @SerializedName("Password")
    public String password = "";
    @SerializedName("EntranceDate")
    public String EntranceDate = "";
    @SerializedName("BirthDate")
    private String BirthDate;

    public String getBirthDate() {
        if (DaZoneApplication.getInstance().getPreferenceUtilities().getDisPlayBirthday()) {
            return TimeUtils.displayTimeWithoutOffset(BirthDate);
        } else {
            return null;
        }
    }

    public void setBirthDate(String birthDate) {
        BirthDate = birthDate;
    }

    public String getEntranceDate() {
        if (DaZoneApplication.getInstance().getPreferenceUtilities().getDisPlayEntrance()) {
            return TimeUtils.displayTimeWithoutOffset(EntranceDate);
        } else {
            return null;
        }
    }

    public String getPositionDepartName() {
        return positionDepartName;
    }

    public void setPositionDepartName(String positionDepartName) {
        this.positionDepartName = positionDepartName;
    }

    public void setEntranceDate(String entranceDate) {
        EntranceDate = entranceDate;
    }

    public String positionDepartName = "";

    public String getCompanyPhone() {
        return CompanyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        CompanyPhone = companyPhone;
    }

    protected MailProfileData(Parcel in) {
        UserNo = in.readString();
        UserId = in.readString();
        NameDefault = in.readString();
        NameEn = in.readString();
        Name = in.readString();
        MailAddress = in.readString();
        CellPhone = in.readString();
        CompanyPhone = in.readString();
        EntranceDate = in.readString();
        BirthDate = in.readString();
        Avatar = in.readString();
        CompanyId = in.readString();
        departName = in.readString();
        positionName = in.readString();
        password = in.readString();
        positionDepartName = in.readString();
    }

    public String getFullName() {
        String temp = "";
        if (Util.isPhoneLanguageEN()) {
            if (!TextUtils.isEmpty(NameEn)) {
                temp = NameEn;
            } else {
                temp = NameDefault;
            }
        } else {
            if (!TextUtils.isEmpty(NameDefault)) {
                temp = NameDefault;
            } else {
                temp = NameEn;
            }
        }
        return temp;
    }

    public String getUserNo() {
        return UserNo;
    }

    public void setUserNo(String userNo) {
        UserNo = userNo;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getNameDefault() {
        return NameDefault;
    }

    public void setNameDefault(String nameDefault) {
        NameDefault = nameDefault;
    }

    public String getNameEn() {
        return NameEn;
    }

    public void setNameEn(String nameEn) {
        NameEn = nameEn;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMailAddress() {
        return MailAddress;
    }

    public void setMailAddress(String mailAddress) {
        MailAddress = mailAddress;
    }

    public String getCellPhone() {
        return CellPhone;
    }

    public void setCellPhone(String cellPhone) {
        CellPhone = cellPhone;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public String getCompanyId() {
        return CompanyId;
    }

    public void setCompanyId(String companyId) {
        CompanyId = companyId;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Belong> getBelongs() {
        return belongs;
    }

    public void setBelongs(List<Belong> belongs) {
        this.belongs = belongs;
    }


}
