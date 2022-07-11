package com.dazone.crewemail.data;

public class DraftMailData {
    private long id;
    private int draftType;
    private MailBoxData data;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDraftType() {
        return draftType;
    }

    public void setDraftType(int draftType) {
        this.draftType = draftType;
    }

    public MailBoxData getData() {
        return data;
    }

    public void setData(MailBoxData data) {
        this.data = data;
    }

}
