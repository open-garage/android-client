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

import okhttp3.Request;
import retrofit2.Response;

/**
 * Created by dustin on 04.12.2014.
 */
public class GarageService extends IntentService {
    private static final String TAG = GarageService.class.getSimpleName();

    public static final String ARG_DIRECTION = "arg_direction";

    public static final String ACTION_TOGGLE_GARAGE_STATUS = "action_toggle_garage_status";
    public static final String EXTRA_PROGRESS = "extra_progress";

    public static final int PROGRESS_IDLE = 0;
    public static final int PROGRESS_LOADING = 50;
    public static final int PROGRESS_SUCCESS = 100;
    public static final int PROGRESS_ERROR = -1;

    /**
     * Creates an IntentService. Invoked by your subclass's constructor.
     */
    public GarageService() {
        super("GarageAppWidgetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: " + intent);

        ToggleRequest.State direction = ToggleRequest.State.values()[intent.getIntExtra(ARG_DIRECTION, ToggleRequest.State.TOGGLE.ordinal())];

        setProgress(PROGRESS_LOADING);
        try {
            Response<ToggleResponse> toggleResponse = ((GarageApp) getApplicationContext()).getApiService().toggle(new ToggleRequest(direction, ((GarageApp) getApplicationContext()).getApiToken())).execute();
            if (toggleResponse == null || !toggleResponse.body().isSuccess()) {
                Log.d(TAG, "Toggle Garage System (ERROR)!");
                setProgress(PROGRESS_ERROR);
            } else {
                Log.d(TAG, "Toggle Open Garage System!");
                setProgress(PROGRESS_SUCCESS);
            }
        } catch (Exception ex) {
            Log.d(TAG, "Toggle Garage System (ERROR)!");
            setProgress(PROGRESS_ERROR);
        }
    }

    @Override
    public void onDestroy() {
        setProgress(PROGRESS_IDLE);

        super.onDestroy();
    }

    private void setProgress(int progress) {
        ((GarageApp) getApplicationContext()).setCurrentProgress(progress);

        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        Intent intent = new Intent(ACTION_TOGGLE_GARAGE_STATUS);
        intent.putExtra(EXTRA_PROGRESS, progress);
        broadcastManager.sendBroadcast(intent);

        String message = GarageAppWidgetProvider.getToggleString(getApplicationContext(), progress);
        boolean loading = progress == PROGRESS_LOADING;

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        ComponentName thisWidget = new ComponentName(getApplicationContext(), GarageAppWidgetProvider.class);
        int[] allAppWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allAppWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.appwidget);
            remoteViews.setTextViewText(R.id.toggle_button, message);
            remoteViews.setViewVisibility(R.id.progress_view, loading ? View.VISIBLE : View.INVISIBLE);

            PendingIntent pendingIntent = null;
            if (!loading) {
                Intent serviceIntent = new Intent(getApplicationContext(), GarageService.class);
                pendingIntent = PendingIntent.getService(getApplicationContext(), 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            remoteViews.setOnClickPendingIntent(R.id.toggle_button, pendingIntent);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }

        if (!loading && progress != PROGRESS_IDLE) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
        }
    }
}
