package com.dazone.crewemail.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dazone.crewemail.R;
import com.dazone.crewemail.data.PersonData;

import java.util.ArrayList;

public class AdapterOrganization extends RecyclerView.Adapter<AdapterOrganization.OrganizationViewHolder> {

    private ArrayList<PersonData> list = new ArrayList<>();

    public AdapterOrganization(ArrayList<PersonData> listData) {
        list.clear();
        list.addAll(listData);
    }

    @Override
    public OrganizationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_organization_company, viewGroup, false);
        return new OrganizationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrganizationViewHolder organizationViewHolder, int i) {
        if (list != null && list.get(i) != null) {

            PersonData personData = list.get(i);
            organizationViewHolder.tvOrganizationName.setText(personData.getFullName());
            organizationViewHolder.ckDepartment.setChecked(personData.isCheck());
            if (personData.getPersonList() != null && personData.getPersonList().size() > 0) {
                organizationViewHolder.rvChildOrganization.setAdapter(new AdapterOrganization(personData.getPersonList()));
            }

            if (personData.getListMembers() != null && personData.getListMembers().size() > 0) {
                MemberAdapter memberAdapter = new MemberAdapter(personData.getListMembers(), new MemberAdapter.IAllMemberChecked() {
                    @Override
                    public void onAllMemberChecked() {


                    }

                    @Override
                    public void onAllMemberNonChecked() {

                    }
                });

                organizationViewHolder.rvMembers.setAdapter(memberAdapter);
            }

            organizationViewHolder.ckDepartment.setOnCheckedChangeListener((buttonView, isChecked) -> {
                personData.setIsCheck(isChecked);
                if(personData.getListMembers() != null && personData.getListMembers().size() > 0) {
                    setCheckMemberList(personData.getListMembers(), isChecked);
                }

                if (personData.getPersonList() != null && personData.getPersonList().size() > 0) {
                    setCheckDepartment(personData.getPersonList(), isChecked);
                }

                organizationViewHolder.itemView.post(new Runnable()
                {
                    @Override
                    public void run() {
                        notifyItemChanged(i);
                    }
                });

            });
        }
    }

    private void setCheckMemberList(ArrayList<PersonData> list, boolean isCheck) {
        for(PersonData member : list) {
            member.setIsCheck(isCheck);
        }
    }


    private void setCheckDepartment(ArrayList<PersonData> list, boolean isCheck) {
        for(PersonData member : list) {
            member.setIsCheck(isCheck);

            if(member.getPersonList() != null && member.getPersonList().size() > 0) {
                setCheckDepartment(member.getPersonList(), isCheck);
            }

            if(member.getListMembers() != null && member.getListMembers().size() > 0) {
                setCheckMemberList(member.getListMembers(), isCheck);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class OrganizationViewHolder extends RecyclerView.ViewHolder {

        private TextView tvOrganizationName;
        private RecyclerView rvChildOrganization, rvMembers;
        private CheckBox ckDepartment;

        public OrganizationViewHolder(View itemView) {
            super(itemView);
            initViews(itemView);
        }

        private void initViews(View itemView) {
            tvOrganizationName = itemView.findViewById(R.id.tvOrganizationName);
            rvChildOrganization = itemView.findViewById(R.id.rvChildOrganization);
            rvMembers = itemView.findViewById(R.id.rvMembers);
            ckDepartment = itemView.findViewById(R.id.ckDepartment);
        }
    }

    public ArrayList<PersonData> getListData() {
        return list;
    }

    public ArrayList<PersonData> getListSelected() {
        ArrayList<PersonData> selected = new ArrayList<>();
        for(PersonData personData : list) {
            if(personData.getListMembers() != null && personData.getListMembers().size() > 0) {
                for(PersonData member : personData.getListMembers()) {
                    if(member.isCheck()) {
                        selected.add(member);
                    }
                }
            }

            if(personData.getPersonList() != null && personData.getPersonList().size() > 0) {
                for(PersonData department : personData.getPersonList()) {
                    checkEachDepartment(department, selected);

                }
            }
        }

        return selected;
    }

    private void checkEachDepartment(PersonData department, ArrayList<PersonData> selected) {
        if(department.getListMembers() != null && department.getListMembers().size() > 0) {
            for(PersonData member : department.getListMembers()) {
                if(member.isCheck()) {
                    selected.add(member);
                }
            }
        }

        if(department.getPersonList() != null && department.getPersonList().size() > 0) {
            for(PersonData ChildDepartment : department.getPersonList()) {
                checkEachDepartment(ChildDepartment, selected);
            }
        }
    }
}
