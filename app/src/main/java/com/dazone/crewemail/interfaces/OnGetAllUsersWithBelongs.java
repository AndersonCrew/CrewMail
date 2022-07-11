package com.dazone.crewemail.interfaces;

import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.PersonData;

import java.util.ArrayList;

/**
 * Created by Dat on 7/18/2016.
 */
public interface OnGetAllUsersWithBelongs {
    void OnGetAllUsersWithBelongsSuccess(ArrayList<PersonData> personDatas);

    void OnGetAllUsersWithBelongsFail(ErrorData errorData);

}
