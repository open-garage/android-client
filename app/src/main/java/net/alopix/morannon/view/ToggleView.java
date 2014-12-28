/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 3.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dd.CircularProgressButton;

import net.alopix.morannon.GarageApp;
import net.alopix.morannon.HandlesOptionsMenu;
import net.alopix.morannon.Paths;
import net.alopix.morannon.R;
import net.alopix.morannon.api.v1.OpenGarageService;
import net.alopix.morannon.api.v1.request.DoorStatusRequest;
import net.alopix.morannon.api.v1.response.DoorStatusResponse;
import net.alopix.morannon.api.v1.response.ToggleResponse;
import net.alopix.morannon.service.GarageService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import flow.Flow;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by dustin on 03.12.2014.
 */
public class ToggleView extends FrameLayout implements HandlesOptionsMenu {
    private static final String TAG = ToggleView.class.getSimpleName();

    private static final int RESET_BUTTON_TIMEOUT = 3000;

    private Handler mHandler = new Handler();

    private final Runnable mResetButtonCallback = new Runnable() {
        @Override
        public void run() {
            resetButton();
        }
    };

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GarageService.ACTION_TOGGLE_GARAGE_STATUS)) {
                if (intent.hasExtra(GarageService.EXTRA_PROGRESS)) {
                    int progress = intent.getIntExtra(GarageService.EXTRA_PROGRESS, GarageService.PROGRESS_IDLE);
                    mToggleButton.setProgress(progress);
                }
            }
        }
    };

    @InjectView(R.id.door_status_label)
    TextView mDoorStatusLabel;
    @InjectView(R.id.toggle_button)
    CircularProgressButton mToggleButton;

    public ToggleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GarageService.ACTION_TOGGLE_GARAGE_STATUS);
        broadcastManager.registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

        loadDoorState();

        mToggleButton.setIndeterminateProgressMode(true);

        mToggleButton.setProgress(((GarageApp) getContext().getApplicationContext()).getCurrentProgress());
    }

    private void loadDoorState() {
        mDoorStatusLabel.setText(R.string.door_state_loading);

        ((GarageApp) getContext().getApplicationContext()).getApiService().doorStatus(new DoorStatusRequest(((GarageApp) getContext().getApplicationContext()).getApiToken()), new Callback<DoorStatusResponse>() {
            @Override
            public void success(DoorStatusResponse statusResponse, Response response) {
                mDoorStatusLabel.setText(getDoorStatusString(statusResponse.getStatus()));
            }

            @Override
            public void failure(RetrofitError error) {
                mDoorStatusLabel.setText(getDoorStatusString(DoorStatusResponse.STATUS_TOKEN_ERROR));
                startButtonReset();
            }
        });
    }

    private String getDoorStatusString(int status) {
        String statusStr;
        switch (status) {
            case DoorStatusResponse.STATUS_CLOSED:
                statusStr = getContext().getString(R.string.door_closed);
                break;

            case DoorStatusResponse.STATUS_OPEN:
                statusStr = getContext().getString(R.string.door_open);
                break;

            default:
                statusStr = getContext().getString(R.string.door_state_unavailable);
                break;
        }
        return getContext().getString(R.string.door_state, statusStr);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        removeButtonResetHandler();

        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());
        broadcastManager.unregisterReceiver(mBroadcastReceiver);
    }

    @OnClick(R.id.toggle_button)
    void onToggleClicked() {
        if (mToggleButton.getProgress() > 0) {
            return;
        }

        removeButtonResetHandler();
        resetButton();
        mToggleButton.setProgress(0);
        Intent serviceIntent = new Intent(getContext(), GarageService.class);
        getContext().startService(serviceIntent);
    }

    private void removeButtonResetHandler() {
        if (mResetButtonCallback != null) {
            mHandler.removeCallbacks(mResetButtonCallback);
        }
    }

    private void startButtonReset() {
        removeButtonResetHandler();
        mHandler.postDelayed(mResetButtonCallback, RESET_BUTTON_TIMEOUT);
    }

    private void resetButton() {
        mToggleButton.setProgress(0);
    }

    @Override
    public boolean onCreateOptionsMenu(MenuInflater menuInflater, Menu menu) {
        menuInflater.inflate(R.menu.toggle_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh_state:
                loadDoorState();
                return true;

            case R.id.action_settings:
                openSettings();
                return true;

            default:
                return false;
        }
    }

    private void openSettings() {
        Flow.get(getContext()).goTo(new Paths.Settings());
    }
}
