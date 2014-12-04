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
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.dd.CircularProgressButton;

import net.alopix.morannon.GarageApp;
import net.alopix.morannon.HandlesOptionsMenu;
import net.alopix.morannon.Paths;
import net.alopix.morannon.R;
import net.alopix.morannon.service.GarageService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import flow.Flow;

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
                mToggleButton.setProgress(intent.getIntExtra(GarageService.EXTRA_STATUS, 0));
            }
        }
    };

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

        mToggleButton.setIndeterminateProgressMode(true);

        mToggleButton.setProgress(((GarageApp) getContext().getApplicationContext()).getCurrentStatus());
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
//
//        mToggleButton.setProgress(50);
//        ((GarageApp) getContext().getApplicationContext()).getApiService().toggle(new ToggleRequest(), new Callback<ToggleResponse>() {
//            @Override
//            public void success(ToggleResponse toggleResponse, Response response) {
//                Log.d(TAG, "Toggle Open Garage System!");
//                mToggleButton.setProgress(100);
//                startButtonReset();
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                Log.d(TAG, "Toggle Garage System (ERROR)!");
//                mToggleButton.setProgress(-1);
//                startButtonReset();
//            }
//        });
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
