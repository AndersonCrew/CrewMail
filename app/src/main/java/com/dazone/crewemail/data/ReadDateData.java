package com.dazone.crewemail.data;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by THANHTUNG on 02/02/2016.
 */
public class ReadDateData {
    @SerializedName("LogNo")
    private Long logNo;
    @SerializedName("Name")
    private String name;
    @SerializedName("Address")
    private String Address;
    @SerializedName("SentType")
    private String SentType;
    @SerializedName("Status")
    private String Status;
    @SerializedName("CanSentCancel")
    private Boolean CanSentCancel;
    @SerializedName("AvatarUrl")
    private String AvatarUrl;
    @SerializedName("StatusColor")
    private String StatusColor;
    private int mColor;
    private boolean mIsSelected;

    public ReadDateData() {
    }

    public Long getLogNo() {
        return logNo;
    }

    public void setLogNo(Long logNo) {
        this.logNo = logNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSentType() {
        return SentType;
    }

    public void setSentType(String sentType) {
        SentType = sentType;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Boolean getCanSentCancel() {
        return CanSentCancel;
    }

    public void setCanSentCancel(Boolean canSentCancel) {
        CanSentCancel = canSentCancel;
    }

    public String getAddress() {
        return Address;
    }

    public int getmColor() {
        return mColor;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getAvatarUrl() {
        return AvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        AvatarUrl = avatarUrl;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean mIsSelected) {
        this.mIsSelected = mIsSelected;
    }

    public String getStatusColor() {
        return StatusColor;
    }

    public void setStatusColor(String statusColor) {
        StatusColor = statusColor;
    }

    public String getFirstLetterOfName(){
        if(!TextUtils.isEmpty(name)){
            if(name.equalsIgnoreCase("\""))
            {
                return name.substring(1,2).toUpperCase();
            }else
                return name.substring(0,1).toUpperCase();
        }else{
            return getFirstLetterOfEmail();
        }
    }

    public String getFirstLetterOfEmail(){
        if(!TextUtils.isEmpty(Address)){
            if(Address.equalsIgnoreCase("\""))
            {
                return Address.substring(1,2).toUpperCase();
            }else
                return Address.substring(0,1).toUpperCase();
        }else{
            return "";
        }
    }
}
