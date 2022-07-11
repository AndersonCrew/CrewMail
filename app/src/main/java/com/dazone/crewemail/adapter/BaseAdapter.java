package com.dazone.crewemail.adapter;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harry on 6/7/16.
 */
public abstract class BaseAdapter<T, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {
    private int lastPosition = -1;
    protected Activity mActivity;
    protected List<T> list;
    private boolean enableAnimation;

    public BaseAdapter(Activity mActivity) {
        this.mActivity = mActivity;
        this.list = new ArrayList<>();
        this.enableAnimation = true;
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mActivity, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public void onViewDetachedFromWindow(V holder) {
        if (enableAnimation) {
            holder.itemView.clearAnimation();
        }
    }

    public List<T> getList() {
        return list;
    }

    @Override
    public void onBindViewHolder(V holder, int position) {
        if (enableAnimation) {
            setAnimation(holder.itemView, position);
        }
    }

    public void add(T data) {
        if (data == null) {
            return;
        }
        list.add(data);
        notifyItemInserted(list.size());
    }

    public void add(int position, T data) {
        if (data == null) {
            return;
        }
        if (position < 0) {
            return;
        }
        list.add(position, data);
        notifyItemInserted(list.size());
    }

    public void addAll(List<T> data) {
        if (data == null) {
            return;
        }
        int pos = getItemCount();
        list.addAll(data);
        notifyItemRangeChanged(pos, list.size());
    }

    public void setList(List<T> data){
        this.list = data;
        notifyDataSetChanged();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public Activity getContext() {
        return mActivity;
    }

}
