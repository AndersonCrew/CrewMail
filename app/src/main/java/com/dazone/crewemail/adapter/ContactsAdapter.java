package com.dazone.crewemail.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.data.ContactObj;
import com.dazone.crewemail.interfaces.ItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Admin on 8/7/2017.
 */

public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ContactObj> itemList;
    private ContactsAdapter.OnLoadMoreListener onLoadMoreListener;
    private ItemClickListener clickListener;
    //add lines if load more
    private final int VIEW_TYPE_ITEM = 3000;
    private final int VIEW_TYPE_LOADING = 3001;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean isLoading = true;

    /**
     * Class view holder
     */
    public class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMail;
        ImageView mAvatar;
        CheckBox mCheckBox;


        public ContactViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvMail = itemView.findViewById(R.id.tvEmail);
            mAvatar = itemView.findViewById(R.id.imgAvatar);
            mCheckBox = itemView.findViewById(R.id.mCheckBox);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar1);
        }
    }

    /**
     * Load more listener
     */
    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    /**
     * init adapter
     *
     * @param itemList
     */
    public ContactsAdapter(Context context, RecyclerView recyclerView, List<ContactObj> itemList, int typemessage) {
        this.itemList = itemList;
        this.context = context;
        //add lines if load more
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy <= 0) {
                    return;
                }
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_contact, parent, false);
            return new ContactsAdapter.ContactViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
            return new ContactsAdapter.LoadingViewHolder(itemView);
        }
        return null;

    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ContactsAdapter.ContactViewHolder) {
            // view holder
            ContactsAdapter.ContactViewHolder viewHolder = (ContactsAdapter.ContactViewHolder) holder;
            final ContactObj msgResponse = itemList.get(position);

            if (msgResponse.getFirstName() != null && msgResponse.getName() != null)
                viewHolder.tvName.setText(msgResponse.getLastName() + " " + msgResponse.getFirstName());
            viewHolder.tvMail.setText(msgResponse.getEmail());
            String url = DaZoneApplication.getInstance().getPrefs().getServerSite() + msgResponse.getPhoto();
            Picasso.with(context).load(url)
                    .error(R.drawable.avatar_l)
                    .placeholder(R.drawable.avatar_l)
                    .into(viewHolder.mAvatar);
            //check show checkbox
            if (msgResponse.getEmail().trim().equals("") || msgResponse.getEmail() == null) {
                viewHolder.mCheckBox.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.mCheckBox.setVisibility(View.VISIBLE);
            }
            viewHolder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> setOnClick(buttonView, position, isChecked));
        } else if (holder instanceof ContactsAdapter.LoadingViewHolder) {
            // loading view holder
            ContactsAdapter.LoadingViewHolder loadingViewHolder = (ContactsAdapter.LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);

        }
    }

    private void setOnClick(View view, int pos, boolean isCheck) {
        if (clickListener != null) {
            clickListener.onClick(view, pos, isCheck);
        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setOnLoadMoreListener(ContactsAdapter.OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setLoaded() {
        isLoading = false;
    }
}

