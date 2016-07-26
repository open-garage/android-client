/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 3.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.alopix.morannon.R;
import net.alopix.morannon.model.ClickableSettingItem;
import net.alopix.morannon.model.SettingItem;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created by dustin on 03.12.2014.
 */
public class SettingsRecyclerAdapter extends ArrayRecycleAdapter<SettingItem, SettingsRecyclerAdapter.ViewHolder> {
    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_CLICKABLE = 1;

    private OnItemClickListener mOnItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    protected void notifyItemClicked(View view, int position) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClicked(view, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        SettingItem item = getItem(position);
        if (item instanceof ClickableSettingItem) {
            return TYPE_CLICKABLE;
        } else {
            return TYPE_DEFAULT;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        int layoutRes;
        switch (getItemViewType(i)) {
            case TYPE_DEFAULT:
                layoutRes = R.layout.list_item_clickable_setting;
                break;

            default:
                layoutRes = R.layout.list_item_setting;
                break;
        }
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutRes, viewGroup, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final SettingItem item = getItem(i);

        viewHolder.title.setText(item.getTitle());
        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            viewHolder.description.setVisibility(View.GONE);
        } else {
            viewHolder.description.setText(item.getDescription());
            viewHolder.description.setVisibility(View.VISIBLE);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SettingsRecyclerAdapter mAdapter;

        @BindView(R.id.title_label)
        TextView title;
        @BindView(R.id.description_label)
        TextView description;

        public ViewHolder(View itemView, SettingsRecyclerAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mAdapter = adapter;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getItemViewType() == TYPE_DEFAULT) {
                return;
            }

            mAdapter.notifyItemClicked(v, getPosition());
        }
    }
}
