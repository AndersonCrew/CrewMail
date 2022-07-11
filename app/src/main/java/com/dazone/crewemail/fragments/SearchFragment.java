package com.dazone.crewemail.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dazone.crewemail.BuildConfig;
import com.dazone.crewemail.R;
import com.dazone.crewemail.activities.MoveMailBoxActivity;
import com.dazone.crewemail.activities.ToolBarActivity;
import com.dazone.crewemail.adapter.ListEmailRecyclerAdapter;
import com.dazone.crewemail.customviews.EndlessRecyclerOnScrollListener;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.MailData;
import com.dazone.crewemail.interfaces.BaseHTTPCallBack;
import com.dazone.crewemail.interfaces.OnMailListCallBack;
import com.dazone.crewemail.utils.EmailBoxStatics;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.StaticsBundle;
import com.dazone.crewemail.webservices.HttpRequest;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends BaseFragment implements BaseHTTPCallBack {

    protected SearchView searchView;
    private View mView;
    private ListEmailRecyclerAdapter mAdapter;
    private ArrayList<MailData> mMailDataList;
    private ArrayList<MailData> mSelectedMailList;
    private boolean mIsEditModeActivated;
    private RecyclerView recyclerView;
    private boolean mAllowLoadOld;
    private SwipeRefreshLayout swipeRefreshLayout;

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
    private long quickSearch = 0;
    private int sortColum = 4;
    private boolean isAscend = false;

    public static SearchFragment newInstance(int mailBoxNo, String emailBoxClassName, int mailType) {
        Bundle args = new Bundle();
        args.putInt("mailBoxNo", mailBoxNo);
        args.putInt("mailType", mailType);
        args.putBoolean("isTrashBox", (EmailBoxStatics.MAIL_CLASS_TRASH_BOX.equals(emailBoxClassName)));
        args.putString("emailBoxClassName", TextUtils.isEmpty(emailBoxClassName) ? "" : emailBoxClassName);
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMailDataList = new ArrayList<>();
        mSelectedMailList = new ArrayList<>();
        mIsEditModeActivated = false;
        setHasOptionsMenu(true);
        mAllowLoadOld = true;
        mSearchType = 1;
        mSearchQuery = "";
        mMailBoxNo = getArguments().getInt("mailBoxNo");
        mMailType = getArguments().getInt("mailType");
        mMailBoxClassName = getArguments().getString("emailBoxClassName");
        mIsTrashBox = getArguments().getBoolean("isTrashBox");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.common_recycler_view, container, false);

        initRecyclerView();
        if (mMailBoxNo != 0) {
            initSwipeRefreshLayout();
        }
        return mView;
    }

    //isTask = 0 if load data default
    //=1 if refesh data
    private void getListEmailFromServer(long anchorMailNo) {
        mAdapter.setProgressOn(true);
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
                    if (lastPosition == 0)
                        mAdapter.notifyDataSetChanged();
                    else {
                        mAdapter.notifyItemRangeInserted(lastPosition, mailDataList.size());
                    }
                    if (mMailDataList.size() == totalEmailCount)
                        mAllowLoadOld = false;
                }
            }

            @Override
            public void onMailListFail(ErrorData errorData) {
                if (isVisible()) {
                    mAdapter.setProgressOn(false);
                    if (TextUtils.isEmpty(errorData.getMessage()))
                        Snackbar.make(mView, R.string.exception_error, Snackbar.LENGTH_LONG).show();
                    else
                        Snackbar.make(mView, errorData.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initRecyclerView() {
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
            if (mSelectedMailList.contains(mailData)) {
                // remove if already add selected list
                mSelectedMailList.remove(mailData);
            } else {
                mSelectedMailList.add(mailData);
            }
            setToolBarTitle();

        });
        // load more when hit bottom
        recyclerView.addOnScrollListener(mEndlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (mAllowLoadOld) {
                    long lastEmailNo = 0;
                    if (mMailDataList != null && mMailDataList.size() > 0) {
                        lastEmailNo = mMailDataList.get(mMailDataList.size() - 1).getMailNo();
                    }
                    getListEmailFromServer(lastEmailNo);
                }
            }
        });
    }

    public void initSwipeRefreshLayout() {
        swipeRefreshLayout = mView.findViewById(R.id.common_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this::RefeshData);
    }

    public void RefeshData() {
        int reloadEmailCount;
        long anchorMailNo;
        if (mMailDataList == null || mMailDataList.size() == 0) {
            swipeRefreshLayout.setRefreshing(false);
            mAdapter.setProgressOn(true);
            getListEmailFromServer(0);
            return;
        }

        reloadEmailCount = Statics.DEFAULT_GET_NEW_DISPLAY_LIST_COUNT;
        mMailDataList = new ArrayList<>();
        anchorMailNo = 0;

        HttpRequest.getInstance().getEmailList(mMailBoxNo, anchorMailNo, reloadEmailCount, true, mMailType, mSearchType, mSearchQuery, quickSearch, sortColum, isAscend, new OnMailListCallBack() {
            @Override
            public void onMailListSuccess(List<MailData> mailDataList, int totalEmailCount) {
                if (isVisible()) {
                    mAdapter.setProgressOn(false);
                    swipeRefreshLayout.setRefreshing(false);
                    mMailDataList.clear();
                    mMailDataList.addAll(mailDataList);
                    mAdapter = new ListEmailRecyclerAdapter(SearchFragment.this, mMailDataList, mMailBoxClassName);
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.setOnMailItemSelectListener(mailData -> {
                        if (!mIsEditModeActivated)
                            changeToolBarStatusToEditMode(true);
                        if (mSelectedMailList.contains(mailData)) {
                            // remove if already add selected list
                            mSelectedMailList.remove(mailData);
                        } else {
                            mSelectedMailList.add(mailData);
                        }
                        setToolBarTitle();
                    });
                }
            }

            @Override
            public void onMailListFail(ErrorData errorData) {
                if (isVisible()) {
                    mAdapter.setProgressOn(false);
                    Snackbar.make(mView, errorData.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void changeToolBarStatusToEditMode(boolean isActive) {
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
            setToolBarTitle();

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
        int mSelectedCount = mSelectedMailList.size();
        if (mSelectedCount < 1) {
            changeToolBarStatusToEditMode(false);
        } else {
            ((ToolBarActivity) getActivity()).setToolBarTitle(mSelectedCount + "/" + mMailDataList.size());
        }
    }

    private void setReadUnreadForSelectedList(MenuItem item) {
        if (mSelectedMailList != null && mSelectedMailList.size() > 0) {
            boolean isFirstOneRead = mSelectedMailList.get(0).isRead();
            for (MailData selectedMail : mSelectedMailList) {
                // set back to unread if first one already read , else set to read
                selectedMail.setRead(!isFirstOneRead);
            }
            mAdapter.notifyDataSetChanged();
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
                if (isVisible()) {
                    handleDeleteEmailInListSuccess(tempDeleteEmailList);
                }
            }

            @Override
            public void onHTTPFail(ErrorData errorDto) {
                handleDeleteEmailListFail(tempDeleteEmailList);
                if (TextUtils.isEmpty(errorDto.getMessage()))
                    Snackbar.make(mView, R.string.update_data_error, Snackbar.LENGTH_LONG).show();
                else
                    Snackbar.make(mView, errorDto.getMessage(), Snackbar.LENGTH_LONG).show();
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
                if (isVisible()) {
                    handleDeleteEmailInListSuccess(tempDeleteEmailList);
                }
            }

            @Override
            public void onHTTPFail(ErrorData errorDto) {
                handleDeleteEmailListFail(tempDeleteEmailList);
                if (TextUtils.isEmpty(errorDto.getMessage()))
                    Snackbar.make(mView, R.string.update_data_error, Snackbar.LENGTH_LONG).show();
                else
                    Snackbar.make(mView, errorDto.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void handleDeleteEmailInListSuccess(ArrayList<MailData> tempDeleteEmailList) {
        for (MailData removeEmail : tempDeleteEmailList) {
            int index = mMailDataList.indexOf(removeEmail);
            mMailDataList.remove(index);
            mAdapter.notifyItemRemoved(index);
        }
        if (mMailDataList.size() > 0) {
            mEndlessRecyclerOnScrollListener.reset(0, false);
            recyclerView.setAdapter(mAdapter);
        } else {
            getListEmailFromServer(0);
        }
        if (mSelectedMailList.size() == 0)
            changeToolBarStatusToEditMode(false);
        setToolBarTitle();
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
            mView.findViewById(R.id.common_swipe_refresh).setEnabled(true);
            inflater.inflate(R.menu.menu_search, menu);

            mListCheckAll = menu.findItem(R.id.menu_list_check_all);
            mListCheckAll.setVisible(false);

            SearchManager searchManager =
                    (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            searchView =
                    (SearchView) menu.findItem(R.id.search).getActionView();
            if (null != searchView) {
                searchView.setSearchableInfo(searchManager
                        .getSearchableInfo(getActivity().getComponentName()));
                searchView.setIconifiedByDefault(true);
                ActionBar.LayoutParams p = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                searchView.setLayoutParams(p);
                menu.findItem(R.id.search).expandActionView();
                searchView.setQueryHint(getString(R.string.string_title_menu_title));
                MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.search), new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return false;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        getActivity().finish();
                        searchView.setQuery("", false);
                        return false;
                    }
                });
            }
            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                public boolean onQueryTextChange(String newText) {
                    return true;
                }

                public boolean onQueryTextSubmit(String query) {
                    searchView.clearFocus();
                    mSearchQuery = query;
                    RefeshData();
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
            searchView.setOnCloseListener(() -> {
                mSearchQuery = "";
                getListEmailFromServer(0);
                return true;
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_list_check_all:
                if (isCheck == true) {
                    if (!mIsEditModeActivated)
                        changeToolBarStatusToEditMode(true);
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
                startActivityForResult(intent, Statics.EMAIL_MOVE_ACTIVITY);

                break;

            case R.id.none:
                mSearchType = 0;
                searchView.setQueryHint(getString(R.string.string_title_menu_none));
                break;
            case R.id.title:
                mSearchType = 1;
                searchView.setQueryHint(getString(R.string.string_title_menu_title));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onHTTPSuccess() {
        new Prefs().putBooleanValue(StaticsBundle.PREFS_KEY_RELOAD_LIST, true);
    }

    @Override
    public void onHTTPFail(ErrorData errorDto) {
        if (TextUtils.isEmpty(errorDto.getMessage()))
            Snackbar.make(mView, R.string.update_data_error, Snackbar.LENGTH_LONG).show();
        else
            Snackbar.make(mView, errorDto.getMessage(), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Statics.EMAIL_MOVE_ACTIVITY:
                    Bundle b = data.getExtras();
                    if (b != null) {
                        moveEmailFromSelectedList(b.getInt(StaticsBundle.BUNDLE_MAIL_BOX_NO));
                    }
                    break;
            }
        }
    }
}
