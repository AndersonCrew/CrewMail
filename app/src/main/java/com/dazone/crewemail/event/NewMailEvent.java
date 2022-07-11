package com.dazone.crewemail.event;

/**
 * Created by Dazone on 8/2/2017.
 */

public class NewMailEvent {
   public String mail;

    public NewMailEvent(String mail) {
        this.mail = mail;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
