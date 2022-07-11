package com.dazone.crewemail.adapter;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.graphics.Color;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.data.ReadDateData;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.Util;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by THANHTUNG on 02/02/2016.
 */
public class ListReadDateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ReadDateData> mMailDataList;
    private boolean mIsProgressOn;
    private Activity mContext;
    private final String mRootLink;
    private static final int FOOTER  = 0;
    private static final int NORMAL = 1;
    private ArrayList<ReadDateData> mSelectedMailList = new ArrayList<>();
    private OnItemSelectListener mOnMailItemSelectListener;

    public ListReadDateAdapter(Fragment fragment, ArrayList<ReadDateData> mailDataList) {
        mMailDataList = mailDataList;
        this.mContext = fragment.getActivity();
        mRootLink = DaZoneApplication.getInstance().getPrefs().getServerSite();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == NORMAL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_mail_new, parent, false);
            return new ItemViewHolder(v);
        }else{
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progress_item, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= getItemCount() - 1) {
            final ProgressViewHolder v = (ProgressViewHolder) holder;
            if (mIsProgressOn) {
                v.progressBar.setVisibility(View.VISIBLE);
            } else {
                v.progressBar.setVisibility(View.GONE);
            }
        } else {
            final ItemViewHolder vh = (ItemViewHolder) holder;
            final ReadDateData readDateData = mMailDataList.get(position);
            vh.mainWrapper.setTag(readDateData);
            vh.mainWrapper.setOnClickListener(v -> runAnimationStateForMail(vh, readDateData, 2));
            vh.mainWrapper.setOnLongClickListener(v -> {
                runAnimationStateForMail(vh, readDateData, 1);
                return true;
            });
            vh.address.setText(readDateData.getAddress());
            vh.name.setText(readDateData.getName());
            vh.date.setTextColor(Color.parseColor(readDateData.getStatusColor()));
            vh.date.setText(readDateData.getStatus());

            if(TextUtils.isEmpty(readDateData.getAvatarUrl()))
            {
            int iconColor = readDateData.getmColor();
            if (iconColor < 1) {
                ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                // generate color based on a key (same key returns the same color), useful for list/grid views
                iconColor = generator.getColor(readDateData.getAddress() + readDateData.getAddress());
                // set to object
                readDateData.setmColor(iconColor);
            }
            TextDrawable drawable;
            drawable= TextDrawable.builder()
                    .buildRound(readDateData.getFirstLetterOfName(), iconColor);
            drawable.setPadding(1, 1, 1, 1);
            vh.avatar.setImageDrawable(drawable);
            }else
            {
                String url = mRootLink+readDateData.getAvatarUrl();
                ImageLoader.getInstance().displayImage(url,vh.avatar, Statics.options2);
            }
        }
    }

    public interface OnItemSelectListener{
        void onItemSelect(ReadDateData mailData);
    }

    public void setOnMailItemSelectListener(OnItemSelectListener listener){
        this.mOnMailItemSelectListener = listener;
    }

    public void ClearListSelect()
    {
        if(mSelectedMailList!=null)
        {
            mSelectedMailList.clear();
            mSelectedMailList = new ArrayList<>();
        }

        isOnTouch = false;
    }

    private void AddOrRemoveMail(ReadDateData mailData)
    {
        if(mSelectedMailList.contains(mailData)){
            // remove if already add selected list
            mSelectedMailList.remove(mailData);
        }else {
            mSelectedMailList.add(mailData);
        }
    }

    private boolean isOnTouch = false;
    public void runAnimationStateForMail(ItemViewHolder vh, ReadDateData mailData, int isTask){
        if(mailData.getCanSentCancel()) {
        if (isTask == 1) {
            isOnTouch = true;
            if (mOnMailItemSelectListener != null) {
                mOnMailItemSelectListener.onItemSelect(null);
            }

        } else {
            if(isOnTouch)
            {
                if (!mailData.isSelected()) {
                    showHideCheckAnimation(vh, true);
                    mailData.setSelected(true);
                } else {
                    showHideCheckAnimation(vh, false);
                    mailData.setSelected(false);
                }
                vh.mainWrapper.setActivated(mailData.isSelected());
                if (mOnMailItemSelectListener != null) {
                    mOnMailItemSelectListener.onItemSelect(mailData);
                }
                AddOrRemoveMail(mailData);
            }

        }
        }else
        {
            Util.showMessage(Util.getString(R.string.string_notice_sent_error));
        }
    }

    public void showHideCheckAnimation(ItemViewHolder vh, boolean isShowCheck){
        if(isShowCheck) {
            final AnimatorSet setRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,
                    R.animator.flip_right);

            final AnimatorSet setLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,
                    R.animator.flip_left_in);
            setRightOut.setTarget(vh.avatar);
            setRightOut.start();
            setLeftIn.setTarget(vh.avatarCheck);
            setLeftIn.start();
        }else{
            final AnimatorSet setRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,
                    R.animator.flip_right_out);

            final AnimatorSet setLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,
                    R.animator.flip_left_in);
            setRightOut.setTarget(vh.avatarCheck);
            setRightOut.start();
            setLeftIn.setTarget(vh.avatar);
            setLeftIn.start();
        }
        vh.mainWrapper.setActivated(isShowCheck);
    }

    @Override
    public int getItemCount() {
        return mMailDataList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position >= getItemCount() -1){
            return FOOTER;
        }else{
            return  NORMAL;
        }
    }

    public void setProgressOn(boolean isOn){
        this.mIsProgressOn = isOn;
        this.notifyItemChanged(getItemCount()-1);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout mainWrapper;
        private ImageView avatar;
        private ImageView avatarCheck;
        private TextView name;
        private TextView address;
        private TextView status;
        private TextView date;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mainWrapper = itemView.findViewById(R.id.item_mail_wrapper);
            avatar = itemView.findViewById(R.id.item_mail_avatar);
            avatarCheck = itemView.findViewById(R.id.item_mail_avatar_check);
            name = itemView.findViewById(R.id.item_mail_name);
            status = itemView.findViewById(R.id.item_mail_status);
            date = itemView.findViewById(R.id.item_mail_date);
            address = itemView.findViewById(R.id.item_mail_address);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder{
        ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

}
