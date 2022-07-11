package com.dazone.crewemail.interfaces;

import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.MailProfileData;

/**
 * Created by THANHTUNG on 26/01/2016.
 */
public interface OnGetListFriend {
    void onGetListFriendSuccess(MailProfileData commentList);
    void onGetListFriendFail(ErrorData errorDto);
}
