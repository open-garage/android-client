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

import net.alopix.morannon.BuildConfig;
import net.alopix.morannon.GarageApp;
import net.alopix.morannon.R;
import net.alopix.morannon.adapter.OnItemClickListener;
import net.alopix.morannon.adapter.SettingsRecyclerAdapter;
import net.alopix.morannon.api.v1.response.SystemInfosResponse;
import net.alopix.morannon.decoration.DividerItemDecoration;
import net.alopix.morannon.model.ClickableSettingItem;
import net.alopix.morannon.model.InputSettingItem;
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
public class SettingsView extends FrameLayout implements OnItemClickListener {
    private static final String TAG = SettingsView.class.getSimpleName();

    private SettingsInputPopup mInputPopup;

    private SettingItem mServerHostSetting = new ServerHostSettingItem();
    private SettingItem mServerPortSetting = new ServerPortSettingItem();
    private SettingItem mApiTokenSetting = new ApiTokenSettingItem();
    private SettingItem mServerNameSetting = new SettingItem("Server Name", "?");
    private SettingItem mServerVersionSetting = new SettingItem("Server Version", "?");

    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    SettingsRecyclerAdapter mAdapter;

    public SettingsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mAdapter = new SettingsRecyclerAdapter();
        mAdapter.setOnItemClickListener(this);

        GarageApp app = (GarageApp) context.getApplicationContext();

        mServerHostSetting.setDescription(app.getApiServerHost());
        mServerPortSetting.setDescription(String.valueOf(app.getApiServerPort()));
        mApiTokenSetting.setDescription(app.getApiToken());

        mAdapter.add(mServerHostSetting);
        mAdapter.add(mServerPortSetting);
        mAdapter.add(mApiTokenSetting);
        mAdapter.add(mServerNameSetting);
        mAdapter.add(mServerVersionSetting);
        mAdapter.add(new SettingItem("App Version", String.format("v%s (build %d%s)", AppUtil.getVersionName(context), AppUtil.getVersion(context), BuildConfig.DEBUG ? " - Debug Mode" : "")));

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

        if (item instanceof ClickableSettingItem) {
            ((ClickableSettingItem) item).onClicked(position);
        }
    }

    private class ServerHostSettingItem extends InputSettingItem implements PopupResult<SettingsInput, SettingsInputResult> {
        public ServerHostSettingItem() {
            super("Server Host", "");
        }

        @Override
        public void onClicked(int position) {
            mInputPopup.show(new SettingsInput(getTitle(), getDescription()), this);
        }

        @Override
        public void onPopupResult(SettingsInput settingsInput, SettingsInputResult result) {
            if (result != null) {
                ((GarageApp) getContext().getApplicationContext()).setApiServerHost(result.value);
                setDescription(result.value);
                mAdapter.notifyDataSetChanged();

                loadServerInfo();
            }
        }
    }

    private class ServerPortSettingItem extends InputSettingItem implements PopupResult<SettingsInput, SettingsInputResult> {
        public ServerPortSettingItem() {
            super("Server Port", "");
        }

        @Override
        public void onClicked(int position) {
            mInputPopup.show(new SettingsInput(getTitle(), getDescription(), true), this);
        }

        @Override
        public void onPopupResult(SettingsInput settingsInput, SettingsInputResult result) {
            if (result != null) {
                ((GarageApp) getContext().getApplicationContext()).setApiServerPort(Integer.parseInt(result.value));
                setDescription(result.value);
                mAdapter.notifyDataSetChanged();

                loadServerInfo();
            }
        }
    }

    private class ApiTokenSettingItem extends InputSettingItem implements PopupResult<SettingsInput, SettingsInputResult> {
        public ApiTokenSettingItem() {
            super("Api Token", "");
        }

        @Override
        public void onClicked(int position) {
            mInputPopup.show(new SettingsInput(getTitle(), getDescription()), this);
        }

        @Override
        public void onPopupResult(SettingsInput settingsInput, SettingsInputResult result) {
            if (result != null) {
                ((GarageApp) getContext().getApplicationContext()).setApiToken(result.value);
                setDescription(result.value);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
