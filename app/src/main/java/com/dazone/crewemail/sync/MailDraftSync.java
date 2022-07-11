package com.dazone.crewemail.sync;

import com.crashlytics.android.Crashlytics;
import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.data.DraftMailData;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.database.DataManager;
import com.dazone.crewemail.database.DraftMailDataSource;
import com.dazone.crewemail.interfaces.BaseHTTPCallBack;
import com.dazone.crewemail.utils.Util;
import com.dazone.crewemail.webservices.HttpRequest;

import java.util.ArrayList;

public class MailDraftSync implements BaseHTTPCallBack {
    static MailDraftSync instance;
    final static String LOCK = "";
    private ArrayList<DraftMailData> syncData;
    private int syncedIndex = -1;

    private MailDraftSync() {
        this.syncData = new ArrayList<>();
    }

    public static MailDraftSync Instance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new MailDraftSync();
            }
            return instance;
        }
    }

    public void sync() {
        if (!Util.isNetworkAvailable() || DaZoneApplication.getInstance().offlineMode)
            return;

        syncData = DataManager.Instance().getDraftMailList();

        if (syncData == null || syncData.size() == 0)
            return;

        syncedIndex = 0;
        resendMail(syncData.get(syncedIndex));
    }


    @Override
    public void onHTTPSuccess() {
        try {
            DataManager.Instance().deleteDraftMail(syncData.get(syncedIndex).getId());
            syncNext();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onHTTPFail(ErrorData errorData) {
    }

    private void syncNext() {
        syncedIndex++;
        if (syncedIndex < syncData.size())
            resendMail(syncData.get(syncedIndex));
    }

    private void resendMail(DraftMailData mailData) {
        switch (mailData.getDraftType()) {
            case DraftMailDataSource.COMPOSE_TYPE_NEW:
            case DraftMailDataSource.COMPOSE_TYPE_FORWARRD:
            case DraftMailDataSource.COMPOSE_TYPE_REPLY:
                HttpRequest.getInstance().ComposeMail(mailData.getData(), this);
                break;

            default:
                break;
        }
    }
}
