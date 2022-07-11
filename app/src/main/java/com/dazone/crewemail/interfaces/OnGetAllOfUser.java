package com.dazone.crewemail.interfaces;

import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.PersonData;

import java.util.ArrayList;

/**
 * Created by THANHTUNG on 23/12/2015.
 */
public interface OnGetAllOfUser {
    void onGetAllOfUserSuccess (ArrayList<PersonData> list);
    void onGetAllOfUserFail (ErrorData errorData);
}
