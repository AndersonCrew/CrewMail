package com.dazone.crewemail.customviews;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.MailBoxMenuData;
import com.dazone.crewemail.data.MailTagMenuData;
import com.dazone.crewemail.interfaces.OnMenuListCallBack;
import com.dazone.crewemail.utils.EmailBoxStatics;
import com.dazone.crewemail.utils.MailHelper;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.StaticsBundle;
import com.dazone.crewemail.utils.Util;
import com.dazone.crewemail.webservices.HttpRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class MailMenuView implements View.OnClickListener {
    private final LinearLayout menuLayout;
    private DrawerLayout mDrawerLayout;
    private Activity mActivity;
    private OnMenuItemClickListener mMenuItemClickListener;
    private View previousActiveMenu;
    private boolean mIsReload;
    private boolean mIsMoveToBox = false;
    private List<MailTagMenuData> list;
    private int isTask = 0;
    private boolean isCheckMove = false;

    public MailMenuView(Activity context, LinearLayout menuLayout, DrawerLayout rootView) {
        this.mActivity = context;
        this.mDrawerLayout = rootView;
        this.menuLayout = menuLayout;
        mIsReload = false;

    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
        this.mMenuItemClickListener = menuItemClickListener;
    }

    public void initMenu(boolean isSkipCache) {
        String cachedMenuJson = DaZoneApplication.getInstance().getPrefs().getMenuListData();
        String accessToken = DaZoneApplication.getInstance().getPrefs().getAccessToken();
        // get menu from cache if exist
        if((!Util.isNetworkAvailable() || DaZoneApplication.getInstance().offlineMode) && !TextUtils.isEmpty(cachedMenuJson) && cachedMenuJson.contains(accessToken+"#@#")){
            String[] splitString = cachedMenuJson.split("#@#");
            displayParentMenuItem(MailHelper.convertJsonStringToMap(splitString[1]));
        }else{
            // don't have any menu in cache , request server for the new one
            HttpRequest.getInstance().getEmailMenuList(new OnMenuListCallBack() {
                @Override
                public void onMenuListSuccess(LinkedHashMap menuMap) {
                    displayParentMenuItem(menuMap);
                }

                @Override
                public void onMenuListFail(ErrorData errorData) {
                    if(errorData!=null&&errorData.getMessage()!=null) {
                        Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //task 1-move 2-filter
    public void init(boolean isSkipCache, final int task) {
        String cachedMenuJson = DaZoneApplication.getInstance().getPrefs().getMenuListData();
        String accessToken = DaZoneApplication.getInstance().getPrefs().getAccessToken();
        // get menu from cache if exist
        if (!isSkipCache && !TextUtils.isEmpty(cachedMenuJson) && cachedMenuJson.contains(accessToken + "#@#")) {
            String[] splitString = cachedMenuJson.split("#@#");
            displayParentMenuItemForMove(MailHelper.convertJsonStringToMap(splitString[1]), task, true);
        } else {
            // don't have any menu in cache , request server for the new one
            HttpRequest.getInstance().getEmailMenuList(new OnMenuListCallBack() {
                @Override
                public void onMenuListSuccess(LinkedHashMap menuMap) {
                    displayParentMenuItemForMove(menuMap, task, true);
                }

                @Override
                public void onMenuListFail(ErrorData errorData) {
                    Toast.makeText(mActivity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        isCheckMove = true;
    }

    public void reloadMenu(int isTasks) {
        initMenu(true);
        mIsReload = true;
        isTask = isTasks;
    }

    public void displayParentMenuItem(LinkedHashMap menuMap) {
        boolean tempCheck = false;
        Iterator<?> keySets = menuMap.keySet().iterator();
        List<MailBoxMenuData> list = new ArrayList<>();
        menuLayout.removeAllViews();
        while (keySets.hasNext()) {
            String key = (String) keySets.next();
            if ((key.equals(EmailBoxStatics.DEFAULT_BOX))) {
                List<MailBoxMenuData> listTemp = (List<MailBoxMenuData>) menuMap.get(key);
                list.addAll(listTemp);
            } else if (key.equals(EmailBoxStatics.RECEIVE_BOX)) {
                if (!tempCheck) {
                    List<MailBoxMenuData> listTemp = (List<MailBoxMenuData>) menuMap.get(key);
                    list.get(MailHelper.getIndexOfMenu(list, 1)).setChildBox(listTemp);
                    tempCheck = true;
                } else {
                    View view = mActivity.getLayoutInflater().inflate(R.layout.item_nav_menu, menuLayout, false);
                    menuLayout.addView(view);
                    mapFolderNameAndDrawable(EmailBoxStatics.DEFAULT_BOX, view.findViewById(R.id.item_nav_menu_name));
                    final ImageView imgDrop = view.findViewById(R.id.item_nav_menu_drop);
                    List<MailBoxMenuData> listTemp = (List<MailBoxMenuData>) menuMap.get(key);
                    list.get(MailHelper.getIndexOfMenu(list, 1)).setChildBox(listTemp);
                    final View temp = mActivity.getLayoutInflater().inflate(R.layout.layout_temp, menuLayout, false);
                    menuLayout.addView(temp);
                    displaySubMenuItem(list, (LinearLayout) temp, mActivity.getResources().getDimensionPixelSize(R.dimen.dimen_20_40), true, false);
                    // set click listener for each item
                    if (new Prefs().getBooleanValue(key, true)) {
                        temp.setVisibility(View.VISIBLE);
                        imgDrop.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                    } else {
                        temp.setVisibility(View.GONE);
                        imgDrop.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                    }
                    LinearLayout wrapper = view.findViewById(R.id.item_nav_menu_wrapper);
                    wrapper.setTag(key);
                    wrapper.setClickable(true);
                    wrapper.setOnClickListener(v -> {
                        if (temp.getVisibility() == View.GONE) {
                            temp.setVisibility(View.VISIBLE);
                            imgDrop.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                            new Prefs().putBooleanValue(String.valueOf(v.getTag()), true);
                        } else {
                            temp.setVisibility(View.GONE);
                            imgDrop.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                            new Prefs().putBooleanValue(String.valueOf(v.getTag()), false);
                        }
                    });
                }
            } else if (key.equals(EmailBoxStatics.SENT_BOX)) {
                if (!tempCheck) {
                    List<MailBoxMenuData> listTemp = (List<MailBoxMenuData>) menuMap.get(key);
                    list.get(MailHelper.getIndexOfMenu(list, 0)).setChildBox(listTemp);
                    tempCheck = true;
                } else {
                    View view = mActivity.getLayoutInflater().inflate(R.layout.item_nav_menu, menuLayout, false);
                    menuLayout.addView(view);
                    mapFolderNameAndDrawable(EmailBoxStatics.DEFAULT_BOX, view.findViewById(R.id.item_nav_menu_name));
                    final ImageView imgDrop = view.findViewById(R.id.item_nav_menu_drop);
                    List<MailBoxMenuData> listTemp = (List<MailBoxMenuData>) menuMap.get(key);
                    list.get(MailHelper.getIndexOfMenu(list, 0)).setChildBox(listTemp);
                    final View temp = mActivity.getLayoutInflater().inflate(R.layout.layout_temp, menuLayout, false);
                    menuLayout.addView(temp);
                    displaySubMenuItem(list, (LinearLayout) temp, mActivity.getResources().getDimensionPixelSize(R.dimen.dimen_20_40), true, false);
                    // set click listener for each item
                    if (new Prefs().getBooleanValue(key, true)) {
                        temp.setVisibility(View.VISIBLE);
                        imgDrop.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                    } else {
                        temp.setVisibility(View.GONE);
                        imgDrop.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                    }
                    LinearLayout wrapper = view.findViewById(R.id.item_nav_menu_wrapper);
                    wrapper.setTag(key);
                    wrapper.setClickable(true);
                    wrapper.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (temp.getVisibility() == View.GONE) {
                                temp.setVisibility(View.VISIBLE);
                                imgDrop.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                                new Prefs().putBooleanValue(String.valueOf(v.getTag()), true);
                            } else {
                                temp.setVisibility(View.GONE);
                                imgDrop.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                                new Prefs().putBooleanValue(String.valueOf(v.getTag()), false);
                            }
                        }
                    });
                }
            } else {
                List<?> menuList = (List<?>) menuMap.get(key);
                View view = mActivity.getLayoutInflater().inflate(R.layout.item_nav_menu, menuLayout, false);
                if (key.equals(EmailBoxStatics.FAV_BOX) && menuList.size() > 0) {
                    menuLayout.addView(view);
                } else if (!key.equals(EmailBoxStatics.FAV_BOX)) {
                    menuLayout.addView(view);
                }

                // displayAsFolder as normal
                mapFolderNameAndDrawable(key, view.findViewById(R.id.item_nav_menu_name));
                final ImageView imgDrop = view.findViewById(R.id.item_nav_menu_drop);
                final View temp = mActivity.getLayoutInflater().inflate(R.layout.layout_temp, menuLayout, false);
                menuLayout.addView(temp);
                displaySubMenuItem(menuList, (LinearLayout) temp, mActivity.getResources().getDimensionPixelSize(R.dimen.dimen_20_40), false, false);
                // set click listener for each item
                if (new Prefs().getBooleanValue(key, true)) {
                    temp.setVisibility(View.VISIBLE);
                    imgDrop.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                } else {
                    temp.setVisibility(View.GONE);
                    imgDrop.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                }
                LinearLayout wrapper = view.findViewById(R.id.item_nav_menu_wrapper);
                wrapper.setTag(key);
                wrapper.setClickable(true);
                wrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (temp.getVisibility() == View.GONE) {
                            temp.setVisibility(View.VISIBLE);
                            imgDrop.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                            new Prefs().putBooleanValue(String.valueOf(v.getTag()), true);
                        } else {
                            temp.setVisibility(View.GONE);
                            imgDrop.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                            new Prefs().putBooleanValue(String.valueOf(v.getTag()), false);
                        }
                    }
                });
            }
//            wrapper.setOnClickListener(this);
        }
    }

    //is menu new - true - menu move and filter
    //- false - Normal
    public void displayParentMenuItemForMove(LinkedHashMap menuMap, int task, boolean isMenuNew) {
        boolean tempCheck = false;
        Iterator<?> keySets = menuMap.keySet().iterator();
        List<MailBoxMenuData> list = new ArrayList<>();
//        LinearLayout menuLayout = (LinearLayout) mRootView.findViewById(R.id.nav_menu_item_wrapper);
        menuLayout.removeAllViews();
        while (keySets.hasNext()) {
            String key = (String) keySets.next();
            if ((key.equals(EmailBoxStatics.DEFAULT_BOX))) {
                List<MailBoxMenuData> listTemp = getListAffterCheck((List<MailBoxMenuData>) menuMap.get(key), task);
                list.addAll(listTemp);
            } else if (key.equals(EmailBoxStatics.RECEIVE_BOX)) {
                if (!tempCheck) {
                    List<MailBoxMenuData> listTemp = (List<MailBoxMenuData>) menuMap.get(key);
                    list.get(MailHelper.getIndexOfMenu(list, 1)).setChildBox(listTemp);
                    tempCheck = true;
                } else {
                    List<MailBoxMenuData> listTemp = (List<MailBoxMenuData>) menuMap.get(key);
                    list.get(MailHelper.getIndexOfMenu(list, 1)).setChildBox(listTemp);
                    displaySubMenuItem(list, menuLayout, 0, true, isMenuNew);
                }
            } else if (key.equals(EmailBoxStatics.SENT_BOX)) {
                if (!tempCheck) {
                    if (task == 1) {
                        List<MailBoxMenuData> listTemp = (List<MailBoxMenuData>) menuMap.get(key);
                        list.get(MailHelper.getIndexOfMenu(list, 0)).setChildBox(listTemp);
                    }
                    tempCheck = true;
                } else {
                    if (task == 1) {
                        List<MailBoxMenuData> listTemp = (List<MailBoxMenuData>) menuMap.get(key);
                        list.get(MailHelper.getIndexOfMenu(list, 0)).setChildBox(listTemp);
                    }
                    displaySubMenuItem(list, menuLayout, 0, true, isMenuNew);
                }
            }
//            wrapper.setOnClickListener(this);
        }
    }

    //task = 1 - Move // TODO: 27/01/2016
    //task = 2 - Filter
    public List<MailBoxMenuData> getListAffterCheck(List<MailBoxMenuData> list, int task) {
        for (Iterator<MailBoxMenuData> iterator = list.iterator(); iterator.hasNext(); ) {
            MailBoxMenuData mailBoxMenuData = iterator.next();
            if (task == 1) {
                if (mailBoxMenuData.getName().equalsIgnoreCase(Util.getString(R.string.string_title_menu_unread_box))
                        || mailBoxMenuData.getName().equalsIgnoreCase(Util.getString(R.string.string_title_menu_reserved))
                        || mailBoxMenuData.getName().equalsIgnoreCase(Util.getString(R.string.string_title_menu_inbox))) {
                    //list.remove(mailBoxMenuData);
                    iterator.remove();
                }
            } else {
                if (mailBoxMenuData.getName().equalsIgnoreCase(Util.getString(R.string.string_title_menu_inbox))
                        || mailBoxMenuData.getName().equalsIgnoreCase(Util.getString(R.string.string_title_menu_outbox))
                        || mailBoxMenuData.getName().equalsIgnoreCase(Util.getString(R.string.string_title_menu_unread_box))
                        || mailBoxMenuData.getName().equalsIgnoreCase(Util.getString(R.string.string_title_menu_draft_box))
                        || mailBoxMenuData.getName().equalsIgnoreCase(Util.getString(R.string.string_title_menu_reserved))) {
                    //list.remove(mailBoxMenuData);
                    iterator.remove();
                }
            }

        }
        return list;
    }

    /**
     * Display Submenu Item
     *
     * @param menuDataList child list data
     * @param menuLayout   layout which want to add child item to
     * @param marginLeft   margin for each child item
     */
    private int unReadCountAllBox = 0;
    private void displaySubMenuItem(List<?> menuDataList, LinearLayout menuLayout, int marginLeft, boolean isCallClickEvent, final boolean isMenuNew) {
        int index = 0;
        View temp = null;
        List<MailTagMenuData> listTag = new ArrayList<>();
        for (Object menuData : menuDataList) {
            View view;
            if (isMenuNew) {
                view = mActivity.getLayoutInflater().inflate(R.layout.item_nav_menu_new, menuLayout, false);
            } else {
                view = mActivity.getLayoutInflater().inflate(R.layout.item_nav_menu, menuLayout, false);
            }
            menuLayout.addView(view);
            final ImageView imgDrop = view.findViewById(R.id.item_nav_menu_drop);
            TextView navMenuName = view.findViewById(R.id.item_nav_menu_name);
            TextView navCount = view.findViewById(R.id.item_nav_menu_count);
            if (menuData instanceof MailBoxMenuData) {
                MailBoxMenuData mailBoxMenuData = (MailBoxMenuData) menuData;
                if (mailBoxMenuData.getName().equalsIgnoreCase(Util.getString(R.string.string_title_menu_outbox))) {
                    new Prefs().putLongValue(StaticsBundle.PREFS_KEY_COMPOSE, mailBoxMenuData.getBoxNo());
                }
                if (mailBoxMenuData.getChildBox() != null && mailBoxMenuData.getChildBox().size() > 0) {
                    ((LinearLayout.LayoutParams) imgDrop.getLayoutParams()).setMargins(marginLeft, mActivity.getResources().getDimensionPixelSize(R.dimen.dimen_10_20), 0
                            , mActivity.getResources().getDimensionPixelSize(R.dimen.dimen_10_20));
                } else {
                    imgDrop.setVisibility(View.INVISIBLE);
                    ((LinearLayout.LayoutParams) navMenuName.getLayoutParams()).setMargins(marginLeft, 0, 0, 0);
                }
            } else {
                imgDrop.setVisibility(View.INVISIBLE);
                ((LinearLayout.LayoutParams) navMenuName.getLayoutParams()).setMargins(marginLeft, 0, 0, 0);
            }
            // set margin left for child
            if (menuData instanceof MailTagMenuData) {
                //set drawable for each tag
                navMenuName.setCompoundDrawablesWithIntrinsicBounds(MailHelper.getColorTag(((MailTagMenuData) menuData).getImageNo()), 0, 0, 0);
                navMenuName.setText(((MailTagMenuData) menuData).getName());

                listTag.add((MailTagMenuData) menuData);
            } else if (menuData instanceof MailBoxMenuData) {
                // set drawable for each item
                MailBoxMenuData mailBoxMenuData = (MailBoxMenuData) menuData;
                navMenuName.setCompoundDrawablesWithIntrinsicBounds(mailBoxMenuData.getClassNameResourceId(), 0, 0, 0);
                navMenuName.setText(mailBoxMenuData.getName());
                if(mailBoxMenuData.getSortNo() == 1) {
                    unReadCountAllBox = mailBoxMenuData.getUnreadCount();
                } else if(mailBoxMenuData.getSortNo() == 7) {
                    navCount.setText(unReadCountAllBox + "");
                }

                temp = mActivity.getLayoutInflater().inflate(R.layout.layout_temp, menuLayout, false);
                menuLayout.addView(temp);
                if (!isMenuNew) {
                    int mailBoxNo = mailBoxMenuData.getBoxNo();
                    if (new Prefs().getBooleanValue(String.valueOf(mailBoxNo), true)) {
                        temp.setVisibility(View.VISIBLE);
                        imgDrop.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                    } else {
                        temp.setVisibility(View.GONE);
                        imgDrop.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                    }
                }
                if (mailBoxMenuData.getChildBox() != null && mailBoxMenuData.getChildBox().size() > 0) {
                    // do recursive if having child
                    int marginLeftChild = mActivity.getResources().getDimensionPixelSize(R.dimen.dimen_25_50) + marginLeft;
                    displaySubMenuItem(mailBoxMenuData.getChildBox(), (LinearLayout) temp, marginLeftChild, false, isMenuNew);
                    imgDrop.setVisibility(View.VISIBLE);
                } else {
                    imgDrop.setVisibility(View.INVISIBLE);
                }
                imgDrop.setTag(mailBoxMenuData);
            }

            final View finalTemp = temp;
            imgDrop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalTemp != null) {
                        int mailBoxNo = ((MailBoxMenuData) v.getTag()).getBoxNo();
                        if (finalTemp.getVisibility() == View.GONE) {
                            finalTemp.setVisibility(View.VISIBLE);
                            imgDrop.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                            if (!isMenuNew)
                                new Prefs().putBooleanValue(String.valueOf(mailBoxNo), true);
                        } else {
                            finalTemp.setVisibility(View.GONE);
                            imgDrop.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                            if (!isMenuNew)
                                new Prefs().putBooleanValue(String.valueOf(mailBoxNo), false);
                        }
                    }
                }
            });

            // set click listener for item
            LinearLayout wrapper = view.findViewById(R.id.item_nav_menu_wrapper);
            wrapper.setTag(menuData);
            wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                    Object object = v.getTag();
                    if (previousActiveMenu != null)
                        previousActiveMenu.setActivated(false);
                    v.setActivated(true);
                    if (mMenuItemClickListener != null) {
                        mMenuItemClickListener.onMenuItemClick(object);
                        if (!isCheckMove) {
                            if (object instanceof MailBoxMenuData) {
                                MailBoxMenuData menuData = (MailBoxMenuData) object;
                                new Prefs().putLongValue(Statics.SAVE_BOX_NO_PREF, menuData.getBoxNo());
                            } else if (object instanceof MailTagMenuData) {
                                MailTagMenuData menuData = (MailTagMenuData) object;
                                new Prefs().putLongValue(Statics.SAVE_BOX_NO_PREF, menuData.getTagNo());
                            }
                        }
                    }
                    previousActiveMenu = v;
                }
            });
            if (isTask == 0) {
                Long No = new Prefs().getLongValue(Statics.SAVE_BOX_NO_PREF, 0);


//                Log.d(">>>sss menu", No + "");
                if (No != 0) {
                    if (menuData instanceof MailTagMenuData) {
                        if (((MailTagMenuData) menuData).getTagNo() == No) {
                            wrapper.callOnClick();
                        }
                    } else if (menuData instanceof MailBoxMenuData) {
                        if (((MailBoxMenuData) menuData).getBoxNo() == No) {
                            wrapper.callOnClick();
                        }
                    }
                } else {
                    if (isCallClickEvent && index == 0) {
                        wrapper.callOnClick();
                    }
                }

            }
            if (isTask == 3) {
                Long No = new Prefs().getLongValue(Statics.SAVE_BOX_NO_PREF_V2, 0);
                Log.d(">>>sssDebugNono", No + "");
                if (No != 0) {
                    if (menuData instanceof MailTagMenuData) {
                        if (((MailTagMenuData) menuData).getTagNo() == No) {
                            wrapper.callOnClick();
                        }
                    } else if (menuData instanceof MailBoxMenuData) {
                        if (((MailBoxMenuData) menuData).getBoxNo() == No) {
                            wrapper.callOnClick();
                        }
                    }
                } else {
                    if (isCallClickEvent && index == 0) {
                        wrapper.callOnClick();
                    }
                }
            }

            index++;
        }
        setListTag(listTag);
    }


    /**
     * Map json field name with displayAsFolder name
     *
     * @param name         json field name or class name
     * @param nameTextView text view which contain name
     */
    private void mapFolderNameAndDrawable(String name, TextView nameTextView) {
        switch (name) {
            case EmailBoxStatics.FAV_BOX:
                nameTextView.setText(R.string.favorite);
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sidebar_ic_01, 0, 0, 0);
                break;
            case EmailBoxStatics.DEFAULT_BOX:
                nameTextView.setText(R.string.default_box);
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sidebar_ic_02, 0, 0, 0);
                break;
/*            case EmailBoxStatics.RECEIVE_BOX:
                nameTextView.setText(R.string.received_box);
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sidebar_ic_04,0,0,0);
                break;
            case EmailBoxStatics.SENT_BOX:
                nameTextView.setText(R.string.sent_box);
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sidebar_ic_07,0,0,0);
                break;*/
            case EmailBoxStatics.MAIL_TAG_BOX:
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sidebar_ic_tag_01, 0, 0, 0);
                nameTextView.setText(R.string.tagged_mail);
                break;

        }
    }

    private void mapTagColor() {

    }

    @Override
    public void onClick(View v) {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        Object object = v.getTag();
        if (previousActiveMenu != null)
            previousActiveMenu.setActivated(false);
        v.setActivated(true);
        if (mMenuItemClickListener != null) {
            mMenuItemClickListener.onMenuItemClick(object);
            if (!isCheckMove) {
                if (object instanceof MailBoxMenuData) {
                    MailBoxMenuData menuData = (MailBoxMenuData) object;
                    new Prefs().putLongValue(Statics.SAVE_BOX_NO_PREF, menuData.getBoxNo());
                } else if (object instanceof MailTagMenuData) {
                    MailTagMenuData menuData = (MailTagMenuData) object;
                    new Prefs().putLongValue(Statics.SAVE_BOX_NO_PREF, menuData.getTagNo());
                }
            }
        }
        previousActiveMenu = v;
    }

    public interface OnMenuItemClickListener {
        /**
         * click event for each item in menu
         *
         * @param object Object MailBoxMenuData ||Object MailTagMenuData || Object String
         */
        void onMenuItemClick(Object object);
    }

    public void setListTag(List<MailTagMenuData> listTag) {
        this.list = listTag;
    }

    public List<MailTagMenuData> getListTag() {
        return list;
    }
}
