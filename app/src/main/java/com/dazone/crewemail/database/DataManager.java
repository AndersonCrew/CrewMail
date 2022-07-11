package com.dazone.crewemail.database;

import android.content.Context;
import android.util.Log;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.data.DraftMailData;
import com.dazone.crewemail.data.MailBoxData;
import com.dazone.crewemail.data.MailData;

import java.util.ArrayList;

public class DataManager {
    private static DataManager instance;
    private static Context mContext;
    private final static String LOCK = "";

    private DataManager() {
    }

    public static DataManager Instance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new DataManager();
                initDataManager(DaZoneApplication.getInstance());
            }
            return instance;
        }
    }

    private static void initDataManager(Context applicationContext) {
        mContext = applicationContext;
    }

    public ArrayList<MailData> getMailList(int mailBoxNo, String searchText) {
        if (DaZoneApplication.getInstance().getPrefs().getMailBoxNo() == mailBoxNo)
            mailBoxNo++;

        ArrayList<MailData> result;

        MailListDataSource dataSource = new MailListDataSource(mContext);
        dataSource.open();
        result = dataSource.getMailList(mailBoxNo, searchText.trim());
        dataSource.close();
        return result;
    }

    public ArrayList<Long> getMailListId() {
        ArrayList<Long> result = new ArrayList<>();

        MailListDataSource dataSource = new MailListDataSource(mContext);
        dataSource.open();
        result = dataSource.getMailListId();
        dataSource.close();
        return result;
    }

    public void saveMail(MailData mailData) {
        boolean result = false;

        MailListDataSource dataSource = new MailListDataSource(mContext);
        dataSource.open();
        result = dataSource.addMail(mailData);
        dataSource.close();
//        return result;
    }

    public void clearMailList() {
        MailListDataSource dataSource = new MailListDataSource(mContext);
        dataSource.open();
        dataSource.clearData();
        dataSource.close();
    }

    public MailBoxData getMailDetail(long id) {
        MailBoxData result = null;

        MailDataSource dataSource = new MailDataSource(mContext);
        dataSource.open();
        result = dataSource.getMail(id);
        dataSource.close();
        return result;
    }

    public ArrayList<Long> getMailDetailIds() {
        ArrayList<Long> result;

        MailDataSource dataSource = new MailDataSource(mContext);
        dataSource.open();
        result = dataSource.getMailDetailIds();
        dataSource.close();
        return result;
    }

    public void saveMailDetail(MailBoxData mailData) {
        boolean result = false;

        MailDataSource dataSource = new MailDataSource(mContext);
        dataSource.open();
        result = dataSource.addMailDetail(mailData);
        dataSource.close();
    }

    public void clearMailDetail() {
        MailDataSource dataSource = new MailDataSource(mContext);
        dataSource.open();
        dataSource.clearData();
        dataSource.close();
    }

    public ArrayList<DraftMailData> getDraftMailList() {
        ArrayList<DraftMailData> result;

        DraftMailDataSource dataSource = new DraftMailDataSource(mContext);
        dataSource.open();
        result = dataSource.getMails();
        dataSource.close();
        return result;
    }

    public void saveDraftMail(MailBoxData mailData, int draftType) {
        DraftMailDataSource dataSource = new DraftMailDataSource(mContext);
        dataSource.open();
        dataSource.addDraftMail(mailData, draftType);
        dataSource.close();
    }

    public void deleteDraftMail(long mailNo) {
        DraftMailDataSource dataSource = new DraftMailDataSource(mContext);
        dataSource.open();
        dataSource.deleteDraftMail(mailNo);
        dataSource.close();
    }

    public void clearDraftMailList() {
        DraftMailDataSource dataSource = new DraftMailDataSource(mContext);
        dataSource.open();
        dataSource.clearData();
        dataSource.close();
    }

    public void clearData() {
        clearDraftMailList();
        clearMailDetail();
        clearMailList();
    }
}
