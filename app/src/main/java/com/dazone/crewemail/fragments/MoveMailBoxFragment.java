package com.dazone.crewemail.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.dazone.crewemail.R;
import com.dazone.crewemail.customviews.MailMenuView;
import com.dazone.crewemail.data.MailBoxMenuData;
import com.dazone.crewemail.data.MailTagMenuData;
import com.dazone.crewemail.utils.EmailBoxStatics;
import com.dazone.crewemail.utils.StaticsBundle;

/**
 * Created by Sherry on 1/6/16.
 */
public class MoveMailBoxFragment extends BaseFragment {

    private View mView;
    private MailMenuView mMailMenuView;
    private Object mSelectedObject;
    private int task = 1;

    public static MoveMailBoxFragment newInstance(int task) {
        Bundle args = new Bundle();
        MoveMailBoxFragment fragment = new MoveMailBoxFragment();
        args.putInt(StaticsBundle.BUNDLE_TASK, task);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null)
            task = getArguments().getInt(StaticsBundle.BUNDLE_TASK);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_move_mail_box,container,false);
        initMailBoxMenu();
        setHasOptionsMenu(true);
        return  mView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.check_menu, menu);
    }

    private void initMailBoxMenu(){
        mMailMenuView = new MailMenuView(getActivity(),
                mView.findViewById(R.id.move_mail_box_wrapper),null);
        mMailMenuView.init(false,task);
        mMailMenuView.setOnMenuItemClickListener(object -> mSelectedObject = object);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_check:
                Intent resultIntent = new Intent();
                if (mSelectedObject instanceof MailBoxMenuData) {
                    resultIntent.putExtra(StaticsBundle.BUNDLE_MAIL_BOX_TYPE, EmailBoxStatics.NORMAL_MAIL_BOX);
                    resultIntent.putExtra(StaticsBundle.BUNDLE_MAIL_BOX_NO,((MailBoxMenuData)mSelectedObject).getBoxNo());
                }else if (mSelectedObject instanceof MailTagMenuData) {
                    resultIntent.putExtra(StaticsBundle.BUNDLE_MAIL_BOX_TYPE, EmailBoxStatics.TAG_MAIL_BOX);
                    resultIntent.putExtra(StaticsBundle.BUNDLE_MAIL_BOX_NO, ((MailTagMenuData)mSelectedObject).getTagNo());
                }
                getActivity().setResult(Activity.RESULT_OK, resultIntent);
                getActivity().finish();
                break;
        }
        return true;
    }

}
