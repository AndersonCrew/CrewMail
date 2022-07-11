package com.dazone.crewemail.data;

import com.dazone.crewemail.interfaces.OnGetListOfMailAccount;
import com.dazone.crewemail.webservices.HttpRequest;
import com.google.gson.annotations.SerializedName;

/**
 * Created by THANHTUNG on 31/12/2015.
 */
public class AccountData {
    @SerializedName("AccountNo")
    private int AccountNo;
    @SerializedName("UserNo")
    private int UserNo;
    @SerializedName("Server")
    private String Server;
    @SerializedName("PopUser")
    private String PopUser;
    @SerializedName("Name")
    private String Name;
    @SerializedName("MailAddress")
    private String MailAddress;

    public AccountData() {
    }

    public int getAccountNo() {
        return AccountNo;
    }

    public void setAccountNo(int accountNo) {
        AccountNo = accountNo;
    }

    public int getUserNo() {
        return UserNo;
    }

    public void setUserNo(int userNo) {
        UserNo = userNo;
    }

    public String getServer() {
        return Server;
    }

    public void setServer(String server) {
        Server = server;
    }

    public String getPopUser() {
        return PopUser;
    }

    public void setPopUser(String popUser) {
        PopUser = popUser;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMailAddress() {
        return MailAddress;
    }

    public void setMailAddress(String mailAddress) {
        MailAddress = mailAddress;
    }

    public static void getAllAccount(OnGetListOfMailAccount callback){
        HttpRequest.getInstance().getListOfMailAccountsForServer(callback);
    }
}
