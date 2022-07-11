package com.dazone.crewemail.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dazone.crewemail.R;
import com.dazone.crewemail.data.AttachData;
import com.dazone.crewemail.utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jerry on 11/28/2017.
 */

public class AttachAdapter extends BaseAdapter<AttachData, AttachAdapter.ViewHolder> {


    public AttachAdapter(Activity mActivity) {
        super(mActivity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mail_create_item_attach, parent, false);
        ButterKnife.bind(v);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtMailCreateItemAttachFileName)
        TextView txtMailCreateAttachFileName;

        @BindView(R.id.txtMailCreateItemAttachFileSize)
        TextView txtMailCreateAttachFileSize;

        @BindView(R.id.imgMailCreateItemAttachDownload)
        ImageButton imgMailCreateItemAttachDownload;

        @BindView(R.id.imgMailCreateItemAttachDelete)
        ImageButton imgMailCreateItemAttachDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            imgMailCreateItemAttachDelete.setOnClickListener(view -> {
                onClicklistener.onRemove(getAdapterPosition());
            });

            imgMailCreateItemAttachDownload.setOnClickListener(view -> onClicklistener.onDownload(getAdapterPosition()));
        }

        void bind(int pos) {
            AttachData attachData = list.get(pos);
            txtMailCreateAttachFileName.setText(attachData.getFileName());
            txtMailCreateAttachFileSize.setText(" (" + Util.readableFileSize(attachData.getFileSize()) + ")");
        }
    }

    public interface onClicklistener {
        void onRemove(int pos);

        void onDownload(int pos);
    }

    private onClicklistener onClicklistener;

    public void setOnClicklistener(AttachAdapter.onClicklistener onClicklistener) {
        this.onClicklistener = onClicklistener;
    }
}
