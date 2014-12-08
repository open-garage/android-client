/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 3.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import net.alopix.morannon.GarageApp;
import net.alopix.morannon.R;
import net.alopix.morannon.adapter.OnItemClickListener;
import net.alopix.morannon.adapter.SettingsRecyclerAdapter;
import net.alopix.morannon.api.v1.response.SystemInfosResponse;
import net.alopix.morannon.decoration.DividerItemDecoration;
import net.alopix.morannon.model.SettingItem;
import net.alopix.morannon.popup.PopupResult;
import net.alopix.morannon.popup.SettingsInput;
import net.alopix.morannon.popup.SettingsInputPopup;
import net.alopix.morannon.popup.SettingsInputResult;
import net.alopix.morannon.util.AppUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by dustin on 03.12.2014.
 */
public class SettingsView extends FrameLayout implements OnItemClickListener, PopupResult<SettingsInput, SettingsInputResult> {
    private static final String TAG = SettingsView.class.getSimpleName();

    private SettingsInputPopup mInputPopup;

    private SettingItem mServerNameSetting = new SettingItem("Server Name", "?");
    private SettingItem mServerVersionSetting = new SettingItem("Server Version", "?");

    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    SettingsRecyclerAdapter mAdapter;

    public SettingsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mAdapter = new SettingsRecyclerAdapter();
        mAdapter.setOnItemClickListener(this);

        mAdapter.add(new SettingItem("Open Garage Server URL", ((GarageApp) context.getApplicationContext()).getApiServiceEndpoint(), true));
        mAdapter.add(mServerNameSetting);
        mAdapter.add(mServerVersionSetting);
        mAdapter.add(new SettingItem("App Version", String.format("v%s (build %d)", AppUtil.getVersionName(context), AppUtil.getVersion(context))));

        mInputPopup = new SettingsInputPopup(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                getResources().getDrawable(R.drawable.list_item_divider)));

        loadServerInfo();
    }

    private void loadServerInfo() {
        ((GarageApp) getContext().getApplicationContext()).getApiService().systemInfos(new Callback<SystemInfosResponse>() {
            @Override
            public void success(SystemInfosResponse systemInfosResponse, Response response) {
                Log.d(TAG, "Welcome to the Open Garage System!");

                mServerNameSetting.setDescription(systemInfosResponse.getName());
                mServerVersionSetting.setDescription(systemInfosResponse.getVersion());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Welcome to the Open Garage System (ERROR)!");

                // TODO: display error to the user
            }
        });
    }

    @Override
    public void onItemClicked(View view, int position) {
        SettingItem item = mAdapter.getItem(position);

        if (position == 0) {
            GarageApp app = (GarageApp) getContext().getApplicationContext();
            mInputPopup.show(new SettingsInput(position, app.getApiServiceEndpoint(), app.getApiToken()), this);
        }
    }

    @Override
    public void onPopupResult(SettingsInput settingsInput, SettingsInputResult result) {
        if (result != null) {
            SettingItem item = mAdapter.getItem(settingsInput.id);
            if (settingsInput.id == 0) {
                ((GarageApp) getContext().getApplicationContext()).updateApiServiceEndpoint(result.url, result.token);
            }
            item.setDescription(result.url);
            mAdapter.notifyDataSetChanged();
        }
    }
}
