package com.dazone.crewemail.interfaces;

import com.dazone.crewemail.data.PersonData;

/**
 * Created by Sherry on 12/31/15.
 */
public interface OnOrganizationSelectedEvent {
    void onOrganizationCheck(boolean isCheck,PersonData personData);
//    void onOrganizationCollapseExpand(int isHide,PersonData selectedObject);
}
