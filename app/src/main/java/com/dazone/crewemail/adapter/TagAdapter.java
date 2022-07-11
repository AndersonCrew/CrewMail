package com.dazone.crewemail.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dazone.crewemail.R;
import com.dazone.crewemail.data.MailTagMenuData;
import com.dazone.crewemail.utils.MailHelper;

import java.util.List;

/**
 * Created by THANHTUNG on 14/01/2016.
 */
public class TagAdapter extends ArrayAdapter<MailTagMenuData> {
    private List<MailTagMenuData> listTag;
    private Activity activity;
    private int resoure;
    private TextView txtTag;
    private ImageView imgTag;

    public TagAdapter(Activity activity, int resource, List<MailTagMenuData> listTag) {
        super(activity, resource, listTag);
        this.activity = activity;
        this.resoure = resource;
        this.listTag = listTag;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(resoure, null);
        }
        initControls(convertView);
        initSetControls(position);
        return convertView;
    }

    private void initControls(View convertView) {
        txtTag = convertView.findViewById(R.id.txtTag);
        imgTag = convertView.findViewById(R.id.imeCheckTag);
    }

    private void initSetControls(int position) {
        final MailTagMenuData tagMenuData = listTag.get(position);
        txtTag.setText(tagMenuData.getName());
        imgTag.setImageResource(MailHelper.getColorTag(tagMenuData.getImageNo()));
    }
}
