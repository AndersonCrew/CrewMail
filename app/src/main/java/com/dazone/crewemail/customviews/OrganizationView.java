package com.dazone.crewemail.customviews;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dazone.crewemail.R;
import com.dazone.crewemail.data.PersonData;
import com.dazone.crewemail.database.OrganizationUserDBHelper;
import com.dazone.crewemail.interfaces.OnOrganizationSelectedEvent;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.Util;
import com.dazone.crewemail.webservices.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Sherry on 12/31/15.
 */
public class OrganizationView {
    private String TAG = "OrganizationView";
    private ArrayList<PersonData> mPersonList = new ArrayList<>();
    private ArrayList<PersonData> mSelectedPersonList;
    private Context mContext;
    private int displayType = 0; // 0 folder structure , 1
    private OnOrganizationSelectedEvent mSelectedEvent;
    private boolean loaded = false;

    public OrganizationView(Context context, ArrayList<PersonData> selectedPersonList, boolean isDisplaySelectedOnly, ViewGroup viewGroup) {
        this.mContext = context;
        if (selectedPersonList != null) {
            this.mSelectedPersonList = selectedPersonList;
        } else {
            this.mSelectedPersonList = new ArrayList<>();
        }
        if (isDisplaySelectedOnly) {
            initSelectedList(selectedPersonList, viewGroup);
        }



    }

    /**
     * this function automatically set all to selected
     *
     * @param selectedPersonList
     */
    private void initSelectedList(ArrayList<PersonData> selectedPersonList, ViewGroup viewGroup) {
        mPersonList = new ArrayList<>();
        for (PersonData selectedPerson : selectedPersonList) {
            selectedPerson.setIsCheck(true);
            if (selectedPerson.getType() == 2) {
                mPersonList.add(selectedPerson);
            }
        }
        createRecursiveList(mPersonList, mPersonList);
        displayAsFolder(viewGroup);
    }

    private void createRecursiveList(ArrayList<PersonData> list, ArrayList<PersonData> parentList) {

        //create recursive list
        Iterator<PersonData> iter = list.iterator();
        while (iter.hasNext()) {
            PersonData tempPerson = iter.next();
            for (PersonData person : parentList) {
                if (person.getType() == 1) {
                    if (tempPerson.getType() == 1 && person.getDepartNo() == tempPerson.getDepartmentParentNo()) {
                        person.addChild(tempPerson);
                        iter.remove();
                        parentList.remove(tempPerson);
                        break;
                    } else if (tempPerson.getType() == 2 && person.getDepartNo() == tempPerson.getDepartNo()) {
                        person.addChild(tempPerson);
                        iter.remove();
                        parentList.remove(tempPerson);
                        break;
                    }
                    if (person.getPersonList() != null && person.getPersonList().size() > 0) {
                        ArrayList<PersonData> test = new ArrayList<>();
                        test.add(tempPerson);
                        createRecursiveList(test, person.getPersonList());
                    }
                }
            }
        }
    }

    public void displayAsFolder(ViewGroup viewGroup) {
        this.displayType = 0;
        for (PersonData personData : mPersonList) {
            if (personData.getType() == 2) {
                for (PersonData department : OrganizationUserDBHelper.getDepartments(HttpRequest.sRootLink)) {
                    if (department.getDepartNo() == personData.getDepartNo()) {
                        draw(personData, viewGroup, false, 0);
                        break;
                    }
                }
            } else {
                if (personData.getDepartmentParentNo() == 0) {
                    draw(personData, viewGroup, false, 0);
                }
            }
        }
    }

    private void getPersonDataWithQuery(String query, ArrayList<PersonData> searchResultList, ArrayList<PersonData> searchList) {
        for (PersonData personData : searchList) {
            if (personData.getType() == 2) {
                if ((personData.getFullName() != null && personData.getFullName().toLowerCase().contains(query))
                        || (personData.getEmail() != null && personData.getEmail().toLowerCase().contains(query))) {
                    boolean isAdd = true;
                    for (PersonData userAdded : searchResultList) {
                        if (userAdded.getUserNo() == personData.getUserNo()) {
                            isAdd = false;
                        }
                    }
                    if (isAdd) {
                        searchResultList.add(personData);
                    }
                }
            }
            if (personData.getPersonList() != null && personData.getPersonList().size() > 0) {
                getPersonDataWithQuery(query, searchResultList, personData.getPersonList());
            }
        }
    }

    private void draw(final PersonData personData, final ViewGroup layout, final boolean checked, final int iconMargin) {
        final LinearLayout child_list;
        final LinearLayout iconWrapper;
        final ImageView avatar;
        final ImageView folderIcon;
        final TextView name;
        final CheckBox row_check;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_organization, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        layout.addView(view);
        child_list = view.findViewById(R.id.child_list);
        avatar = view.findViewById(R.id.avatar);
        folderIcon = view.findViewById(R.id.icon);
        iconWrapper = view.findViewById(R.id.icon_wrapper);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) iconWrapper.getLayoutParams();
        if (displayType == 0) // set margin for icon if it's company type
        {
            params.leftMargin = iconMargin;
        }
        iconWrapper.setLayoutParams(params);
        name = view.findViewById(R.id.name);
        row_check = view.findViewById(R.id.row_check);
        row_check.setChecked(personData.isCheck());

        String nameString = personData.getFullName();
        if (personData.getType() == 2) {
            String url = new Prefs().getServerSite() + personData.getUrlAvatar();
            ImageLoader.getInstance().displayImage(url, avatar, Statics.options2);
            nameString += !TextUtils.isEmpty(personData.getPositionName()) ? " (" + personData.getPositionName() + ")" : "";
            folderIcon.setVisibility(View.GONE);
        } else {
            avatar.setVisibility(View.GONE);
            folderIcon.setVisibility(View.VISIBLE);
        }
        name.setText(nameString);

        final int tempMargin = iconMargin + Util.getDimenInPx(R.dimen.activity_login_user_margin);
        row_check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked && personData.getType() == 2) {
                ViewGroup parent = ((ViewGroup) layout.getParent());
                unCheckBoxParent(parent);
            } else {
                if (buttonView.getTag() != null && !(Boolean) buttonView.getTag()) {
                    buttonView.setTag(true);
                } else {
                    personData.setIsCheck(isChecked);
                    if (personData.getPersonList() != null && personData.getPersonList().size() != 0) {
                        int index = 0;
                        for (PersonData dto1 : personData.getPersonList()) {

                            dto1.setIsCheck(isChecked);
                            View childView = child_list.getChildAt(index);
                            CheckBox childCheckBox = childView.findViewById(R.id.row_check);
                            if (childCheckBox != null) {
                                if (childCheckBox.isEnabled()) {
                                    childCheckBox.setChecked(dto1.isCheck());
                                }

                            } else {
                                break;
                            }
                            index++;
                        }
                    }
                }
            }
            if (mSelectedEvent != null) {
                mSelectedEvent.onOrganizationCheck(isChecked, personData);
            }
        });

        String temp = personData.getDepartNo() + personData.getFullName();
        if (!TextUtils.isEmpty(temp)) {
            if (new Prefs().getBooleanValue(temp, true)) {
                child_list.setVisibility(View.VISIBLE);
            } else {
                child_list.setVisibility(View.GONE);
            }
        }

        if (personData.getPersonList() != null && personData.getPersonList().size() != 0) {
            folderIcon.setOnClickListener(v -> showHideSubMenuView(child_list, folderIcon, personData));
            name.setOnClickListener(v -> showHideSubMenuView(child_list, folderIcon, personData));

            for (PersonData dto1 : personData.getPersonList()) {
                draw(dto1, child_list, false, tempMargin);
            }
        }
    }

    private void unCheckBoxParent(ViewGroup view) {
        if (view.getId() == R.id.item_org_main_wrapper || view.getId() == R.id.item_org_wrapper) {
            CheckBox parentCheckBox = view.findViewById(R.id.row_check);
            if (parentCheckBox.isChecked()) {
                parentCheckBox.setTag(false);
                parentCheckBox.setChecked(false);
            }
            if ((view.getParent()).getParent() instanceof ViewGroup) {
                try {
                    ViewGroup parent = (ViewGroup) (view.getParent()).getParent();
                    unCheckBoxParent(parent);
                } catch (Exception e) {
                }
            }
        }
    }

    private void showHideSubMenuView(LinearLayout child_list, ImageView icon, PersonData personData) {
        String temp = personData.getDepartNo() + personData.getFullName();
        if (child_list.getVisibility() == View.VISIBLE) {
            child_list.setVisibility(View.GONE);
            icon.setImageResource(R.drawable.ic_folder_close);
            new Prefs().putBooleanValue(temp, false);

        } else {
            child_list.setVisibility(View.VISIBLE);
            icon.setImageResource(R.drawable.ic_folder_open);
            new Prefs().putBooleanValue(temp, true);

        }
    }
}
