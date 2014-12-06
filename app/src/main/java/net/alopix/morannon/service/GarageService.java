/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 4.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import net.alopix.morannon.GarageApp;
import net.alopix.morannon.R;
import net.alopix.morannon.api.v1.request.ToggleRequest;
import net.alopix.morannon.api.v1.response.ToggleResponse;
import net.alopix.morannon.appwidget.GarageAppWidgetProvider;

/**
 * Created by dustin on 04.12.2014.
 */
public class GarageService extends IntentService {
    private static final String TAG = GarageService.class.getSimpleName();

    public static final String ACTION_TOGGLE_GARAGE_STATUS = "action_toggle_garage_status";
    public static final String EXTRA_STATUS = "extra_status";
    public static final int STATUS_IDLE = 0;
    public static final int STATUS_LOADING = 50;
    public static final int STATUS_SUCCESS = 100;
    public static final int STATUS_ERROR = -1;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public GarageService() {
        super("GarageAppWidgetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: " + intent);

        setStatus(STATUS_LOADING);
        try {
            ToggleResponse toggleResponse = ((GarageApp) getApplicationContext()).getApiService().toggleSync(new ToggleRequest());
            Log.d(TAG, "Toggle Open Garage System!");
            setStatus(STATUS_SUCCESS);
        } catch (Exception ex) {
            Log.d(TAG, "Toggle Garage System (ERROR)!");
            setStatus(STATUS_ERROR);
        }
    }

    @Override
    public void onDestroy() {
        setStatus(STATUS_IDLE);

        super.onDestroy();
    }

    private void setStatus(int status) {
        ((GarageApp) getApplicationContext()).setCurrentStatus(status);

        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        Intent intent = new Intent(ACTION_TOGGLE_GARAGE_STATUS);
        intent.putExtra(EXTRA_STATUS, status);
        broadcastManager.sendBroadcast(intent);

        int messageRes = getMessageRes(status);
        boolean loading = status == STATUS_LOADING;

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        ComponentName thisWidget = new ComponentName(getApplicationContext(), GarageAppWidgetProvider.class);
        int[] allAppWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allAppWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.appwidget);
            remoteViews.setTextViewText(R.id.toggle_button, getText(messageRes));
            remoteViews.setViewVisibility(R.id.progress_view, loading ? View.VISIBLE : View.INVISIBLE);

            PendingIntent pendingIntent = null;
            if (!loading) {
                Intent serviceIntent = new Intent(getApplicationContext(), GarageService.class);
                pendingIntent = PendingIntent.getService(getApplicationContext(), 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            remoteViews.setOnClickPendingIntent(R.id.toggle_button, pendingIntent);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }

        if (!loading && status != STATUS_IDLE) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
        }
    }

    private int getMessageRes(int status) {
        switch (status) {
            case STATUS_IDLE:
                return R.string.appwidget_toggle_door_idle;

            case STATUS_LOADING:
                return R.string.appwidget_toggle_door_loading;

            case STATUS_SUCCESS:
                return R.string.appwidget_toggle_door_complete;

            default:
                return R.string.appwidget_toggle_door_error;
        }
    }
}