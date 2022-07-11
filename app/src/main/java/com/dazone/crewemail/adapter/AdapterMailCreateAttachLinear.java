package com.dazone.crewemail.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dazone.crewemail.R;
import com.dazone.crewemail.data.AttachData;
import com.dazone.crewemail.utils.Util;

/**
 * Created by THANHTUNG on 18/12/2015.
 */
public class AdapterMailCreateAttachLinear extends LinearLayout {
    public ImageButton imgMailCreateAttachDownload, imgMailCreateAttachDelete;
    public TextView txtMailCreateAttachFileName, txtMailCreateAttachFileSize;
    public ProgressBar progressBar1;
    public int progressbar;

    public int getProgressbar() {
        return progressbar;
    }

    public void setProgressbar(int progressbar) {
        this.progressbar = progressbar;
    }

    public void setImageButtonDelete(ImageButton imgMailCreateAttachDelete) {
        this.imgMailCreateAttachDelete = imgMailCreateAttachDelete;
    }

    public void setImageButtonDownLoad(ImageButton imgMailCreateAttachDownload) {
        this.imgMailCreateAttachDownload = imgMailCreateAttachDownload;
    }

    public ImageButton getImageButtonDelete() {
        return imgMailCreateAttachDelete;
    }

    public ImageButton getImageButtonDownLoad() {
        return imgMailCreateAttachDownload;
    }

    public AdapterMailCreateAttachLinear(Context context, AttachData attachData) {
        super(context);
        init(context, attachData);
    }

    private void init(Context context, AttachData attachData) {
        LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        li.inflate(R.layout.fragment_mail_create_item_attach, this, true);
        this.imgMailCreateAttachDownload = findViewById(R.id.imgMailCreateItemAttachDownload);
        this.imgMailCreateAttachDelete = findViewById(R.id.imgMailCreateItemAttachDelete);
        this.txtMailCreateAttachFileName = findViewById(R.id.txtMailCreateItemAttachFileName);
        this.txtMailCreateAttachFileSize = findViewById(R.id.txtMailCreateItemAttachFileSize);
        this.progressBar1 = findViewById(R.id.progressBar1);
        setImageButtonDelete(imgMailCreateAttachDelete);
        setImageButtonDownLoad(imgMailCreateAttachDownload);
        this.txtMailCreateAttachFileName.setText(attachData.getFileName());
        this.txtMailCreateAttachFileSize.setText(" (" + Util.readableFileSize(attachData.getFileSize()) + ")");
    }

}
