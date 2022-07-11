package com.dazone.crewemail.customviews;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.dazone.crewemail.R;

public class AlertDialogView {
    public static void normalAlertDialog(final Context context, String title, String message, String okButton, final OnAlertDialogViewClickEvent clickEvent) {
        AlertDialog mAlertDialog;
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(okButton, (dialog, which) -> {
            if (clickEvent != null) {
                clickEvent.onOkClick(dialog);
            } else {
                dialog.dismiss();
            }
        });
        mAlertDialog = alertDialog.create();
        if (mAlertDialog != null && mAlertDialog.isShowing()) return;
        if (!mAlertDialog.isShowing()) {
            mAlertDialog.show();
        }
    }

    public static void normalAlertDialogNotBack(final Context context, String title, String message, String okButton, String noButton, final OnAlertDialogViewClickEvent clickEvent) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(okButton, (dialog, which) -> {
            if (clickEvent != null) {
                clickEvent.onOkClick(dialog);
            } else {
                dialog.dismiss();
            }
        });

        alertDialog.setNegativeButton(noButton, (dialog, which) -> {
            if (clickEvent != null) {
                clickEvent.onCancelClick();
            } else {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public static void normalAlertDialogWithCancel(final Context context, String title, String message, String okButton, String noButton, final OnAlertDialogViewClickEvent clickEvent) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setNegativeButton(noButton, (dialog, which) -> {
            if (clickEvent != null) {
                clickEvent.onCancelClick();
            }
            dialog.cancel();
        });
        alertDialog.setPositiveButton(okButton, (dialog, which) -> {
            if (clickEvent != null) {
                clickEvent.onOkClick(dialog);
            } else {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public interface OnAlertDialogViewClickEvent {
        void onOkClick(DialogInterface alertDialog);

        void onCancelClick();
    }
}
