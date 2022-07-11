package com.dazone.crewemail.interfaces;

import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.PersonData;

import java.util.ArrayList;

/**
 * Created by Dazone on 7/6/2017.
 */

public interface IGetListOrganization {
    void onGetListSuccess(ArrayList<PersonData> treeUserDTOs);
    void onGetListFail(ErrorData dto);
}
