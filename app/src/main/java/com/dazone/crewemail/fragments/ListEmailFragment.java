package com.dazone.crewemail.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dazone.crewemail.BuildConfig;
import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.activities.ListEmailActivity;
import com.dazone.crewemail.activities.MoveMailBoxActivity;
import com.dazone.crewemail.activities.ToolBarActivity;
import com.dazone.crewemail.adapter.ListEmailRecyclerAdapter;
import com.dazone.crewemail.adapter.SortAdapter;
import com.dazone.crewemail.customviews.EndlessRecyclerOnScrollListener;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.MailData;
import com.dazone.crewemail.data.MailTagMenuData;
import com.dazone.crewemail.data.MenuSortData;
import com.dazone.crewemail.data.PersonData;
import com.dazone.crewemail.database.GetMailListTask;
import com.dazone.crewemail.event.ReloadListMail;
import com.dazone.crewemail.event.reloadTitle;
import com.dazone.crewemail.interfaces.BaseHTTPCallBack;
import com.dazone.crewemail.interfaces.OnGetLocalDataCallback;
import com.dazone.crewemail.interfaces.OnMailListCallBack;
import com.dazone.crewemail.interfaces.OnMenuListCallBack;
import com.dazone.crewemail.utils.EmailBoxStatics;
import com.dazone.crewemail.utils.MailHelper;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.StaticsBundle;
import com.dazone.crewemail.utils.TimeUtils;
import com.dazone.crewemail.utils.Util;
import com.dazone.crewemail.webservices.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;


public class ListEmailFragment extends BaseFragment implements BaseHTTPCallBack, OnGetLocalDataCallback {
    private ListEmailFragment fragment;
    private View mView;
    private ListEmailRecyclerAdapter mAdapter;
    private ArrayList<MailData> mMailDataList;
    private ArrayList<MailData> mSelectedMailList;
    private ArrayList<MenuSortData> listSort;
    private boolean mIsEditModeActivated;
    private RecyclerView recyclerView;
    private boolean mAllowLoadOld;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView txtNoData;

    // for searching
    private int mSearchType;
    private String mSearchQuery;
    private int mMailBoxNo;
    private int mMailType; // 0 normal mail box , 1 : tag mail box
    private String mMailBoxClassName;
    private Menu mMenu;
    private boolean mIsTrashBox;
    private EndlessRecyclerOnScrollListener mEndlessRecyclerOnScrollListener;
    MenuItem mListCheckAll;
    private boolean isCheck = true;
    private long quickSearch = 1;
    private int sortColum = 4;
    private boolean isAscend = false;
    private SearchView searchView;
    private PopupWindow popupWindow;
    private Menu menuTemp;
    private ArrayList<PersonData> personDatasRecycle = new ArrayList<>();
    private ArrayList<PersonData> mSelectedPersonList = new ArrayList<>();
    private ArrayList<PersonData> mPersonList = new ArrayList<>();
    private List<PersonData> personDatas = new ArrayList<>();
    private Handler mHandler = new Handler();

    /**
     * @param mailBoxNo         mail box id
     * @param mailBoxName       mail box name
     * @param emailBoxClassName class name {@link EmailBoxStatics}
     * @param mailType          0 normal mail box , 1 : tag mail box
     * @return Fragment
     */
    public static ListEmailFragment newInstance(int mailBoxNo, String mailBoxName, String emailBoxClassName, int mailType) {
        Bundle args = new Bundle();
        args.putInt(StaticsBundle.BUNDLE_MAIL_FROM_NOTIFICATION_MAILBOX_NO, mailBoxNo);
        args.putInt("mailType", mailType);
        args.putString("mailBoxName", mailBoxName);
        args.putBoolean("isTrashBox", (EmailBoxStatics.MAIL_CLASS_TRASH_BOX.equals(emailBoxClassName)));
        args.putString("emailBoxClassName", TextUtils.isEmpty(emailBoxClassName) ? "" : emailBoxClassName);
        ListEmailFragment fragment = new ListEmailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragment = this;
        mMailDataList = new ArrayList<>();
        mSelectedMailList = new ArrayList<>();
        mIsEditModeActivated = false;
        setHasOptionsMenu(true);
        mAllowLoadOld = true;
        mSearchType = 0;
        mSearchQuery = "";
        mMailBoxNo = getArguments().getInt(StaticsBundle.BUNDLE_MAIL_FROM_NOTIFICATION_MAILBOX_NO);
        mMailType = getArguments().getInt("mailType");
        mMailBoxClassName = getArguments().getString("emailBoxClassName");
        mIsTrashBox = getArguments().getBoolean("isTrashBox");
        genDataSort(mMailBoxClassName);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.common_recycler_view, container, false);
        initRecyclerView(true);

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Util.isNetworkAvailable() && !DaZoneApplication.getInstance().offlineMode) {
            ///TODO Uncomment when build
            new Handler().postDelayed(() -> {
                //MailListSync.Instance().sync();
            }, 3000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void getListEmailFromServer(long anchorMailNo) {
        if (mMailBoxNo == 0) {
            return;
        }

        try {

            mAdapter.setProgressOn(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpRequest.getInstance().getEmailList(mMailBoxNo, anchorMailNo, Statics.DEFAULT_GET_NEW_DISPLAY_LIST_COUNT, true, mMailType, mSearchType, mSearchQuery, quickSearch, sortColum, isAscend, new OnMailListCallBack() {
            @Override
            public void onMailListSuccess(List<MailData> mailDataList, int totalEmailCount) {
                if (isVisible()) {
                    int lastPosition = 0;
                    mAdapter.setProgressOn(false);
                    try {
                        if (mMailDataList.size() > 0) {
                            lastPosition = mMailDataList.size();
                        } else {
                            lastPosition = 0;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mMailDataList.addAll(mailDataList);
                    if (sortColum == 4) {
                        List<MailData> listTemp = TimeUtils.CheckDateTime(mMailDataList);
                        mMailDataList.clear();
                        mMailDataList.addAll(listTemp);
                        Prefs prefs = DaZoneApplication.getInstance().getPrefs();
                        String dataStr = new Gson().toJson(mMailDataList);
                        prefs.putListData(dataStr);
                    }

                    if (mMailDataList.size() == 0) {
                        txtNoData.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        txtNoData.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    if (lastPosition == 0)
                        mAdapter.notifyDataSetChanged();
                    else {
                        mAdapter.notifyItemRangeInserted(lastPosition, mailDataList.size());
                    }
                    if (mMailDataList.size() >= totalEmailCount) {
                        mAllowLoadOld = false;
                    }
                    if (!isCheck) {
                        if (!mIsEditModeActivated) {
                            changeToolBarStatusToEditMode(true);
                        }
                        selectAllMailInList();
                    }

                    if(isShowToolBar) {
                        setToolBarTitle();
                    }
                }
            }

            @Override
            public void onMailListFail(ErrorData errorData) {
                if (isVisible()) {
                    mAdapter.setProgressOn(false);
                }
            }
        });
    }

    private void initRecyclerView(boolean check) {
        txtNoData = mView.findViewById(R.id.txt_no_data);
        recyclerView = mView.findViewById(R.id.common_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLinearLayoutManager);

        mAdapter = new ListEmailRecyclerAdapter(this, mMailDataList, mMailBoxClassName);
        recyclerView.setAdapter(mAdapter);
        // remove default animation
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        // listener when check/un-check item
        mAdapter.setOnMailItemSelectListener(mailData -> {
            if (!mIsEditModeActivated)
                changeToolBarStatusToEditMode(true);
            if(mailData != null) {
                if (mSelectedMailList.contains(mailData)) {
                    // remove if already add selected list
                    mSelectedMailList.remove(mailData);
                } else {
                    mSelectedMailList.add(mailData);
                }
            }

            isShowToolBar = true;
            setToolBarTitle();

        });
        // load more when hit bottom
        recyclerView.addOnScrollListener(mEndlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (Util.isNetworkAvailable() && !DaZoneApplication.getInstance().offlineMode) {
                    if (mAllowLoadOld) {
                        long lastEmailNo = 0;
                        if (mMailDataList != null && mMailDataList.size() > 0) {
                            lastEmailNo = mMailDataList.get(mMailDataList.size() - 1).getMailNo();
                        }

                        getListEmailFromServer(lastEmailNo);
                    }
                } else {
                    mAdapter.setProgressOn(true);
                    new GetMailListTask(mMailBoxNo, mSearchQuery, fragment).execute();
                }
            }
        });
        // load data from server
        if (mMailBoxNo != 0 && check) {
            sortColum = 4;
            isAscend = false;

            initSwipeRefreshLayout();
        }
    }

    //TODO AUTO LOAD EMAIL HAS APPLY BUT NOT REQUEST FROM MR.GIAP BECAUSE HIDE FUNCTION
    @Subscribe
    public void AutoReloadWhenHaveNewMail(ReloadListMail reloadListMail) {
        refreshData(true);
    }

    private boolean isShowToolBar = false;
    public void initSwipeRefreshLayout() {
        swipeRefreshLayout = mView.findViewById(R.id.common_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> refreshData(true));
    }

    public void refreshData(final boolean isLoad) {
        //load offline
        if (!Util.isNetworkAvailable() || DaZoneApplication.getInstance().offlineMode) {
            mAdapter.setProgressOn(true);
            new GetMailListTask(mMailBoxNo, mSearchQuery, fragment).execute();
            return;
        }
        int reloadEmailCount;
        long anchorMailNo = 0;
        if (mMailDataList.size() == 0) {
            if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
            getListEmailFromServer(0);
            return;
        }

        reloadEmailCount = Statics.DEFAULT_GET_NEW_DISPLAY_LIST_COUNT;
        mMailDataList = new ArrayList<>();

        try {
            mAdapter.setProgressOn(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpRequest.getInstance().getEmailList(mMailBoxNo, anchorMailNo, reloadEmailCount, true, mMailType, mSearchType, mSearchQuery, quickSearch, sortColum, isAscend, new OnMailListCallBack() {
            @Override
            public void onMailListSuccess(List<MailData> mailDataList, int totalEmailCount) {
                if (isVisible()) {
                    mAdapter.setProgressOn(false);
                    swipeRefreshLayout.setRefreshing(false);
                    if (sortColum == 4) {
                        List<MailData> listTemp = TimeUtils.CheckDateTime(mailDataList);
                        mMailDataList.clear();
                        mMailDataList.addAll(listTemp);
                    } else {
                        mMailDataList.clear();
                        mMailDataList.addAll(mailDataList);
                    }

                    if (mMailDataList.size() == 0) {
                        txtNoData.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        txtNoData.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    initRecyclerView(false);
                    if (isLoad) {
                        if (getActivity() instanceof ListEmailActivity) {
                            ((ListEmailActivity) getActivity()).reloadMenu(1);
                        }
                    }
                }
            }

            @Override
            public void onMailListFail(ErrorData errorData) {
                if (isVisible()) {
                    mAdapter.setProgressOn(false);
                }
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void changeToolBarStatusToEditMode(boolean isActive) {
        if (isActive) {
            // hide drawer layout
            ((ToolBarActivity) getActivity()).setDrawerState(false);
            // displayAsFolder back button
            ((ToolBarActivity) getActivity()).displayToolBarBackButton(true, true);
            ((ToolBarActivity) getActivity()).getDrawerToggle().setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // release all selected email
                    releaseAllSelectedItem();
                    isShowToolBar = false;
                    EventBus.getDefault().post(new reloadTitle());
                }
            });
        } else {
            // show drawer layout again
            ((ToolBarActivity) getActivity()).setDrawerState(true);
            ((ToolBarActivity) getActivity()).getDrawerToggle().setToolbarNavigationClickListener(null);
        }
        mIsEditModeActivated = isActive;
        ActionBar actionbar = ((ToolBarActivity) getActivity()).getSupportActionBar();
        if (actionbar != null)
            actionbar.invalidateOptionsMenu();
    }

    /**
     * Set all checked item to un-check
     *
     * @return true if releasable else return false
     */
    public boolean releaseAllSelectedItem() {
        if (mIsEditModeActivated) {
            isShowToolBar = false;
            for (MailData mailData : mSelectedMailList) {
                mailData.setSelected(false);
                int indexOf = mMailDataList.indexOf(mailData);
                // run flip animation
                checkUnCheckItemWithAnimation(indexOf, false);
            }
            mSelectedMailList.clear();
            if (mAdapter != null) {
                mAdapter.ClearListSelect();
            }

            changeToolBarStatusToEditMode(false);
            ((ToolBarActivity) getActivity()).setToolBarTitle("");

            return true;
        } else {
            return false;
        }
    }

    private void selectAllMailInList() {
        int index = 0;
        for (MailData mailData : mMailDataList) {
            if (!mailData.isSelected() && !mailData.isDeleted()) {
                mailData.setSelected(true);
                checkUnCheckItemWithAnimation(index, true);
            }
            index++;
        }
        mSelectedMailList.clear();
        mSelectedMailList.addAll(mMailDataList);
        setToolBarTitle();
    }

    private void checkUnCheckItemWithAnimation(int index, boolean isSelected) {
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(index);
        if (holder != null && holder instanceof ListEmailRecyclerAdapter.ItemViewHolder)
            mAdapter.showHideCheckAnimation((ListEmailRecyclerAdapter.ItemViewHolder) holder, isSelected);
    }

    private void setToolBarTitle() {
//        if (mSelectedCount < 1) {
//            changeToolBarStatusToEditMode(false);
//            ((ToolBarActivity) getActivity()).setToolBarTitle("");
//        } else {
//            if (mMenu != null) {
//                MenuItem menuItem = mMenu.findItem(R.id.menu_list_set_filter);
//                if (menuItem != null && mSelectedCount == 1) {
//                    menuItem.setVisible(true);
//                } else {
//                    menuItem.setVisible(false);
//                }
//            }
//            ((ToolBarActivity) getActivity()).setToolBarTitle(mSelectedCount + "/" + mMailDataList.size());
//        }

        if (mMenu != null) {
            MenuItem menuItem = mMenu.findItem(R.id.menu_list_set_filter);
            if (menuItem != null) {
                menuItem.setVisible(true);
            } else {
                menuItem.setVisible(false);
            }
        }
        ((ToolBarActivity) getActivity()).setToolBarTitle(countRealMail(mSelectedMailList) + "/" + countRealMail(mMailDataList));
    }

    private void setReadUnreadForSelectedList(MenuItem item) {
        if (mSelectedMailList != null && mSelectedMailList.size() > 0) {
            boolean isFirstOneRead = mSelectedMailList.get(0).isRead();
            for (MailData selectedMail : mSelectedMailList) {
                // set back to unread if first one already read , else set to read
                selectedMail.setRead(!isFirstOneRead);
            }
            mAdapter.notifyDataSetChanged();
            //checkMailUnread(mSelectedMailList);
            // set to open email
            if (item != null) {
                if (isFirstOneRead) {
                    item.setIcon(R.drawable.ic_drafts_white_24dp);
                } else {
                    item.setIcon(R.drawable.navbar_mail_ic);
                }
            }
            // call api
            HttpRequest.getInstance().updateEmailReadUnRead(!isFirstOneRead, mSelectedMailList, this);
        }
    }

    public void setEmailAsImportant(MailData mailData) {
        HttpRequest.getInstance().updateEmailImportant(!mailData.isImportant(), mailData.getMailNo(), this);
        mailData.setImportant(!mailData.isImportant());
        int adapterPosition = mMailDataList.indexOf(mailData);
        mAdapter.notifyItemChanged(adapterPosition);
    }

    public void setListOfMailAsImportant(boolean isSetImportant) {
        HttpRequest.getInstance().updateEmailImportant(isSetImportant, mSelectedMailList, this);
        for (MailData selectedMail : mSelectedMailList) {
            selectedMail.setImportant(isSetImportant);
        }
        mAdapter.notifyDataSetChanged();
    }

    public void deleteEmailFromSelectedList() {
        final ArrayList<MailData> tempDeleteEmailList = new ArrayList<>();
        for (MailData selectedEmail : mSelectedMailList) {
            selectedEmail.setDeleted(true, getString(R.string.deleting));
            tempDeleteEmailList.add(selectedEmail);
        }
        mAdapter.ClearListSelect();
        mAdapter.notifyDataSetChanged();
        mSelectedMailList.clear();
        HttpRequest.getInstance().moveEmailToTrash(tempDeleteEmailList, mIsTrashBox, new BaseHTTPCallBack() {
            @Override
            public void onHTTPSuccess() {

                handleDeleteEmailInListSuccess(tempDeleteEmailList);
                EventBus.getDefault().post(new reloadTitle());
            }

            @Override
            public void onHTTPFail(ErrorData errorDto) {
                handleDeleteEmailListFail(tempDeleteEmailList);
            }
        });
    }

    public void moveEmailFromSelectedList(int mailBoxNo) {
        final ArrayList<MailData> tempDeleteEmailList = new ArrayList<>();
        for (MailData selectedEmail : mSelectedMailList) {
            selectedEmail.setDeleted(true, getString(R.string.moving));
            tempDeleteEmailList.add(selectedEmail);
        }
        mAdapter.notifyDataSetChanged();
        mSelectedMailList.clear();
        HttpRequest.getInstance().moveEmailToBox(tempDeleteEmailList, mailBoxNo, new BaseHTTPCallBack() {
            @Override
            public void onHTTPSuccess() {
                handleDeleteEmailInListSuccess(tempDeleteEmailList);
            }

            @Override
            public void onHTTPFail(ErrorData errorDto) {
                handleDeleteEmailListFail(tempDeleteEmailList);
            }
        });
    }

    private void handleDeleteEmailInListSuccess(ArrayList<MailData> tempDeleteEmailList) {
        try {
            for (MailData removeEmail : tempDeleteEmailList) {
                int index = mMailDataList.indexOf(removeEmail);
                if (index == mMailDataList.size() - 1 && index > 0 && mMailDataList.get(index - 1).getMailNo() < 0) {
                    mMailDataList.remove(index);
                    if (mMailDataList.size() > 1)
                        mMailDataList.remove(index - 1);
                } else if (index > 0 && mMailDataList.get(index - 1).getMailNo() < 0 && mMailDataList.get(index + 1).getMailNo() < 0) {
                    mMailDataList.remove(index);
                    mMailDataList.remove(index - 1);
                } else {
                    mMailDataList.remove(index);
                }
                mAdapter.notifyItemRemoved(index);
            }
            if (mMailDataList.size() > 1) {
                mEndlessRecyclerOnScrollListener.reset(0, false);
                recyclerView.setAdapter(mAdapter);
            } else {
                refreshData(false);
            }
            if (mSelectedMailList.size() == 0)
                changeToolBarStatusToEditMode(false);
            setToolBarTitle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteEmailListFail(ArrayList<MailData> tempDeleteEmailList) {
        for (MailData tempMail : tempDeleteEmailList) {
            tempMail.setDeleted(false, "");
            tempMail.setSelected(false);
        }
        mAdapter.notifyDataSetChanged();
        tempDeleteEmailList.clear();
        if (mSelectedMailList.size() == 0)
            changeToolBarStatusToEditMode(false);
    }

    public void GetTagMenu(boolean isSkipCache) {
        final LinkedHashMap<String, Object> menuList = null;
        String cachedMenuJson = DaZoneApplication.getInstance().getPrefs().getMenuListData();
        String accessToken = DaZoneApplication.getInstance().getPrefs().getAccessToken();
        if (!isSkipCache && !TextUtils.isEmpty(cachedMenuJson) && cachedMenuJson.contains(accessToken + "#@#")) {
            String[] splitString = cachedMenuJson.split("#@#");
            ShowDialog(MailHelper.convertJsonStringToMap(splitString[1]));
        } else {
            HttpRequest.getInstance().getEmailMenuList(new OnMenuListCallBack() {
                @Override
                public void onMenuListSuccess(LinkedHashMap menuMap) {
                    if (isVisible()) {
                        ShowDialog(menuMap);
                    }
                }

                @Override
                public void onMenuListFail(ErrorData errorData) {
                    if (isVisible()) {
                        ShowDialog(menuList);
                    }
                }
            });
        }
    }

    public void ShowDialog(LinkedHashMap menuMap) {
        final List<MailTagMenuData> itemList = new ArrayList<>();
        if (menuMap != null) {
            Iterator<?> keySets = menuMap.keySet().iterator();
            while (keySets.hasNext()) {
                String key = (String) keySets.next();
                List<?> menuList = (List<?>) menuMap.get(key);
                for (Object menuData : menuList) {
                    if (menuData instanceof MailTagMenuData) {
                        itemList.add((MailTagMenuData) menuData);
                    }
                }
            }
        }
        itemList.add(0, new MailTagMenuData(0, 0, getString(R.string.string_title_mail_no_tag), false));
        MailHelper.displaySingleChoiceList(getActivity(), itemList, (dialog, which) -> {
            if (mSelectedMailList != null && mSelectedMailList.size() > 0)
                HttpRequest.getInstance().updateTagOfMail(itemList.get(which).getTagNo(), mSelectedMailList, new BaseHTTPCallBack() {
                    @Override
                    public void onHTTPSuccess() {
                        if (isVisible()) {
                            for (MailData selectedMail : mSelectedMailList) {
                                selectedMail.setmImageNo(itemList.get(which).getImageNo());
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                        Util.showMessage(getString(R.string.string_success));
                    }

                    @Override
                    public void onHTTPFail(ErrorData errorDto) {
                        Util.showMessage(getString(R.string.string_update_fail));
                    }
                });
        }, getString(R.string.app_name));
    }

    public PopupWindow mPopupWindow() {
        final PopupWindow popupWindow = new PopupWindow(getActivity());
        ListView listViewDogs = new ListView(getActivity());
        listViewDogs.setDividerHeight(0);
        listViewDogs.setPadding(0, 0, 0, 10);
        listViewDogs.setBackgroundResource(R.color.background_holo_light);
        SortAdapter tagAdapter = new SortAdapter(getActivity(), R.layout.tag, listSort);
        listViewDogs.setAdapter(tagAdapter);

        listViewDogs.setOnItemClickListener((parent, view, position, id) -> {
            MenuSortData menuSortData = listSort.get(position);
            sortColum = menuSortData.getSort();
            int index = MailHelper.getSelectPosition(listSort);
            if (index == position) {
                if (menuSortData.getIsCheck() == 1) {
                    menuSortData.setIsCheck(2);
                    isAscend = true;
                } else {
                    menuSortData.setIsCheck(1);
                    isAscend = false;
                }
            } else {
                menuSortData.setIsCheck(1);
                listSort.get(index).setIsCheck(0);
                isAscend = false;
            }
            refreshData(false);
            popupWindow.dismiss();
        });

        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        popupWindow.setWidth((int) getResources().getDimension(R.dimen.dimen_180_360));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(listViewDogs);
        return popupWindow;
    }

    public void genDataSort(String mMailBoxClassName) {
        listSort = new ArrayList<>();
        if (mMailBoxClassName.equals("OutBoxIcon") || mMailBoxClassName.equals("DraftBoxIcon")) {
            listSort.add(new MenuSortData(getString(R.string.string_title_menu_name), 0, 1));
            listSort.add(new MenuSortData(getString(R.string.string_title_menu_to), 0, 2));
            listSort.add(new MenuSortData(getString(R.string.string_title_menu_title), 0, 3));
            listSort.add(new MenuSortData(getString(R.string.date_sent), 1, 4));
            listSort.add(new MenuSortData(getString(R.string.string_title_menu_size), 0, 5));
            listSort.add(new MenuSortData(getString(R.string.string_title_menu_important), 0, 6));
            listSort.add(new MenuSortData(getString(R.string.string_title_menu_read), 0, 7));
            listSort.add(new MenuSortData(getString(R.string.string_title_menu_file), 0, 8));
        } else {
            listSort.add(new MenuSortData(getString(R.string.string_title_menu_name), 0, 1));
            listSort.add(new MenuSortData(getString(R.string.string_title_menu_to), 0, 2));
            listSort.add(new MenuSortData(getString(R.string.string_title_menu_title), 0, 3));
            listSort.add(new MenuSortData(getString(R.string.string_title_menu_reg_date), 1, 4));
            listSort.add(new MenuSortData(getString(R.string.string_title_menu_size), 0, 5));
            listSort.add(new MenuSortData(getString(R.string.string_title_menu_important), 0, 6));
            listSort.add(new MenuSortData(getString(R.string.string_title_menu_read), 0, 7));
            listSort.add(new MenuSortData(getString(R.string.string_title_menu_file), 0, 8));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mIsEditModeActivated) {
            mView.findViewById(R.id.common_swipe_refresh).setEnabled(false);
            inflater.inflate(R.menu.list_mail_menu, menu);
            // set read/unread icon based on first item select on list
            try {
                if (mSelectedMailList.get(0).isRead()) {
                    menu.findItem(R.id.menu_list_set_unread).setIcon(R.drawable.navbar_mail_ic);
                } else {
                    menu.findItem(R.id.menu_list_set_unread).setIcon(R.drawable.ic_drafts_white_24dp);
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            mMenu = menu;
        } else {
            mMenu = null;

            if (mView == null) {
                getActivity().finish();
            } else {
                mView.findViewById(R.id.common_swipe_refresh).setEnabled(true);
            }
            inflater.inflate(R.menu.search_menu, menu);
            menuTemp = menu;

            mListCheckAll = menu.findItem(R.id.menu_list_check_all);
            mListCheckAll.setVisible(false);
            try {
                SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
                searchView = (SearchView) menu.findItem(R.id.search).getActionView();
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
                searchView.setIconifiedByDefault(true);
                SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                    public boolean onQueryTextChange(String newText) {
                        mSearchType = 1;
                        mSearchQuery = newText;
                        Log.d("searchView,", " onQueryTextChange " + newText);
                        mHandler.removeCallbacks(mFilterTask);
                        mHandler.postDelayed(mFilterTask, 500);
                        return true;
                    }

                    public boolean onQueryTextSubmit(String query) {
                        // handle search action here
                        mSearchType = 1;
                        mSearchQuery = query;
                        Log.d("searchView,", " onQueryTextSubmit " + query);
                        refreshData(false);
                        return true;
                    }
                };
                searchView.setOnQueryTextListener(queryTextListener);

                searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {

                    @Override
                    public void onViewDetachedFromWindow(View arg0) {
                    }

                    @Override
                    public void onViewAttachedToWindow(View arg0) {
                        Log.d("sss>>> open", " open");
                    }
                });


                ImageView closeButton = searchView.findViewById(R.id.search_close_btn);

                // Set on click listener
                closeButton.setOnClickListener(v -> {
                    EditText et = searchView.findViewById(R.id.search_src_text);
                    et.setText("");
                    searchView.setQuery("", false);
                    searchView.onActionViewCollapsed();


                });

                AutoCompleteTextView searchTextView = searchView.findViewById(R.id.search_src_text);

                Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                mCursorDrawableRes.setAccessible(true);
                mCursorDrawableRes.set(searchTextView, R.drawable.cursor);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_list_check_all:
                if (isCheck) {
                    if (!mIsEditModeActivated) {
                        changeToolBarStatusToEditMode(true);
                    }
                    selectAllMailInList();
                    isCheck = false;
                } else {
                    releaseAllSelectedItem();
                    if (mAdapter != null) {
                        mAdapter.ClearListSelect();
                    }
                    isCheck = true;
                }
                break;
            case R.id.menu_list_delete:
                deleteEmailFromSelectedList();
                break;
            case R.id.menu_list_set_unread:
                setReadUnreadForSelectedList(item);
                break;
            case R.id.menu_mark_important:
                MenuItem importantMenuItem = mMenu.findItem(R.id.menu_mark_important);
                if (importantMenuItem.getTitle().equals(getString(R.string.mark_not_important))) {
                    setListOfMailAsImportant(false);
                } else {
                    setListOfMailAsImportant(true);
                }
                break;
            case R.id.menu_more:
                MenuItem menuItem = mMenu.findItem(R.id.menu_mark_important);
                boolean isAllImportant = true;
                for (MailData selectedMail : mSelectedMailList) {
                    if (!selectedMail.isImportant()) {
                        isAllImportant = false;
                        break;
                    }
                }
                if (isAllImportant) {
                    menuItem.setTitle(R.string.mark_not_important);
                } else {
                    menuItem.setTitle(R.string.mark_as_important);
                }
                break;

            case R.id.menu_move_to:
                Intent intent = new Intent(getActivity(), MoveMailBoxActivity.class);
                intent.putExtra(StaticsBundle.BUNDLE_TASK, 1);
                startActivityForResult(intent, Statics.EMAIL_MOVE_ACTIVITY);
                break;

            case R.id.menu_tag:
                GetTagMenu(false);
                break;

            case R.id.menu_filter_address:
                Intent intent1 = new Intent(getActivity(), MoveMailBoxActivity.class);
                intent1.putExtra(StaticsBundle.BUNDLE_TASK, 2);
                startActivityForResult(intent1, Statics.EMAIL_FILTER_ADDRESS_ACTIVITY);
                break;

            case R.id.menu_filter_sender:
                Intent intent2 = new Intent(getActivity(), MoveMailBoxActivity.class);
                intent2.putExtra(StaticsBundle.BUNDLE_TASK, 2);
                startActivityForResult(intent2, Statics.EMAIL_FILTER_SENDER_ACTIVITY);
                break;
            case R.id.search:
                if (searchView != null) {
                    if (menuTemp != null) {
                        menuTemp.findItem(R.id.menu_sort).setVisible(false);
                    }
                    ((ListEmailActivity) getActivity()).setFab(false);
                    item.expandActionView();
                    searchView.setQueryHint(getString(R.string.string_title_menu_title));
                    ((ListEmailActivity) getActivity()).setDrawerState(false);
                    MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionExpand(MenuItem item) {
                            return true;
                        }

                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem item) {
                            ((ListEmailActivity) getActivity()).setFab(true);
                            searchView.setQuery("", false);
                            mSearchQuery = "";
                            refreshData(false);
                            if (menuTemp != null) {
                                menuTemp.findItem(R.id.menu_sort).setVisible(true);
                            }
                            ((ListEmailActivity) getActivity()).setDrawerState(true);
                            return true;
                        }
                    });
                }
                break;
            case R.id.menu_sort:
                popupWindow = mPopupWindow();
                popupWindow.showAtLocation(swipeRefreshLayout, Gravity.RIGHT | Gravity.TOP, 0, 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int boxNoAfter, boxNoBefore = 0;
        String address = null, sender = null;

        if (resultCode == Activity.RESULT_OK) {
            if (mSelectedMailList != null && mSelectedMailList.size() == 1) {
                boxNoBefore = mSelectedMailList.get(0).getmBoxNo();
                address = mSelectedMailList.get(0).getFromEmail();
                sender = mSelectedMailList.get(0).getFromName();
            }
            switch (requestCode) {
                case Statics.EMAIL_MOVE_ACTIVITY:
                    Bundle b = data.getExtras();
                    if (b != null) {
                        moveEmailFromSelectedList(b.getInt(StaticsBundle.BUNDLE_MAIL_BOX_NO));
                    }
                    break;
                case Statics.EMAIL_FILTER_ADDRESS_ACTIVITY:
                    Bundle bc = data.getExtras();
                    if (bc != null) {
                        boxNoAfter = bc.getInt(StaticsBundle.BUNDLE_MAIL_BOX_NO);
                        if (!TextUtils.isEmpty(address)) {
                            final ArrayList<MailData> temp = new ArrayList<>();
                            temp.addAll(mSelectedMailList);
                            mSelectedMailList.clear();
                            HttpRequest.getInstance().filterAddressMail(boxNoBefore, boxNoAfter, address, new BaseHTTPCallBack() {
                                @Override
                                public void onHTTPSuccess() {
                                    handleDeleteEmailInListSuccess(temp);
                                    Util.showMessage(getString(R.string.string_title_filter_success));
                                }

                                @Override
                                public void onHTTPFail(ErrorData errorDto) {
                                    Util.showMessage(getString(R.string.string_title_filter_fail));
                                }
                            });
                        }
                    }
                    break;
                case Statics.EMAIL_FILTER_SENDER_ACTIVITY:
                    Bundle bd = data.getExtras();
                    if (bd != null) {
                        boxNoAfter = bd.getInt(StaticsBundle.BUNDLE_MAIL_BOX_NO);
                        if (!TextUtils.isEmpty(sender)) {
                            final ArrayList<MailData> temp = new ArrayList<>();
                            temp.addAll(mSelectedMailList);
                            mSelectedMailList.clear();
                            HttpRequest.getInstance().filterSenderMail(boxNoBefore, boxNoAfter, sender, new BaseHTTPCallBack() {
                                @Override
                                public void onHTTPSuccess() {
                                    handleDeleteEmailInListSuccess(temp);
                                    Util.showMessage(getString(R.string.string_title_filter_success));
                                }

                                @Override
                                public void onHTTPFail(ErrorData errorDto) {
                                    Util.showMessage(getString(R.string.string_title_filter_fail));
                                }
                            });
                        }
                    }
                    break;
            }
        }
    }

    Runnable mFilterTask = () -> refreshData(false);

    @Override
    public void onHTTPSuccess() {
    }

    @Override
    public void onHTTPFail(ErrorData errorDto) {
    }

    private void createRecursiveList
            (ArrayList<PersonData> list, ArrayList<PersonData> parentList) {
        Iterator<PersonData> iter = list.iterator();
        while (iter.hasNext()) {
            PersonData tempPerson = iter.next();
            for (PersonData person : parentList) {
                if (person.getType() == 1) {
                    if (tempPerson.getType() == 1 && person.getDepartNo() == tempPerson.getDepartmentParentNo()) {
                        // department compare by departNo and parentNo
                        person.addChild(tempPerson);
                        iter.remove();
                        parentList.remove(tempPerson);
                        break;
                    } else if (tempPerson.getType() == 2 && person.getDepartNo() == tempPerson.getDepartNo()) {
                        // member , compare by departNo and departNo
                        person.addChild(tempPerson);
                        iter.remove();
                        parentList.remove(tempPerson);
                        break;
                    }
                    if (person.getPersonList() != null && person.getPersonList().size() > 0) {
                        // not in root list , search in child list
                        ArrayList<PersonData> test = new ArrayList<>();
                        test.add(tempPerson);
                        createRecursiveList(test, person.getPersonList());
                    }
                }
            }
        }
    }

    @Override
    public void onGetMailList(ArrayList<MailData> mailData) {
        if (mailData != null) {
            mMailDataList.clear();
            mMailDataList.addAll(mailData);
            mAdapter.notifyDataSetChanged();
            mAdapter.setProgressOn(false);
            if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
        } else {
            String str;//you need to retrieve this string from shared preferences.
            Prefs prefs = DaZoneApplication.getInstance().getPrefs();
            str = prefs.getListData();
            Type type = new TypeToken<ArrayList<MailData>>() {
            }.getType();
            mailData = new Gson().fromJson(str, type);
            if (mailData != null) {
                mMailDataList.clear();
                mMailDataList.addAll(mailData);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private int countRealMail(ArrayList<MailData> mailData) {
        int count = 0;
        for(MailData mailData1 : mailData) {
            if(mailData1.getContent() != null) {
                count++;
            }
        }
        return count;
    }


}
