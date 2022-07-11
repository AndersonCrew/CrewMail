package com.dazone.crewemail.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by THANHTUNG on 18/12/2015.
 */
public class MailBoxData implements Serializable {
    @SerializedName("MailNo")
    private long MailNo;
    @SerializedName("BoxNo")
    private int BoxNo;
    @SerializedName("UserNo")
    private int UserNo = 0;
    @SerializedName("FromAddress")
    private PersonData MailFrom;
    @SerializedName("RegDate")
    private String DateCreate;

    @SerializedName("FromName")
    private String FromName;
    @SerializedName("FromAddr")
    private String FromAddr;
    @SerializedName("languageCode")
    private String LanguageCode;//KO VN EN
    @SerializedName("Title")
    private String Subject;

    @SerializedName("AddressType")
    private int AddressType;

    @SerializedName("Content")
    private String Content;
    private String ContentReply;
    @SerializedName("timeZoneOffset")
    private int TimeZoneOffset;
    @SerializedName("IsImportant")
    private boolean isImportant = false; //true: Important false: Normal
    @SerializedName("Priority")
    private String Priority = "x";//x: None, 1: High, 3: Normal, 5: Low
    @SerializedName("IsReadMail")
    private boolean IsReadMail;
    @SerializedName("TagImageNo")
    private int mImageNo;
    @SerializedName("ToAddressList")
    private ArrayList<PersonData> ListPersonDataTo = new ArrayList<>();
    @SerializedName("CcAddressList")
    private ArrayList<PersonData> ListPersonDataCc = new ArrayList<>();
    @SerializedName("BccAddressList")
    private ArrayList<PersonData> ListPersonDataBcc = new ArrayList<>();
    @SerializedName("files")
    private List<AttachData> ListAttachMent = new ArrayList<>();
    private ArrayList<PersonData> ListPersonData = new ArrayList<>();

    public String getFromName() {
        return FromName;
    }

    public void setFromName(String fromName) {
        FromName = fromName;
    }

    public String getFromAddr() {
        return FromAddr;
    }

    public void setFromAddr(String fromAddr) {
        FromAddr = fromAddr;
    }

    public int getmImageNo() {
        return mImageNo;
    }

    public void setmImageNo(int mImageNo) {
        this.mImageNo = mImageNo;
    }

    public String getDateCreate() {
        return DateCreate;
    }

    public void setDateCreate(String dateCreate) {
        DateCreate = dateCreate;
    }

    public long getMailNo() {
        return MailNo;
    }

    public void setMailNo(long mailNo) {
        MailNo = mailNo;
    }

    public int getBoxNo() {
        return BoxNo;
    }

    public void setBoxNo(int boxNo) {
        BoxNo = boxNo;
    }

    public int getUserNo() {
        return UserNo;
    }

    public void setUserNo(int userNo) {
        UserNo = userNo;
    }

    public PersonData getMailFrom() {
        return MailFrom;
    }

    public void setMailFrom(PersonData mailFrom) {
        MailFrom = mailFrom;
    }

    public String getLanguageCode() {
        return LanguageCode;
    }

    public void setLanguageCode(String languageCode) {
        LanguageCode = languageCode;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public int getTimeZoneOffset() {
        return TimeZoneOffset;
    }

    public void setTimeZoneOffset(int timeZoneOffset) {
        TimeZoneOffset = timeZoneOffset;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }

    public boolean isReadMail() {
        return IsReadMail;
    }

    public void setReadMail(boolean readMail) {
        IsReadMail = readMail;
    }


    public ArrayList<PersonData> getListPersonDataTo() {
        return ListPersonDataTo;
    }

    public void setListPersonDataTo(ArrayList<PersonData> listPersonDataTo) {
        ListPersonDataTo = listPersonDataTo;
    }

    public ArrayList<PersonData> getListPersonDataCc() {
        return ListPersonDataCc;
    }

    public void setListPersonDataCc(ArrayList<PersonData> listPersonDataCc) {
        ListPersonDataCc = listPersonDataCc;
    }

    public ArrayList<PersonData> getListPersonDataBcc() {
        return ListPersonDataBcc;
    }

    public void setListPersonDataBcc(ArrayList<PersonData> listPersonDataBcc) {
        ListPersonDataBcc = listPersonDataBcc;
    }

    public List<AttachData> getListAttachMent() {
        return ListAttachMent;
    }

    public void setListAttachMent(List<AttachData> listAttachMent) {
        List<AttachData> list = new ArrayList<>();
        for(AttachData data : listAttachMent) {
            if(!list.contains(data)) {
                list.add(data);
            }
        }
        ListAttachMent = list;
    }

    public ArrayList<PersonData> getListPersonData() {
        return ListPersonData;
    }

    public void setListPersonData(ArrayList<PersonData> listPersonData) {
        ListPersonData = listPersonData;
    }

    public String getContentReply() {
        return ContentReply;
    }

    public void setContentReply(String contentReply) {
        ContentReply = contentReply;
    }

    public String getPriority() {
        return Priority;
    }

    public void setPriority(String priority) {
        Priority = priority;
    }

    public int getAddressType() {
        return AddressType;
    }

    public void setAddressType(int addressType) {
        AddressType = addressType;
    }

    @Override
    public String toString() {
        return "MailBoxData{" +
                "MailNo=" + MailNo +
                ", BoxNo=" + BoxNo +
                ", UserNo=" + UserNo +
                ", MailFrom=" + MailFrom +
                ", DateCreate='" + DateCreate + '\'' +
                ", LanguageCode='" + LanguageCode + '\'' +
                ", Subject='" + Subject + '\'' +
                ", Content='" + Content + '\'' +
                ", ContentReply='" + ContentReply + '\'' +
                ", TimeZoneOffset=" + TimeZoneOffset +
                ", AddressType=" + AddressType +
                ", isImportant=" + isImportant +
                ", Priority='" + Priority + '\'' +
                ", IsReadMail=" + IsReadMail +
                ", mImageNo=" + mImageNo +
                ", ListPersonDataTo=" + ListPersonDataTo +
                ", ListPersonDataCc=" + ListPersonDataCc +
                ", ListPersonDataBcc=" + ListPersonDataBcc +
                ", ListAttachMent=" + ListAttachMent +
                ", ListPersonData=" + ListPersonData +
                '}';
    }
}
