package com.dazone.crewemail.data;

import android.text.TextUtils;

import com.dazone.crewemail.dto.ob_belongs;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by THANHTUNG on 17/12/2015.
 */
public class PersonData implements Serializable {
    public static String TAG = "PersonData";
    @SerializedName("Name")
    private String FullName;
    @SerializedName("Name_EN")
    private String mNameEn;
    @SerializedName("Name_Default")
    private String mNameDefault;
    @SerializedName("Mail")
    private String Email;
    @SerializedName("MailAddress")
    private String mEmail;
    @SerializedName("UserNo")
    private int UserNo = 0;
    @SerializedName("UserID")
    private String UserID;
    @SerializedName("AvatarUrl")
    private String UrlAvatar;
    @SerializedName("addrType")
    private String addrType = ""; //mail: email user input, user: name member from oran, depart: Type Address depart
    private int TypeAddress = 0; //0: to, 1: cc, 2: Type Address Bcc
    private int TypeColor = 2;//0: unknown 1: Known from listUser 2: Known from Oran
    @SerializedName("Belongs")
    private ArrayList<ob_belongs> belongsArrayList;
    @SerializedName("DepartNo")
    private int DepartNo = 0;
    @SerializedName("DepartName")
    private String DepartName;
    @SerializedName("PositionNo")
    private int PositionNo;
    @SerializedName("PositionSortNo")
    private int PositionSortNo;
    @SerializedName("PositionName")
    private String PositionName;
    @SerializedName("DutyNo")
    private int DutyNo; @SerializedName("AddressType")
    private int AddressType;
    @SerializedName("DutyName")
    private String DutyName;
    @SerializedName("isLow")
    private boolean isLow;
    @SerializedName("Photo")
    private String mPhoto;
    private int margin = 0;
    private int level = 0;
    private boolean flag = true;


    public boolean isChosen = false;

    private ArrayList<PersonData> listMembers;

    public ArrayList<PersonData> getListMembers() {
        if(listMembers == null) {
            listMembers = new ArrayList<>();
        }
        return listMembers;
    }

    public void setListMembers(ArrayList<PersonData> listMembers) {
        this.listMembers = listMembers;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public int getPositionSortNo() {
        return PositionSortNo;
    }


    public ArrayList<ob_belongs> getBelongsArrayList() {
        return belongsArrayList;
    }

    @Override
    public String toString() {
        return "PersonData{" +
                "FullName='" + FullName + '\'' +
                ", mNameEn='" + mNameEn + '\'' +
                ", mNameDefault='" + mNameDefault + '\'' +
                ", Email='" + Email + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", UserNo=" + UserNo +
                ", UserID='" + UserID + '\'' +
                ", UrlAvatar='" + UrlAvatar + '\'' +
                ", addrType='" + addrType + '\'' +
                ", TypeAddress=" + TypeAddress +
                ", TypeColor=" + TypeColor +
                ", belongsArrayList=" + belongsArrayList +
                ", DepartNo=" + DepartNo +
                ", DepartName='" + DepartName + '\'' +
                ", PositionNo=" + PositionNo +
                ", PositionName='" + PositionName + '\'' +
                ", DutyNo=" + DutyNo +
                ", DutyName='" + DutyName + '\'' +
                ", isLow=" + isLow +
                ", mPhoto='" + mPhoto + '\'' +
                ", mType=" + mType +
                ", mIsEnabled=" + mIsEnabled +
                ", mSortNo=" + mSortNo +
                ", mDepartmentParentNo=" + mDepartmentParentNo +
                ", mPersonList=" + mPersonList +
                ", isCheck=" + isCheck +
                '}';
    }

    private int mType; // 0 : user not in organization || user in organization  1 : department , 2 : user
    @SerializedName("Enabled")
    private boolean mIsEnabled = true;

    @SerializedName("SortNo") // use for department only (mType = 1)
    private int mSortNo;
    @SerializedName("ParentNo") // use for department only (mType = 1)
    private int mDepartmentParentNo;

    @SerializedName("ChildDepartments")
    private ArrayList<PersonData> mPersonList;

    private boolean isCheck = false;

    public PersonData(String name, String email, String urlAvatar) {
        Email = email;
        FullName = name;
        UrlAvatar = urlAvatar;
    }

    public PersonData(String name, String email) {
        Email = email;
        FullName = name;
    }

    public PersonData() {
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getEmail() {
        if (TextUtils.isEmpty(Email))
            return "";
        else
            return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getmEmail() {
        if (TextUtils.isEmpty(mEmail))
            return "";
        else
            return mEmail;
    }

    public void setmEmail(String email) {
        mEmail = email;
    }

    public String getUrlAvatar() {
        return UrlAvatar;
    }

    public void setUrlAvatar(String urlAvatar) {
        UrlAvatar = urlAvatar;
    }

    public void setTypeAddress(int typeAddress) {
        TypeAddress = typeAddress;
    }

    public int getTypeColor() {
        return TypeColor;
    }

    public void setTypeColor(int typeColor) {
        TypeColor = typeColor;
    }

    public int getType() {
        if (belongsArrayList == null) {
            if (DepartNo != 0) {
                if (UserNo == 0) // category
                    return 1;
                else
                    return 2; // user
            } else {
                return 0; // not in organization
            }
        } else {
            int DepartNo = 0;
            for (int i = 0; i < belongsArrayList.size(); i++) {
                ob_belongs ob = belongsArrayList.get(i);
                DepartNo = ob.getDepartNo();
            }
            if (DepartNo != 0) {
                if (UserNo == 0) // category
                    return 1;
                else
                    return 2; // user
            } else {
                return 0; // not in organization
            }
        }
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    public String getNameEn() {
        return mNameEn;
    }

    public void setNameEn(String nameEn) {
        mNameEn = nameEn;
    }

    public String getNameDefault() {
        return mNameDefault;
    }

    public void setNameDefault(String nameDefault) {
        mNameDefault = nameDefault;
    }

    public boolean isEnabled() {
        return mIsEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        mIsEnabled = isEnabled;
    }

    public ArrayList<PersonData> getPersonList() {
        return mPersonList;
    }

    public void setPersonList(ArrayList<PersonData> personList) {
        mPersonList = personList;
    }

    public int getSortNo() {
        return mSortNo;
    }

    public void setSortNo(int sortNo) {
        mSortNo = sortNo;
    }

    public int getUserNo() {
        return UserNo;
    }

    public void setUserNo(int userNo) {
        UserNo = userNo;
    }

    public String getUserID() {
        return UserID;
    }

    public int getRealDepartNo() {
        return DepartNo;
    }
    public int getDepartNo() {
        if (belongsArrayList == null) {
            return DepartNo;
        } else {
            int DepartNo = 0;
            for (int i = 0; i < belongsArrayList.size(); i++) {
                ob_belongs ob = belongsArrayList.get(i);
                DepartNo = ob.getDepartNo();
            }
            return DepartNo;
        }
    }

    public void setDepartNo(int departNo) {
        if (belongsArrayList == null) {
            DepartNo = departNo;
        } else {
            for (int i = 0; i < belongsArrayList.size(); i++) {
                ob_belongs ob = belongsArrayList.get(i);
                ob.setDepartNo(departNo);
            }
        }
    }

    public String getDepartName() {
        return DepartName;
    }

    public String getPositionName() {
        if (belongsArrayList == null) {
            return PositionName;
        } else {
            String PositionName = "";
            for (int i = 0; i < belongsArrayList.size(); i++) {
                ob_belongs ob = belongsArrayList.get(i);
                PositionName = ob.getPositionName();
            }
            return PositionName;
        }
    }

    public void setPositionName(String positionName) {
        if (belongsArrayList == null) {
            PositionName = positionName;
        } else {
            for (int i = 0; i < belongsArrayList.size(); i++) {
                ob_belongs ob = belongsArrayList.get(i);
                ob.setPositionName(positionName);
            }
        }
    }

    public boolean isLow() {
        return isLow;
    }


    /**
     * mail: email user input, user: name member from oran, depart: Type Address depart
     *
     * @return
     */
    public String getAddrType() {
        if (TextUtils.isEmpty(addrType)) {
            if (this.getType() == 1) {
                return "depart";
            } else if (this.getType() == 2) {
                return "user";
            } else {
                return "mail";
            }
        } else {

            return addrType;
        }
    }

    public int getDepartmentParentNo() {
        return mDepartmentParentNo;
    }

    public void setDepartmentParentNo(int departmentParentNo) {
        mDepartmentParentNo = departmentParentNo;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public void addChild(PersonData person) {
        if (this.mPersonList == null)
            this.mPersonList = new ArrayList<>();
        this.mPersonList.add(person);
    }

    public void addChildren(ArrayList<PersonData> persons) {
        if (this.mPersonList == null)
            this.mPersonList = new ArrayList<>();
        this.mPersonList.addAll(persons);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PersonData) {
            PersonData temp = (PersonData) o;
            if (this.getType() == 2 && temp.getType() == 2) { // user
                return (this.UserNo == temp.UserNo);

            } else if (this.getType() == 1 && temp.getType() == 1) {
                return (this.DepartNo == temp.DepartNo);
            } else {
                if (this.Email != null) {
                    return (this.Email.equals(temp.mEmail) || this.Email.equals(temp.Email));
                }
            }
        }
        return false;
    }


}
