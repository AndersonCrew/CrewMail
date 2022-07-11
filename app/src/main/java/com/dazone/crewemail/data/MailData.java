package com.dazone.crewemail.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sherry on 12/17/15.
 */
public class MailData implements Parcelable {
    @SerializedName("MailNo")
    private long mMailNo;
    @SerializedName("UserNo")
    private String mUserNo;
    @SerializedName("FromAddr")
    private String mFromEmail;
    @SerializedName("FromName")
    private String mFromName;
    @SerializedName("IsReadMail")
    private boolean mIsRead;
    @SerializedName("IsImportant")
    private boolean mIsImportant;
    @SerializedName("IsFile")
    private boolean mIsAttachFile;
    @SerializedName("Content")
    private String mContent;
    @SerializedName("Title")
    private String mTitle;
    @SerializedName("RegDate")
    private String mRegisterDate;
    @SerializedName("RegDateToString")
    private String mRegisterDateString;
    @SerializedName("TagNo")
    private int mTagNo;
    @SerializedName("TagImageNo")
    private int mImageNo;
    @SerializedName("TotalCount")
    private int mTotalCount;
    @SerializedName("IsSent")
    private boolean isSend;
    @SerializedName("AllAddressList")
    private ArrayList<PersonData> ListPersonDataAll = new ArrayList<>();
    @SerializedName("ReadCount")
    private int mIsReadCount;
    @SerializedName("RecipientsCount")
    private int mRecipientCount;

    @SerializedName("AvatarUrlsOfFrom")
    private String mAvatar;
    @SerializedName("AvatarUrlsOfTo")
    private List<String> avatarTo = new ArrayList<>();
    @SerializedName("BoxNo")
    private int mBoxNo;
    private boolean mIsSelected;
    private boolean mIsDeleted;
    private String mDisplayDeleteString;
    private int mColor;
    private String noneHTMLContent;

    public MailData() {
    }

    public int getmRecipientCount() {
        return mRecipientCount;
    }

    public void setmRecipientCount(int mRecipientCount) {
        this.mRecipientCount = mRecipientCount;
    }

    public int getmIsReadCount() {
        return mIsReadCount;
    }

    public void setmIsReadCount(int mIsReadCount) {
        this.mIsReadCount = mIsReadCount;
    }

    public List<String> getAvatarTo() {
        return avatarTo;
    }

    public void setAvatarTo(List<String> avatarTo) {
        this.avatarTo = avatarTo;
    }

    public int getmImageNo() {
        return mImageNo;
    }

    public void setmImageNo(int mImageNo) {
        this.mImageNo = mImageNo;
    }

    public MailData(String sender) {
        mFromEmail = sender;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    public boolean isImportant() {
        return mIsImportant;
    }

    public void setImportant(boolean important) {
        mIsImportant = important;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public String getFromEmail() {
        return mFromEmail;
    }

    public void setFromEmail(String fromEmail) {
        mFromEmail = fromEmail;
    }

    public boolean isRead() {
        return mIsRead;
    }

    public void setRead(boolean read) {
        mIsRead = read;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public long getMailNo() {
        return mMailNo;
    }

    public void setMailNo(long mailNo) {
        mMailNo = mailNo;
    }

    public String getUserNo() {
        return mUserNo;
    }

    public void setUserNo(String userNo) {
        mUserNo = userNo;
    }

    public String getFromName() {
        return mFromName;
    }

    public void setFromName(String fromName) {
        mFromName = fromName;
    }

    public boolean isAttachFile() {
        return mIsAttachFile;
    }

    public void setAttachFile(boolean attachFile) {
        mIsAttachFile = attachFile;
    }

    public String getTitle() {
        if(mTitle == null)
            return "";
        else
            return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getRegisterDate() {
        return mRegisterDate;
    }

    public void setRegisterDate(String registerDate) {
        mRegisterDate = registerDate;
    }

    public int getTagNo() {
        return mTagNo;
    }

    public void setTagNo(int tagNo) {
        mTagNo = tagNo;
    }

    public int getmTotalMail() {
        return mTotalCount;
    }

    public void setmTotalMail(int mTotalMail) {
        this.mTotalCount = mTotalMail;
    }

    public String getRegisterDateString() {
        return mRegisterDateString;
    }

    public void setRegisterDateString(String registerDateString) {
        mRegisterDateString = registerDateString;
    }

    public boolean isDeleted() {
        return mIsDeleted;
    }

    public void setDeleted(boolean deleted,String displayDeleteString) {
        mIsDeleted = deleted;
        mDisplayDeleteString = displayDeleteString;
    }

    public void setDisplayDeleteString(String displayDeleteString) {
        mDisplayDeleteString = displayDeleteString;
    }

    public String getDisplayDeleteString() {
        return mDisplayDeleteString;
    }

    public String getFirstLetterOfName(){
        if(!TextUtils.isEmpty(mFromName)){
            return mFromName.substring(0,1).toUpperCase();
        }else{
            return "";
        }
    }
    public String getSecondLetterOfName(){
        if(!TextUtils.isEmpty(mFromName)){
            return mFromName.substring(1,2).toUpperCase();
        }else{
            return "";
        }
    }

    public String getFirstLetterOfName(String name){
        if(!TextUtils.isEmpty(name)){
            return name.substring(0,1).toUpperCase();
        }else{
            return "";
        }
    }

    public String getSecondLetterOfName(String name){
        if(!TextUtils.isEmpty(name)){
            return name.substring(1,2).toUpperCase();
        }else{
            return "";
        }
    }

    public String getContentWithoutHtml(){
        if(TextUtils.isEmpty(noneHTMLContent)){
            if(TextUtils.isEmpty(mContent)){
                return "";
            }else {
                noneHTMLContent  = mContent;
//                noneHTMLContent = Html.fromHtml(noneHTMLContent).toString();
                noneHTMLContent = Html.fromHtml(noneHTMLContent).toString().replace('\n', (char) 32)
                        .replace((char) 160, (char) 32).replace((char) 65532, (char) 32).trim();
                if(noneHTMLContent.length() < 200){
                    noneHTMLContent =  noneHTMLContent.substring(0,noneHTMLContent.length());
                }else {
                    noneHTMLContent = noneHTMLContent.substring(0, 200);
                }
                return noneHTMLContent;
//                noneHTMLContent = noneHTMLContent.replaceAll("<(.*?)>", "");//Removes all items in brackets
//                noneHTMLContent = noneHTMLContent.replaceAll("<(.*?)\n", "");//Must be undeneath
//                noneHTMLContent = noneHTMLContent.replaceFirst("(.*?)>", "");//Removes any connected item to the last bracket
//                noneHTMLContent = noneHTMLContent.replaceAll("&nbsp;", " ");
//                noneHTMLContent = noneHTMLContent.replaceAll("&amp;", " ");
//                if(noneHTMLContent.length() < 200){
//                    noneHTMLContent =  noneHTMLContent.substring(0,noneHTMLContent.length());
//                }else {
//                    noneHTMLContent = noneHTMLContent.substring(0, 200);
//                }
//                return noneHTMLContent;
            }
        }else{
            return noneHTMLContent;
        }

    }

    public ArrayList<PersonData> getListPersonDataAll() {
        return ListPersonDataAll;
    }

    public void setListPersonDataAll(ArrayList<PersonData> listPersonDataAll) {
        ListPersonDataAll = listPersonDataAll;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MailData){
            MailData temp = (MailData)o;
            if (this.mMailNo == temp.getMailNo())
                return true;
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mMailNo);
        dest.writeString(this.mUserNo);
        dest.writeString(this.mFromEmail);
        dest.writeString(this.mFromName);
        dest.writeByte(mIsRead ? (byte) 1 : (byte) 0);
        dest.writeByte(mIsImportant ? (byte) 1 : (byte) 0);
        dest.writeByte(mIsAttachFile ? (byte) 1 : (byte) 0);
        dest.writeString(this.mContent);
        dest.writeString(this.mTitle);
        dest.writeString(this.mRegisterDate);
        dest.writeString(this.mRegisterDateString);
        dest.writeInt(this.mTagNo);
        dest.writeString(this.mAvatar);
        dest.writeByte(mIsSelected ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mColor);
    }

    protected MailData(Parcel in) {
        this.mMailNo = in.readLong();
        this.mUserNo = in.readString();
        this.mFromEmail = in.readString();
        this.mFromName = in.readString();
        this.mIsRead = in.readByte() != 0;
        this.mIsImportant = in.readByte() != 0;
        this.mIsAttachFile = in.readByte() != 0;
        this.mContent = in.readString();
        this.mTitle = in.readString();
        this.mRegisterDate = in.readString();
        this.mRegisterDateString = in.readString();
        this.mTagNo = in.readInt();
        this.mAvatar = in.readString();
        this.mIsSelected = in.readByte() != 0;
        this.mColor = in.readInt();
    }

    public static final Parcelable.Creator<MailData> CREATOR = new Parcelable.Creator<MailData>() {
        public MailData createFromParcel(Parcel source) {
            return new MailData(source);
        }

        public MailData[] newArray(int size) {
            return new MailData[size];
        }
    };

    public int getmBoxNo() {
        return mBoxNo;
    }

    public void setmBoxNo(int mBoxNo) {
        this.mBoxNo = mBoxNo;
    }
}
