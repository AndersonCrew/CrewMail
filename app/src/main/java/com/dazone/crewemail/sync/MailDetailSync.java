package com.dazone.crewemail.sync;

import com.crashlytics.android.Crashlytics;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.MailBoxData;
import com.dazone.crewemail.database.DataManager;
import com.dazone.crewemail.interfaces.OnMailDetailCallBack;
import com.dazone.crewemail.webservices.HttpRequest;
import java.util.ArrayList;

public class MailDetailSync implements OnMailDetailCallBack {
    static MailDetailSync instance;
    final static String LOCK = "";
    private boolean syncing = false;
    private ArrayList<Long> newMailIds;
    private boolean stopping = false;
    public boolean pausing = false;
    private int syncedIndex = -1;

    private MailDetailSync() {
        newMailIds = new ArrayList<>();
    }

    public static MailDetailSync Instance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new MailDetailSync();
            }
            return instance;
        }
    }

    public void sync() {
        newMailIds = getNewMailId();
        if (newMailIds == null || newMailIds.size() == 0)
            return;
        if (!syncing) {
            syncing = true;
            stopping = false;
            syncedIndex = 0;
            HttpRequest.getInstance().getMaillDetail(this, newMailIds.get(syncedIndex));
        }
    }

    @Override
    public void OnMailDetailSuccess(MailBoxData data) {
        if (stopping || data == null)
            return;

        try {
            writeData(data);
            syncNext();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public void OnMaillDetailFail(ErrorData errorData) {
        syncing = false;
        pausing = true;
    }

    private void syncNext() {
        syncedIndex++;
        if (syncedIndex < newMailIds.size())
            HttpRequest.getInstance().getMaillDetail(this, newMailIds.get(syncedIndex));
    }

    private void writeData(MailBoxData data) {
        if (data != null) {
            DataManager.Instance().saveMailDetail(data);
        }
    }

    private ArrayList<Long> getNewMailId() {
        ArrayList<Long> newIds = new ArrayList<>();
        ArrayList<Long> syncedIds = DataManager.Instance().getMailListId();
        ArrayList<Long> detailMailIds = DataManager.Instance().getMailDetailIds();
        for (int i = 0; i < syncedIds.size(); i++) {
            boolean equal = false;
            for (int j = 0; j < detailMailIds.size(); j++) {
                if (syncedIds.get(i).compareTo(detailMailIds.get(j)) == 0) {
                    equal = true;
                    break;
                }
            }
            if (!equal)
                newIds.add(syncedIds.get(i));
        }

        return newIds;
    }
}
