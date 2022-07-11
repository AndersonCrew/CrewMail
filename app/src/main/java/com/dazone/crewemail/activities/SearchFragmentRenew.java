package com.dazone.crewemail.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.dazone.crewemail.R;
import com.dazone.crewemail.adapter.MemberAdapter;
import com.dazone.crewemail.data.PersonData;
import com.dazone.crewemail.utils.Constants;
import com.dazone.crewemail.utils.Prefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.annotation.Nullable;

public class SearchFragmentRenew extends Fragment {

    private RecyclerView rvMembers;
    private MemberAdapter adapter;
    private ArrayList<PersonData> listMember;
    private ImageView imgSearch;
    private EditText etSearch;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_renew, container, false);
        rvMembers = v.findViewById(R.id.rvMembers);
        imgSearch = v.findViewById(R.id.imgSearch);
        etSearch = v.findViewById(R.id.etSearch);
        getData();
        return v;
    }

    private void getData() {
        Gson gson = new Gson();

        Type userListType = new TypeToken<ArrayList<PersonData>>(){}.getType();

        ArrayList<PersonData> data = gson.fromJson(new Prefs().getStringValue(Constants.ORGANIZATION, ""), userListType);

        if(data != null && data.size() > 0) {
            listMember = new ArrayList<>();
            for(PersonData personData : data) {
                if(personData.getListMembers() != null && personData.getListMembers().size() > 0) {
                    for(PersonData member : personData.getListMembers()) {
                        if(!listMember.contains(member)) {
                            listMember.add(member);
                        }
                    }
                }

                if(personData.getPersonList() != null && personData.getPersonList().size() > 0) {
                    checkDepartment(listMember, personData.getPersonList());
                }
            }

            initRecyclerView(listMember);

            imgSearch.setOnClickListener(v -> {
                if(!etSearch.getText().toString().isEmpty()) {
                    actionSearch();
                }
            });

            etSearch.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(!etSearch.getText().toString().isEmpty()) {
                        actionSearch();
                    }
                    return true;
                }
                return false;
            });
        }
    }

    private void bindSearchToList() {
        if(listSearch.size() > 0) {
            for(PersonData personData : listSearch) {
                if(personData.isCheck()) {
                    checkList(personData, listMember);
                }
            }
        }

        initRecyclerView(listMember);
    }

    private void hideKeyBoard() {
        InputMethodManager imm =(InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
    }

    private void checkList(PersonData personData, ArrayList<PersonData> list) {
        for(PersonData person : list) {
            if(person.getUserNo() == personData.getUserNo()) {
                person.setIsCheck(true);
                return;
            }
        }
    }

    private void initRecyclerView(ArrayList<PersonData> listMember) {
        adapter = new MemberAdapter(listMember, new MemberAdapter.IAllMemberChecked() {
            @Override
            public void onAllMemberChecked() {

            }

            @Override
            public void onAllMemberNonChecked() {

            }
        });

        rvMembers.setAdapter(adapter);
    }

    private ArrayList<PersonData> listSearch = new ArrayList<>();
    private void actionSearch() {
        hideKeyBoard();
        listSearch = new ArrayList<>();
        String keyword = etSearch.getText().toString().toLowerCase();
        for(PersonData personData : listMember) {
            if(personData.getFullName().toLowerCase().contains(keyword)) {
                listSearch.add(personData);
            }
        }

        initRecyclerView(listSearch);
    }

    private void checkDepartment(ArrayList<PersonData> listMember, ArrayList<PersonData> listDepartment) {
        for(PersonData department : listDepartment) {
            if(department.getListMembers() != null && department.getListMembers().size() > 0) {
                for(PersonData member : department.getListMembers()) {
                    if(!listMember.contains(member)) {
                        listMember.add(member);
                    }
                }
            }

            if(department.getPersonList() != null && department.getPersonList().size() > 0) {
                checkDepartment(listMember, department.getPersonList());
            }
        }
    }

    public MemberAdapter getAdapter() {
        bindSearchToList();
        return adapter;
    }

}
