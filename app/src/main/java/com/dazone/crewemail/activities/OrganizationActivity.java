package com.dazone.crewemail.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.dazone.crewemail.R;
import com.dazone.crewemail.adapter.AdapterOrganizationPager;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.PersonData;
import com.dazone.crewemail.interfaces.OnGetAllOfUser;
import com.dazone.crewemail.utils.Constants;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.StaticsBundle;
import com.dazone.crewemail.webservices.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Sherry on 12/30/15.
 */
public class OrganizationActivity extends BaseActivity {
    private ImageView imgBack, imgDone;
    private TextView tvCompany, tvSearch;
    private ViewPager vpOrganization;
    private AdapterOrganizationPager adapter;
    private CompanyFragmentRenew companyFragmentRenew;
    private SearchFragmentRenew searchFragmentRenew;
    private ArrayList<PersonData> selectedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization);
        initViews();
        getDataOrganization();
    }

    private void initViews() {
        companyFragmentRenew = new CompanyFragmentRenew();
        searchFragmentRenew = new SearchFragmentRenew();
        imgBack = findViewById(R.id.imgBack);
        imgDone = findViewById(R.id.imgDone);
        tvCompany = findViewById(R.id.tvCompany);
        tvSearch = findViewById(R.id.tvSearch);
        if(getIntent() != null && getIntent().getStringExtra(Constants.SELECTED_LIST) != null) {
            Gson gson = new Gson();
            Type userListType = new TypeToken<ArrayList<PersonData>>(){}.getType();
            ArrayList<PersonData> list = gson.fromJson(getIntent().getStringExtra(Constants.SELECTED_LIST), userListType);
            if(list != null && list.size() > 0) {
                selectedList.addAll(list);
            }

        }

    }


    private void getDataOrganization() {
        ArrayList<PersonData> data = new Prefs().getListOrganization();
        if(data == null || data.size() <= 0) {
            showProgressDialog();
            HttpRequest.getInstance().getDepartment(new OnGetAllOfUser() {
                @Override
                public void onGetAllOfUserSuccess(ArrayList<PersonData> list) {
                    getListMember(list, true);
                }

                @Override
                public void onGetAllOfUserFail(ErrorData errorData) {

                }
            });
        } else {
            initViewPager();
        }
    }

    private int tabSelected = -1;
    private void changeTab(int tab) {
        tabSelected = tab;

        if (tab == 0) {
            tvCompany.setBackgroundResource(R.drawable.bg_selected);
            tvCompany.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvSearch.setBackgroundResource(R.drawable.bg_non_selected);
            tvSearch.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));

        } else {
            tvCompany.setBackgroundResource(R.drawable.bg_non_selected);
            tvSearch.setBackgroundResource(R.drawable.bg_selected);

            tvCompany.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            tvSearch.setTextColor(ContextCompat.getColor(this, R.color.white));
        }

        vpOrganization.setCurrentItem(tab);
        Log.d("ANDERSON", "tab = " + tab);
    }

    private void getListMember(ArrayList<PersonData> list, boolean flag) {
        for(PersonData personData : list) {
            HttpRequest.getInstance().getUserByDepartment(personData.getDepartNo(), new OnGetAllOfUser() {
                @Override
                public void onGetAllOfUserSuccess(ArrayList<PersonData> listMember) {
                    personData.setListMembers(listMember);
                }

                @Override
                public void onGetAllOfUserFail(ErrorData errorData) {

                }

            });

            if(personData.getPersonList() != null && personData.getPersonList().size() > 0) {
                getListMember(personData.getPersonList(), false);
            }
        }

        if(flag) {
            new Handler().postDelayed(() -> {
                dismissProgressDialog();

                new Prefs().putListOrganization(list);

                initViewPager();
            }, 3000);
        }
    }

    private void initViewPager() {

    }

    private void addSelectedList() {
        if(selectedList.size() > 0) {
            ArrayList<PersonData> data = new Prefs().getListOrganization();
            for(PersonData personData : selectedList) {
                checkAddSelected(personData, data);
            }

            new Prefs().putListOrganization(data);
        }
    }

    private void checkAddSelected(PersonData personData, ArrayList<PersonData> data) {
        if(data != null && data.size() > 0) {
            for(PersonData department : data) {
                if(department.getListMembers() != null && department.getListMembers().size() > 0) {
                    setMemberListAdd(department.getListMembers(), personData);
                }

                if(department.getPersonList() != null && department.getPersonList().size() > 0) {
                    setDepartmentListAdd(department.getPersonList(), personData);
                }
            }
        }
    }

    private void setMemberListAdd(ArrayList<PersonData> list, PersonData personData) {
        for(PersonData member : list) {
            if(member.getmEmail().equals(personData.getmEmail())) {
                member.setIsCheck(true);
            }
        }
    }
    private void setDepartmentListAdd(ArrayList<PersonData> list, PersonData personData) {
        for(PersonData department : list) {

            if(department.getListMembers() != null && department.getListMembers().size() > 0) {
                setMemberListAdd(department.getListMembers(), personData);
            }

            if(department.getPersonList() != null && department.getPersonList().size() > 0) {
                setDepartmentListAdd(department.getPersonList(), personData);
            }
        }
    }

    @Override
    protected void onDestroy() {
        resetSelectedList();
        super.onDestroy();
    }

    private void resetSelectedList() {
        ArrayList<PersonData> data = new Prefs().getListOrganization();

        if(data != null && data.size() > 0) {
            for(PersonData personData : data) {
                personData.setIsCheck(false);

                if(personData.getListMembers() != null && personData.getListMembers().size() > 0) {
                    setMemberList(personData.getListMembers());
                }

                if(personData.getPersonList() != null && personData.getPersonList().size() > 0) {
                    setDepartmentList(personData.getPersonList());
                }
            }
        }

        new Prefs().putListOrganization(data);
    }

    private void setDepartmentList(ArrayList<PersonData> personList) {
        for(PersonData personData : personList) {
            personData.setIsCheck(false);

            if(personData.getListMembers() != null && personData.getListMembers().size() > 0) {
                setMemberList(personData.getListMembers());
            }

            if(personData.getPersonList() != null && personData.getPersonList().size() > 0) {
                setDepartmentList(personData.getPersonList());
            }
        }
    }

    private void setMemberList(ArrayList<PersonData> listMembers) {
        for(PersonData member : listMembers) {
            member.setIsCheck(false);
        }
    }
}
