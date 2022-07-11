package com.dazone.crewemail.data;

import android.text.TextUtils;

import com.dazone.crewemail.R;
import com.dazone.crewemail.utils.EmailBoxStatics;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MailBoxMenuData {

    @SerializedName("BoxNo")
    private int mBoxNo;
    @SerializedName("Name")
    private String mName;
    @SerializedName("ParentNo")
    private int mParentNo;
    @SerializedName("SortNo")
    private int mSortNo;
    @SerializedName("TotalCount")
    private int mTotalCount;
    @SerializedName("UnReadCount")
    private int mUnreadCount;
    @SerializedName("IsShare")
    private boolean mIsShare;
    @SerializedName("IsSent")
    private boolean mIsSent;
    @SerializedName("ClassName")
    private String mClassName;
    @SerializedName("IsReadMail")
    private boolean isReadMail;
    @SerializedName("ChildBoxs")
    private List<MailBoxMenuData> mChildBox = new ArrayList<>();

    public int getBoxNo() {
        return mBoxNo;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getSortNo() {
        return mSortNo;
    }

    public int getUnreadCount() {
        return mUnreadCount;
    }
    public int getmTotalCount() {
        return mTotalCount;
    }

    public List<MailBoxMenuData> getChildBox() {
        return mChildBox;
    }

    public void setChildBox(List<MailBoxMenuData> childBox) {
        mChildBox = childBox;
    }

    public String getClassName() {
        return mClassName;
    }

    public int getClassNameResourceId(){
        int resource;
        switch (mClassName){
            case EmailBoxStatics.MAIL_CLASS_FAV:
                resource = R.drawable.sidebar_ic_05;
                break;
            case EmailBoxStatics.MAIL_CLASS_INBOX:
            case EmailBoxStatics.MAIL_CLASS_DEFAULT:
                resource = R.drawable.sidebar_ic_03;
                break;
            case EmailBoxStatics.MAIL_CLASS_SORT:
                resource = R.drawable.sidebar_ic_04;
                break;
            case EmailBoxStatics.MAIL_CLASS_OUT_BOX:
                resource = R.drawable.sidebar_ic_07;
                break;
            case EmailBoxStatics.MAIL_CLASS_SPAM_BOX:
                resource = R.drawable.sidebar_ic_010;
                break;
            case EmailBoxStatics.MAIL_CLASS_DRAFT_BOX:
                resource = R.drawable.sidebar_ic_09;
                break;
            case EmailBoxStatics.MAIL_CLASS_TRASH_BOX:
                resource = R.drawable.sidebar_ic_011;
                break;
            default:
                resource = R.drawable.sidebar_ic_tag_02;
            break;
        }
        return resource;
    }
}
