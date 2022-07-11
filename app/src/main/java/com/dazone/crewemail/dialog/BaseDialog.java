package com.dazone.crewemail.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.dazone.crewemail.R;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;


/**
 * Created by mb on 4/5/16.
 */
public class BaseDialog extends Dialog {
    private static final int TIME_START_ANIMATION = 200;
    private static final int TIME_END_ANIMATION = 200;

    private static final double TENSION = 800;
    private static final double DAMPER = 40;

    public interface OnCloseDialog {
        void onPositive();

        void onNegative();

        void onClose();
    }

    private OnCloseDialog listener;
    private SpringSystem springSystem;
    private Spring mSpring;

    private static Handler handler = new Handler();
    private View dialogContent;

    public BaseDialog(Context context) {
        super(context, R.style.NYP_Dialog);
        springSystem = SpringSystem.create();
        mSpring = springSystem.createSpring();

        SpringConfig config = new SpringConfig(TENSION, DAMPER);
        mSpring.setSpringConfig(config);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void show() {
        super.show();

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = displayMetrics.widthPixels;
        lp.height = displayMetrics.heightPixels;
        getWindow().setAttributes(lp);

        mSpring.setEndValue(-dialogContent.getHeight() * 1.1);

        handler.postDelayed(() -> {
            if (dialogContent != null && mSpring != null) {
                dialogContent.setVisibility(View.VISIBLE);
                mSpring.setEndValue(dialogContent.getY());
            }
        }, TIME_START_ANIMATION);

        mSpring.addListener(new SpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float value = (float) spring.getCurrentValue();
                dialogContent.setY(value);
            }

            @Override
            public void onSpringAtRest(Spring spring) {

            }

            @Override
            public void onSpringActivate(Spring spring) {

            }

            @Override
            public void onSpringEndStateChange(Spring spring) {

            }
        });
    }

    @Override
    public void dismiss() {
        mSpring.setEndValue(-dialogContent.getHeight() * 1.1);
        handler.postDelayed(() -> {
            BaseDialog.super.dismiss();
            if (listener != null) {
                listener.onClose();
            }
        }, TIME_END_ANIMATION);
    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(layoutResID, null, false);
        setContentView(view);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ViewGroup viewGroup = (ViewGroup) view;
        if (viewGroup.getChildCount() > 0) {
            dialogContent = viewGroup.getChildAt(0);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ViewGroup viewGroup = (ViewGroup) view;
        if (viewGroup.getChildCount() > 0) {
            dialogContent = viewGroup.getChildAt(0);
        }
    }

    public OnCloseDialog getListener() {
        return listener;
    }
}
