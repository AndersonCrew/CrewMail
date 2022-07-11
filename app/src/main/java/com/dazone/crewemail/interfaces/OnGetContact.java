package com.dazone.crewemail.interfaces;

import com.dazone.crewemail.data.ContactObj;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.ListContact;
import com.dazone.crewemail.data.PersonData;

import java.util.ArrayList;

/**
 * Created by THANHTUNG on 23/12/2015.
 */
public interface OnGetContact {
    void onGetOnGetContactSuccess(ListContact list);
    void onGetOnGetContactFail(ErrorData errorData);
}
