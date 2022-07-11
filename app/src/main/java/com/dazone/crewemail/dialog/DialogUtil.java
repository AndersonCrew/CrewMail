package com.dazone.crewemail.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dazone.crewemail.R;


public class DialogUtil {
    public interface OnAlertDialogViewClickEvent {
        void onOkClick(DialogInterface alertDialog);

        void onCancelClick();
    }

    /**
     * DISPLAY DIALOG ONE OPTION
     */

    public static void customAlertDialog(final Activity context, String message, String okButton, String noButton, final OnAlertDialogViewClickEvent clickEvent) {
        // Build an AlertDialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        // Set the custom layout as alert dialog view
        builder.setView(dialogView);

        // Get the custom alert dialog view widgets reference
        Button btnYes = dialogView.findViewById(R.id.btn_yes);
        Button btnNo = dialogView.findViewById(R.id.btn_no);
        TextView txtContent = dialogView.findViewById(R.id.txt_dialog_content);

        btnNo.setText(noButton);
        if (noButton == null) {
            btnNo.setVisibility(View.GONE);
        } else
            btnYes.setText(okButton);
        txtContent.setText(message);

        final android.app.AlertDialog dialog = builder.create();

        btnYes.setOnClickListener(v -> {
            if (clickEvent != null) {
                clickEvent.onOkClick(dialog);
            }
            dialog.dismiss();
        });

        btnNo.setOnClickListener(v -> {
            if (clickEvent != null) {
                clickEvent.onCancelClick();
            }
            dialog.cancel();
        });

        // Display the custom alert dialog on interface
        dialog.show();

    }

    public static void oneButtonAlertDialog(final Activity context, String title, String message, String okButton) {
        // Build an AlertDialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);

        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        // Set the custom layout as alert dialog view
        builder.setView(dialogView);

        // Get the custom alert dialog view widgets reference
        Button btn_positive = dialogView.findViewById(R.id.btn_yes);
        Button btn_negative = dialogView.findViewById(R.id.btn_no);
        TextView txtTitle = dialogView.findViewById(R.id.txt_dialog_title);
        TextView txtContent = dialogView.findViewById(R.id.txt_dialog_content);

        btn_negative.setVisibility(View.GONE);
        btn_positive.setText(okButton);

        txtTitle.setText(title);
        txtContent.setText(message);


        // Create the alert dialog
        final android.app.AlertDialog dialog = builder.create();

        // Set negative/no button click listener
        btn_positive.setOnClickListener(v -> {
            dialog.cancel();
        });

        // Display the custom alert dialog on interface
        if(!context.isFinishing()) {
            dialog.show();
        }
    }
}