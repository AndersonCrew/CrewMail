package com.dazone.crewemail.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Dinh Huynh on 12/9/15.
 */
public class MailTagMenuData implements Serializable {

    @SerializedName("TagNo")
    private int mTagNo;
    @SerializedName("ImageNo")
    private int mImageNo;
    @SerializedName("Name")
    private String mName;
    @SerializedName("TotalCount")
    private int mTotalCount;
    @SerializedName("UnReadCount")
    private int mUnreadCount;
    @SerializedName("IsReadMail")
    private boolean isReadMail;
    private boolean isCheck;

    public MailTagMenuData(int mTagNo, int mImageNo, String mName, boolean isCheck) {
        this.mTagNo = mTagNo;
        this.mImageNo = mImageNo;
        this.mName = mName;
        this.isCheck = isCheck;
    }

    public int getTagNo() {
        return mTagNo;
    }

    public int getTagNoResourceId(){
        switch (mTagNo){
            case 1:
                break;
        }
        return 0;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public void setTagNo(int tagNo) {
        mTagNo = tagNo;
    }

    public int getImageNo() {
        return mImageNo;
    }

    public void setImageNo(int imageNo) {
        mImageNo = imageNo;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getTotalCount() {
        return mTotalCount;
    }

    public void setTotalCount(int totalCount) {
        mTotalCount = totalCount;
    }

    public int getUnreadCount() {
        return mUnreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        mUnreadCount = unreadCount;
    }

    public boolean isReadMail() {
        return isReadMail;
    }

    public void setReadMail(boolean readMail) {
        isReadMail = readMail;
    }

    @Override
    public String toString() {
        return "MailTagMenuData{" +
                "mTagNo=" + mTagNo +
                ", mImageNo=" + mImageNo +
                ", mName='" + mName + '\'' +
                ", mTotalCount=" + mTotalCount +
                ", mUnreadCount=" + mUnreadCount +
                ", isReadMail=" + isReadMail +
                ", isCheck=" + isCheck +
                '}';
    }
}
