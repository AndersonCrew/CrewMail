package com.dazone.crewemail.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Dat on 7/18/2016.
 */
public class BelongToDepartmentDTO implements Parcelable {
    private int BelongNo;
    private int UserNo;
    private int DepartNo;
    private int PositionNo;
    private int DutyNo;
    private boolean IsDefault;
    private String DepartName;
    private int DepartSortNo;
    private String PositionName;
    private int PositionSortNo;
    private String DutyName;
    private int DutySortNo;

    @Override
    public String toString() {
        return "BelongToDepartmentDTO{" +
                "BelongNo=" + BelongNo +
                ", UserNo=" + UserNo +
                ", DepartNo=" + DepartNo +
                ", PositionNo=" + PositionNo +
                ", DutyNo=" + DutyNo +
                ", IsDefault=" + IsDefault +
                ", DepartName='" + DepartName + '\'' +
                ", DepartSortNo=" + DepartSortNo +
                ", PositionName='" + PositionName + '\'' +
                ", PositionSortNo=" + PositionSortNo +
                ", DutyName='" + DutyName + '\'' +
                ", DutySortNo=" + DutySortNo +
                '}';
    }

    public int getBelongNo() {
        return BelongNo;
    }

    public void setBelongNo(int belongNo) {
        BelongNo = belongNo;
    }

    public int getUserNo() {
        return UserNo;
    }

    public void setUserNo(int userNo) {
        UserNo = userNo;
    }

    public int getDepartNo() {
        return DepartNo;
    }

    public void setDepartNo(int departNo) {
        DepartNo = departNo;
    }

    public int getPositionNo() {
        return PositionNo;
    }

    public void setPositionNo(int positionNo) {
        PositionNo = positionNo;
    }

    public int getDutyNo() {
        return DutyNo;
    }

    public void setDutyNo(int dutyNo) {
        DutyNo = dutyNo;
    }

    public boolean isDefault() {
        return IsDefault;
    }

    public void setDefault(boolean aDefault) {
        IsDefault = aDefault;
    }

    public String getDepartName() {
        return DepartName;
    }

    public void setDepartName(String departName) {
        DepartName = departName;
    }

    public int getDepartSortNo() {
        return DepartSortNo;
    }

    public void setDepartSortNo(int departSortNo) {
        DepartSortNo = departSortNo;
    }

    public String getPositionName() {
        return PositionName;
    }

    public void setPositionName(String positionName) {
        PositionName = positionName;
    }

    public int getPositionSortNo() {
        return PositionSortNo;
    }

    public void setPositionSortNo(int positionSortNo) {
        PositionSortNo = positionSortNo;
    }

    public String getDutyName() {
        return DutyName;
    }

    public void setDutyName(String dutyName) {
        DutyName = dutyName;
    }

    public int getDutySortNo() {
        return DutySortNo;
    }

    public void setDutySortNo(int dutySortNo) {
        DutySortNo = dutySortNo;
    }

    public static Creator<BelongToDepartmentDTO> getCREATOR() {
        return CREATOR;
    }

    protected BelongToDepartmentDTO(Parcel in) {
        BelongNo = in.readInt();
        UserNo = in.readInt();
        DepartNo = in.readInt();
        PositionNo = in.readInt();
        DutyNo = in.readInt();
        IsDefault = in.readByte() != 0;
        DepartName = in.readString();
        DepartSortNo = in.readInt();
        PositionName = in.readString();
        PositionSortNo = in.readInt();
        DutyName = in.readString();
        DutySortNo = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(BelongNo);
        dest.writeInt(UserNo);
        dest.writeInt(DepartNo);
        dest.writeInt(PositionNo);
        dest.writeInt(DutyNo);
        dest.writeByte((byte) (IsDefault ? 1 : 0));
        dest.writeString(DepartName);
        dest.writeInt(DepartSortNo);
        dest.writeString(PositionName);
        dest.writeInt(PositionSortNo);
        dest.writeString(DutyName);
        dest.writeInt(DutySortNo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BelongToDepartmentDTO> CREATOR = new Creator<BelongToDepartmentDTO>() {
        @Override
        public BelongToDepartmentDTO createFromParcel(Parcel in) {
            return new BelongToDepartmentDTO(in);
        }

        @Override
        public BelongToDepartmentDTO[] newArray(int size) {
            return new BelongToDepartmentDTO[size];
        }
    };
}
