package com.dazone.crewemail.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.dazone.crewemail.R;
import com.dazone.crewemail.adapter.ListReadDateAdapter;
import com.dazone.crewemail.customviews.AlertDialogView;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.ReadDateData;
import com.dazone.crewemail.data.ReceiveData;
import com.dazone.crewemail.interfaces.BaseHTTPCallBack;
import com.dazone.crewemail.interfaces.OnGetReadDate;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.StaticsBundle;
import com.dazone.crewemail.utils.TimeUtils;
import com.dazone.crewemail.utils.Util;
import com.dazone.crewemail.webservices.HttpRequest;

import java.util.ArrayList;
import java.util.Locale;

public class ReadDateFragment extends BaseFragment {

    private View mView;
    private String time, title;
    private long mailNo;
    private TextView txtTitle, txtTime;
    private RecyclerView recyclerView;
    private ListReadDateAdapter mAdapter;
    private ReceiveData receiveData;
    private ArrayList<ReadDateData> mSelectedMailList;

    public static ReadDateFragment newInstance(long mailNo, String time, String title) {
        Bundle args = new Bundle();
        args.putLong(StaticsBundle.BUNDLE_MAIL_NO_NEW, mailNo);
        args.putString(StaticsBundle.BUNDLE_TIME, time);
        args.putString(StaticsBundle.BUNDLE_TITLE, title);
        ReadDateFragment fragment = new ReadDateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectedMailList = new ArrayList<>();
        receiveData = new ReceiveData();
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mailNo = getArguments().getLong(StaticsBundle.BUNDLE_MAIL_NO_NEW);
            time = getArguments().getString(StaticsBundle.BUNDLE_TIME);
            title = getArguments().getString(StaticsBundle.BUNDLE_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.recycle_read_date, container, false);
        txtTitle = mView.findViewById(R.id.txtTitle);
        txtTime = mView.findViewById(R.id.txtDate);
        txtTitle.setText(title);
        if (!TextUtils.isEmpty(time)) {
            if (Locale.getDefault().getLanguage().toUpperCase().equalsIgnoreCase("KO"))
                txtTime.setText(TimeUtils.displayTimeWithoutOffset(getActivity(), time, 1));
            else
                txtTime.setText(TimeUtils.displayTimeWithoutOffset(getActivity(), time, 0));
        }

        initRecyclerView();
        return mView;
    }

    //isTask = 0 if load data default
    //=1 if refesh data
    private void getListEmailFromServer() {
        mAdapter.setProgressOn(true);
        HttpRequest.getInstance().getReceivesForMail(mailNo, new OnGetReadDate() {
            @Override
            public void onGetReadDateSuccess(ReceiveData receiveDataNew) {
                if (isVisible()) {
                    mAdapter.setProgressOn(false);
                    receiveData = receiveDataNew;
                    mAdapter = new ListReadDateAdapter(ReadDateFragment.this, receiveData.getList());
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.setOnMailItemSelectListener(mailData -> {
                        if(mailData != null) {
                            if (mSelectedMailList.contains(mailData)) {
                                mSelectedMailList.remove(mailData);
                            } else {
                                mSelectedMailList.add(mailData);
                            }
                        }
                    });
                }
            }

            @Override
            public void onGetReadDateFail(ErrorData errorData) {
                if (isVisible()) {
                    mAdapter.setProgressOn(false);
                }
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = mView.findViewById(R.id.common_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new ListReadDateAdapter(this, receiveData.getList());
        recyclerView.setAdapter(mAdapter);
        // remove default animation
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        // load data from server
        if (mailNo != 0) {
            getListEmailFromServer();
        }
    }

    public void cancelSelect() {
        for (int i = 0; i < receiveData.getList().size(); i++) {
            checkUnCheckItemWithAnimation(i, false);
            receiveData.getList().get(i).setSelected(false);
        }
        mSelectedMailList.clear();
        if (mAdapter != null) {
            mAdapter.ClearListSelect();
        }
    }

    public void checkUnCheckItemWithAnimation(int index, boolean isSelected) {
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(index);
        if (holder != null && holder instanceof ListReadDateAdapter.ItemViewHolder)
            mAdapter.showHideCheckAnimation((ListReadDateAdapter.ItemViewHolder) holder, isSelected);
    }

    public boolean checkSelect() {
        return mSelectedMailList.size() > 0;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.meu_read_date, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (checkSelect()) {
                    cancelSelect();
                } else {
                    getActivity().finish();
                }
                break;
            case R.id.menu_list_cancel:
                if (mSelectedMailList != null && mSelectedMailList.size() > 0) {
                    if (receiveData.getReserve()) {
                        HttpRequest.getInstance().CancelReservationEntrie(mailNo, receiveData.getCMSendNum(), new BaseHTTPCallBack() {
                            @Override
                            public void onHTTPSuccess() {

                            }

                            @Override
                            public void onHTTPFail(ErrorData errorDto) {
                                Util.showMessage(Util.getString(R.string.string_update_fail));
                            }
                        });

                        HttpRequest.getInstance().CancelReservation(mailNo, mSelectedMailList, new BaseHTTPCallBack() {
                            @Override
                            public void onHTTPSuccess() {
                                Util.showMessage(Util.getString(R.string.string_success));
                                if (isVisible()) {
                                    mSelectedMailList.clear();
                                    getListEmailFromServer();
                                    new Prefs().putBooleanValue(StaticsBundle.PREFS_KEY_RELOAD_LIST, true);
                                }
                            }

                            @Override
                            public void onHTTPFail(ErrorData errorDto) {
                                Util.showMessage(Util.getString(R.string.string_update_fail));
                            }
                        });
                    } else
                        HttpRequest.getInstance().CancelSent(mailNo, mSelectedMailList, new BaseHTTPCallBack() {
                            @Override
                            public void onHTTPSuccess() {

                                Util.showMessage(Util.getString(R.string.string_success));
                                if (isVisible()) {
                                    mSelectedMailList.clear();
                                    getListEmailFromServer();
                                    new Prefs().putBooleanValue(StaticsBundle.PREFS_KEY_RELOAD_LIST, true);
                                }
                            }

                            @Override
                            public void onHTTPFail(ErrorData errorDto) {
                                Util.showMessage(Util.getString(R.string.string_update_fail));
                            }
                        });
                } else {
                    Util.showMessage(Util.getString(R.string.string_notice_sent_error_select));
                }
                break;
            case R.id.menu_list_cancel_all:
                if (getAllList().size() > 0) {
                    AlertDialogView.normalAlertDialogWithCancel(getActivity(), getString(R.string.app_name), getString(R.string.string_cancel_sent_for_all), getString(R.string.yes), getString(R.string.no), new AlertDialogView.OnAlertDialogViewClickEvent() {
                        @Override
                        public void onOkClick(DialogInterface alertDialog) {

                            if (receiveData.getReserve()) {
                                HttpRequest.getInstance().CancelReservationEntrie(mailNo, receiveData.getCMSendNum(), new BaseHTTPCallBack() {
                                    @Override
                                    public void onHTTPSuccess() {
                                    }

                                    @Override
                                    public void onHTTPFail(ErrorData errorDto) {
                                        Util.showMessage(Util.getString(R.string.string_update_fail));
                                    }
                                });

                                HttpRequest.getInstance().CancelReservation(mailNo, mSelectedMailList, new BaseHTTPCallBack() {
                                    @Override
                                    public void onHTTPSuccess() {
                                        Util.showMessage(Util.getString(R.string.string_success));
                                        if (isVisible()) {
                                            mSelectedMailList.clear();
                                            getListEmailFromServer();
                                            new Prefs().putBooleanValue(StaticsBundle.PREFS_KEY_RELOAD_LIST, true);
                                        }
                                    }

                                    @Override
                                    public void onHTTPFail(ErrorData errorDto) {
                                        Util.showMessage(Util.getString(R.string.string_update_fail));
                                    }
                                });
                            } else {
                                HttpRequest.getInstance().CancelSent(mailNo, getAllList(), new BaseHTTPCallBack() {
                                    @Override
                                    public void onHTTPSuccess() {
                                        Util.showMessage(Util.getString(R.string.string_success));
                                        if (isVisible()) {
                                            mSelectedMailList.clear();
                                            getListEmailFromServer();
                                            new Prefs().putBooleanValue(StaticsBundle.PREFS_KEY_RELOAD_LIST, true);
                                        }
                                    }

                                    @Override
                                    public void onHTTPFail(ErrorData errorDto) {
                                        Util.showMessage(Util.getString(R.string.string_update_fail));
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelClick() {

                        }
                    });
                } else {
                    Util.showMessage(getString(R.string.string_cancel_sent));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public ArrayList<ReadDateData> getAllList() {
        ArrayList<ReadDateData> listTemp = new ArrayList<>();
        for (ReadDateData readDateData : receiveData.getList()) {
            if (readDateData.getCanSentCancel()) {
                listTemp.add(readDateData);
            }
        }
        return listTemp;
    }
}
