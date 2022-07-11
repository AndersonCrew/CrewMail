package com.dazone.crewemail.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by dazone on 4/28/2017.
 */

public class Belong implements Serializable{

    @SerializedName("IsDefault")
    public boolean isDefault;

    @SerializedName("DepartName")
    public String departName;

    @SerializedName("PositionName")
    public String positionName;

}
