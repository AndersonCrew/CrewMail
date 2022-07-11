package com.dazone.crewemail.interfaces;

import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.MailProfileData;

/**
 * Created by Dat on 5/5/2016.
 */
public interface OnGetInfoUser {
    void onGetInfoUserSuccess(MailProfileData commentList);

    void onGetInfoUserFail(ErrorData errorDto);

}
