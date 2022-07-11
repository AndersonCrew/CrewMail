package com.dazone.crewemail.interfaces;

import com.dazone.crewemail.data.AccountData;
import com.dazone.crewemail.data.ErrorData;

import java.util.ArrayList;

/**
 * Created by THANHTUNG on 31/12/2015.
 */
public interface OnGetListOfMailAccount {
    void OnGetListOfMailAccountSuccess(ArrayList<AccountData> accountData);
    void OnGetListOfMailAccountFail (ErrorData errorData);
}
