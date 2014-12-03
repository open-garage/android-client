package net.alopix.morannon.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.alopix.morannon.R;
import net.alopix.morannon.model.SettingItem;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by dustin on 03.12.2014.
 */
public class SettingsRecyclerAdapter extends ArrayRecycleAdapter<SettingItem, SettingsRecyclerAdapter.ViewHolder> {
    private static final int TYPE_CLICKABLE = 0;
    private static final int TYPE_DEFAULT = 1;

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
        return getItem(position).isClickable() ? TYPE_CLICKABLE : TYPE_DEFAULT;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(getItemViewType(i) == TYPE_DEFAULT ? R.layout.list_item_setting : R.layout.list_item_clickable_setting, viewGroup, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final SettingItem item = getItem(i);

        viewHolder.title.setText(item.getTitle());
        viewHolder.description.setText(item.getDescription());
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SettingsRecyclerAdapter mAdapter;

        @InjectView(R.id.title_label)
        TextView title;

        @InjectView(R.id.description_label)
        TextView description;

        public ViewHolder(View itemView, SettingsRecyclerAdapter adapter) {
            super(itemView);
            ButterKnife.inject(this, itemView);
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
