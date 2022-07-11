package com.dazone.crewemail.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.data.PersonData;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private ArrayList<PersonData> list;
    private IAllMemberChecked listener;

    public MemberAdapter(ArrayList<PersonData> list, IAllMemberChecked listener) {
        this.list = list;
        this.listener = listener;
    }

    @Override
    public MemberViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_member, viewGroup, false);
        return new MemberAdapter.MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MemberViewHolder memberViewHolder, int i) {
        if(list != null && list.get(i) != null) {
            memberViewHolder.bindData(list.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MemberViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imgAvatar;
        private TextView tvName, tvCompanyName;
        private CheckBox ckMember;
        public MemberViewHolder(View itemView) {
            super(itemView);

            initViews(itemView);
        }

        void bindData(PersonData personData) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(itemView.getContext())
                    .load(DaZoneApplication.getInstance().getPrefs().getServerSite() + personData.getUrlAvatar())
                    .apply(requestOptions)
                    .into(imgAvatar);

            tvName.setText(personData.getFullName());
            tvCompanyName.setText(personData.getPositionName());
            ckMember.setChecked(personData.isCheck());

            ckMember.setOnCheckedChangeListener((buttonView, isChecked) -> {
                personData.setIsCheck(isChecked);

                //checkAllMemberChecked();
            });

        }

        private void initViews(View itemView) {
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            ckMember = itemView.findViewById(R.id.ckMember);
        }

        private void checkAllMemberChecked() {
            boolean isAllChecked = true;
            boolean isAllNonChecked = true;
            for(PersonData personData : list) {
                if(!personData.isCheck()) {
                    isAllChecked = false;
                } else {
                    isAllNonChecked = false;
                }
            }

            if(isAllChecked) {
                listener.onAllMemberChecked();
            }

            if(isAllNonChecked) {
                listener.onAllMemberNonChecked();
            }

        }
    }

    public ArrayList<PersonData> getSelectedList() {
        ArrayList<PersonData> selected = new ArrayList<>();
        for(PersonData personData : list) {
            if(personData.isCheck() && !selected.contains(personData)) {
                selected.add(personData);
            }
        }

        return selected;
    }

    public interface IAllMemberChecked {
        void onAllMemberChecked();
        void onAllMemberNonChecked();
    }
}
