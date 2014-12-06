/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 3.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by dustin on 01.12.2014.
 */
public abstract class ArrayRecycleAdapter<T, ViewHolder extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<ViewHolder> {
    private List<T> mItems;

    public ArrayRecycleAdapter() {
        super();

        mItems = new ArrayList<>();
    }

    public void add(T item) {
        mItems.add(item);
        notifyDataSetChanged();
    }

    public void add(int position, T item) {
        mItems.add(position, item);
        notifyDataSetChanged();
    }

    public void addAll(Collection<T> items) {
        if (items != null) {
            mItems.addAll(items);
            notifyDataSetChanged();
        }
    }

    public void setItems(Collection<T> items) {
        clear();
        addAll(items);
    }

    public T getItem(int position) {
        return mItems.get(position);
    }

    public List<T> getItems() {
        return Collections.unmodifiableList(mItems);
    }

    public T removeItem(int position) {
        T item = mItems.remove(position);
        notifyDataSetChanged();
        return item;
    }

    public void clear() {
        if (mItems.size() > 0) {
            mItems.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
