package com.dazone.crewemail.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by THANHTUNG on 03/02/2016.
 */
public class ReceiveData {
    @SerializedName("IsReserve")
    private Boolean IsReserve;
    @SerializedName("CMSendNum")
    private long CMSendNum;
    @SerializedName("SentLogs")
    private ArrayList<ReadDateData> list = new ArrayList<>();

    public ReceiveData() {
    }

    public Boolean getReserve() {
        return IsReserve;
    }

    public void setReserve(Boolean reserve) {
        IsReserve = reserve;
    }

    public long getCMSendNum() {
        return CMSendNum;
    }

    public void setCMSendNum(long CMSendNum) {
        this.CMSendNum = CMSendNum;
    }

    public ArrayList<ReadDateData> getList() {
        return list;
    }

    public void setList(ArrayList<ReadDateData> list) {
        this.list = list;
    }
}
