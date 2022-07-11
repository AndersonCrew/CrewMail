package com.dazone.crewemail.interfaces;

import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.MailData;

import java.util.List;

/**
 * Created by Sherry on 12/22/15.
 */
public interface  OnMailListCallBack {
    void onMailListSuccess(List<MailData> mailDataList,int totalEmailCount);
    void onMailListFail(ErrorData errorData);
}
