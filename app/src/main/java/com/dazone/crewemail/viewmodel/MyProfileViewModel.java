package com.dazone.crewemail.viewmodel;

import android.content.Context;
import androidx.databinding.BaseObservable;

import com.dazone.crewemail.data.MailProfileData;

/**
 * Created by dazone on 4/28/2017.
 */

public class MyProfileViewModel extends BaseObservable {
    private MailProfileData data;
    private Context context;

    public MyProfileViewModel() {
    }

    public MyProfileViewModel(MailProfileData data, Context context) {

        this.data = data;
        this.context = context;
    }

    public String getPositionDepartName() {
        for (int i = 0; i < data.belongs.size(); i++) {
            if (data.getBelongs().get(i).isDefault) {
                return data.positionDepartName = data.getBelongs().get(i).departName + "/" + data.getBelongs().get(i).positionName;
            }
        }
        return data.positionDepartName;
    }

    public String getPassWord() {
        return data.password;
    }

}
