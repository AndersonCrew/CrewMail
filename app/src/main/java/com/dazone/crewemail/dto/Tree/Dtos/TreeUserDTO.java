package com.dazone.crewemail.dto.Tree.Dtos;


import android.text.TextUtils;


import com.dazone.crewemail.utils.Statics;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;


public class TreeUserDTO extends DataDto implements DrawImageItem, Serializable {


    @SerializedName("ParentNo")
    private int Parent;
    int margin = 0;
    int level = 0;
    boolean flag = true;

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

    @Override
    public String toString() {
        return "TreeUserDTO{" +
                " \"Parent\":" + Parent +
                ", \"Id\":" + Id +
                ", \"level\":" + level +
                ", \"DBId\":" + DBId +
                ", \"status\":" + status +
                ", \"Type\":" + Type +
                ", \"isHide\":" + isHide +
                ", \"isCheck\":" + isCheck +
                //", \"subordinates\":" + subordinates +
                ", \"Position\":\"" + Position + "\"" +
                ", \"AvatarUrl\":\"" + AvatarUrl + "\"" +
                ", \"PhoneNumber\":\"" + PhoneNumber + "\"" +
                ", \"Name\":\"" + Name + "\"" +
                ", \"NameEN\":\"" + NameEN + "\"" +
                ", \"mSortNo\":" + mSortNo +
                ", \"statusString\":\"" + statusString + "\"" +
                "}";
    }

    private String companyNumber="";

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    @SerializedName("DepartNo")
    private int Id;
    private int DBId;


    // Define status state
    private int status = Statics.USER_LOGOUT;

    // type = 1 category , type =2 : user
    private int Type = 1;
    private int isHide = 0;

    private int PositionSortNo = 0;

    private boolean isCheck = false;

    @SerializedName("ChildDepartments")
    private ArrayList<TreeUserDTO> subordinates;
    @SerializedName("PositionName")
    private String Position = "";
    @SerializedName("AvatarUrl")
    private String AvatarUrl = "";
    @SerializedName("CellPhone")
    private String PhoneNumber = "";
    @SerializedName("Name")
    private String Name = "";
    @SerializedName("Name_EN")
    private String NameEN = "";

    @SerializedName("SortNo") // use for department only (mType = 1)
    private int mSortNo;

    // 즐겨찾기에 등록된 유저 유무
    public boolean mIsFavoriteUser = false;

    public void addChild(TreeUserDTO person) {
        if (this.subordinates == null)
            this.subordinates = new ArrayList<>();
        this.subordinates.add(person);
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusString() {
        return statusString;
    }

    public void setStatusString(String statusString) {
        this.statusString = statusString;
    }

    private String statusString = "";


    public TreeUserDTO(int id, int manager) {
        this.Id = id;
        this.Parent = manager;
    }

    public TreeUserDTO(String name, String nameEN, String phoneNumber, String avatarUrl, String position, int type, int status, int userNo, int departNo) {
        Name = name;
        NameEN = nameEN;
        PhoneNumber = phoneNumber;
        AvatarUrl = avatarUrl;
        Position = position;
        Type = type;
        this.status = status;
        Id = userNo;
        Parent = departNo;
    }

    public TreeUserDTO(String name, String nameEN, String phoneNumber, String avatarUrl, String position, int type, int status, int userNo, int departNo, String userStatusString) {
        Name = name;
        NameEN = nameEN;
        PhoneNumber = phoneNumber;
        AvatarUrl = avatarUrl;
        Position = position;
        Type = type;
        this.status = status;
        Id = userNo;
        Parent = departNo;
        statusString = userStatusString;
    }

    public TreeUserDTO(String name, String nameEN, String phoneNumber, String avatarUrl, String position, int type, int status, int userNo, int departNo, String userStatusString, int positionSortNo) {
        Name = name;
        NameEN = nameEN;
        PhoneNumber = phoneNumber;
        AvatarUrl = avatarUrl;
        Position = position;
        Type = type;
        this.status = status;
        Id = userNo;
        Parent = departNo;
        statusString = userStatusString;
        PositionSortNo = positionSortNo;
    }

    public TreeUserDTO(int id, int manager, String name) {
        this.Id = id;
        this.Parent = manager;
        this.Name = name;
        this.NameEN = name;
    }

    public TreeUserDTO(int DBId, int id, int status, ArrayList<TreeUserDTO> subordinates, String name, String nameEN, int parent, int sortNum) {
        this.DBId = DBId;
        Id = id;
        this.status = status;
        this.subordinates = subordinates;
        NameEN = nameEN;
        Parent = parent;
        this.Name = name;
        this.mSortNo = sortNum;
    }

    public TreeUserDTO(int id, int status, ArrayList<TreeUserDTO> subordinates, String name, String nameEN, int parent, int sortNum) {
        Id = id;
        this.status = status;
        this.subordinates = subordinates;
        NameEN = nameEN;
        Parent = parent;
        this.Name = name;
        this.mSortNo = sortNum;
    }

    public void addSubordinate(TreeUserDTO subordinate) {
        if (subordinates == null) {
            subordinates = new ArrayList<TreeUserDTO>();
        }
        subordinates.add(subordinate);
    }

    public String getAllName() {
        String temp = "";
        temp = getItemName();
        if (subordinates != null && subordinates.size() != 0) {
            for (TreeUserDTO dto : subordinates) {
                if (!TextUtils.isEmpty(dto.getAllName())) {
                    temp += "," + dto.getAllName();
                }
            }
        }
        return temp;
    }

    public String getAllID(String splat) {
        String temp = "";
        temp += getId();
        if (subordinates != null && subordinates.size() != 0) {
            for (TreeUserDTO dto : subordinates) {
                if (!TextUtils.isEmpty("" + dto.getId())) {
                    temp += splat + dto.getAllID(splat);
                }
            }
        }
        return temp;
    }

    public String getItemName() {
        String countryCode = Locale.getDefault().getLanguage();
        if (countryCode.equals("ko")) {
            if (TextUtils.isEmpty(this.getName())) {
                return this.getNameEN();
            } else {
                return this.getName();
            }
        } else {
            return this.getNameEN();
        }

    }

    public int getParent() {
        return Parent;
    }


    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public String getAvatarUrl() {
        return AvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        AvatarUrl = avatarUrl;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNameEN() {
        return NameEN;
    }

    public void setNameEN(String nameEN) {
        NameEN = nameEN;
    }

    public void setParent(int parent) {
        Parent = parent;
    }

    @Override
    public int getId() {
        return Id;
    }

    @Override
    public void setId(int id) {
        Id = id;
    }

    public int getDBId() {
        return DBId;
    }

    public void setDBId(int DBId) {
        this.DBId = DBId;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getIsHide() {
        return isHide;
    }

    public void setIsHide(int isHide) {
        this.isHide = isHide;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public ArrayList<TreeUserDTO> getSubordinates() {
        return subordinates;
    }

    public void setSubordinates(ArrayList<TreeUserDTO> subordinates) {
        this.subordinates = subordinates;
    }

    @Override
    public String getImageLink() {
        return getAvatarUrl();
    }

    @Override
    public String getImageTitle() {
        return getItemName();
    }


    public int getmSortNo() {
        return mSortNo;
    }

    public void setmSortNo(int mSortNo) {
        this.mSortNo = mSortNo;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public int getPositionSortNo() {
        return PositionSortNo;
    }

    public void setPositionSortNo(int positionSortNo) {
        PositionSortNo = positionSortNo;
    }
}
