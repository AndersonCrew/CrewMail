package com.dazone.crewemail.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.dazone.crewemail.R;
import com.dazone.crewemail.data.AccountData;

import java.util.ArrayList;

/**
 * Created by THANHTUNG on 21/12/2015.
 */
public class AdapterMailCreateFromSpiner  extends ArrayAdapter<AccountData> {
    private ArrayList<AccountData> listAcount;
    private Activity activity;
    private int resoure;
    private TextView txtAddressTo;

    public AdapterMailCreateFromSpiner(Activity activity, int resource, ArrayList<AccountData> listAcount) {
        super(activity,resource, listAcount);
        this.activity = activity;
        this.resoure = resource;
        this.listAcount = listAcount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            convertView = activity.getLayoutInflater().inflate(resoure, null);
        }
        initControls(convertView);
        initSetControls(position);
        return convertView;
    }

    private void initSetControls(int position) {
        AccountData accountData = listAcount.get(position);
        String name,email;
        if(!TextUtils.isEmpty(accountData.getName())) {
            name = accountData.getName();
        } else {
            name = "";
        }

        if(!TextUtils.isEmpty(accountData.getMailAddress())) {
            email = accountData.getMailAddress();
        } else {
            email = "";
        }
        String infoFrom = name+" <"+email+">";
        txtAddressTo.setText(infoFrom);
    }

    private void initControls(View convertView) {
        txtAddressTo = convertView.findViewById(R.id.txtMailCreateSpinerAddressTo);
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mail_create_spiner_txt_single_devider, parent, false);
        View mView = v.findViewById(R.id.txtMailCreateSpinerSingleDiver);
        TextView mTextView = (TextView) mView;
        mTextView.setText(getItem(position).getMailAddress());
        return v;
    }


}
