package com.dazone.crewemail.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maidinh on 23-Oct-18.
 */

public class ListContact {
    @SerializedName("listContact")
    @Expose
    private ArrayList<ContactObj> listContact = null;

    public ArrayList<ContactObj> getListContact() {
        return listContact;
    }

    public void setListContact(ArrayList<ContactObj> listContact) {
        this.listContact = listContact;
    }
}
