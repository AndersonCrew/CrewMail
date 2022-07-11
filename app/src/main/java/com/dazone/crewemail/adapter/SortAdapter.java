package com.dazone.crewemail.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dazone.crewemail.R;
import com.dazone.crewemail.data.MenuSortData;

import java.util.List;

/**
 * Created by THANHTUNG on 28/01/2016.
 */
public class SortAdapter extends ArrayAdapter<MenuSortData> {
    private List<MenuSortData> listTag;
    private Activity activity;
    private int resoure;
    private TextView txtTag;
    private ImageView imgTag;

    public SortAdapter(Activity activity, int resource, List<MenuSortData> listTag) {
        super(activity,resource, listTag);
        this.activity = activity;
        this.resoure = resource;
        this.listTag = listTag;
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

    private void initControls(View convertView) {
        txtTag = convertView.findViewById(R.id.txtTag);
        imgTag = convertView.findViewById(R.id.imeCheckTag);
    }

    private void initSetControls(int position) {
        final MenuSortData tagMenuData = listTag.get(position);
        txtTag.setText(tagMenuData.getNameSort());
        if(tagMenuData.getIsCheck()==0)
        {
            imgTag.setVisibility(View.INVISIBLE);
        }else
        {
            imgTag.setVisibility(View.VISIBLE);
            if (tagMenuData.getIsCheck() == 1) {
                imgTag.setImageResource(R.drawable.ic_arrow_downward_black_24dp);
            } else {
                imgTag.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
            }
        }
    }
}
