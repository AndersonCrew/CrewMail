package com.dazone.crewemail.view;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dazone.crewemail.R;


public class ViewHolderMailCreateAttachRecycle extends RecyclerView.ViewHolder {
    public ImageButton imgMailCreateAttachDownload, imgMailCreateAttachDelete;
    public TextView txtMailCreateAttachFileName, txtMailCreateAttachFileSize;
    public ViewHolderMailCreateAttachRecycle(View itemView) {
        super(itemView);
        this.imgMailCreateAttachDownload = itemView.findViewById(R.id.imgMailCreateItemAttachDownload);
        this.imgMailCreateAttachDelete = itemView.findViewById(R.id.imgMailCreateItemAttachDelete);
        this.txtMailCreateAttachFileName = itemView.findViewById(R.id.txtMailCreateItemAttachFileName);
        this.txtMailCreateAttachFileSize = itemView.findViewById(R.id.txtMailCreateItemAttachFileSize);
    }
}
