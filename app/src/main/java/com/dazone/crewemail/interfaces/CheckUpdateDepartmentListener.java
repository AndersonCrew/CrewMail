package com.dazone.crewemail.interfaces;

import com.dazone.crewemail.data.PersonData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dazone on 7/13/2017.
 */

public interface CheckUpdateDepartmentListener {

    void onUpdated(ArrayList<PersonData> lstDepartment,List<PersonData> list);
    void onFail(String mesage);
}
