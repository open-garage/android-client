/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 4.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.RemoteViews;

import net.alopix.morannon.GarageApp;
import net.alopix.morannon.R;
import net.alopix.morannon.api.v1.OpenGarageService;
import net.alopix.morannon.service.GarageService;

/**
 * Created by dustin on 04.12.2014.
 */
public class GarageAppWidgetProvider extends AppWidgetProvider {
    private static final String TAG = GarageAppWidgetProvider.class.getSimpleName();

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        onUpdate(context, appWidgetManager, new int[]{appWidgetId});
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        int status = ((GarageApp) context.getApplicationContext()).getCurrentStatus();
        int progress = ((GarageApp) context.getApplicationContext()).getCurrentProgress();

        boolean loading = progress == GarageService.PROGRESS_LOADING;

        for (int widgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget);

            PendingIntent pendingIntent = null;
            if (!loading) {
                Intent serviceIntent = new Intent(context, GarageService.class);
                pendingIntent = PendingIntent.getService(context, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            remoteViews.setOnClickPendingIntent(R.id.toggle_button, pendingIntent);
            remoteViews.setTextViewText(R.id.toggle_button, getToggleString(context, status, progress));
            remoteViews.setViewVisibility(R.id.progress_view, loading ? View.VISIBLE : View.INVISIBLE);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    @StringRes
    private static int getToggleStringRes(@Nullable Integer status, int progress) {
        switch (progress) {
            case GarageService.PROGRESS_IDLE:
                if (status != null) {
                    switch (status) {
                        case OpenGarageService.DOOR_OPEN:
                            return R.string.appwidget_toggle_door_idle_close;

                        case OpenGarageService.DOOR_CLOSED:
                            return R.string.appwidget_toggle_door_idle_open;
                    }
                }
                return R.string.appwidget_toggle_door_idle;

            case GarageService.PROGRESS_LOADING:
                return R.string.appwidget_toggle_door_loading;

            case GarageService.PROGRESS_SUCCESS:
                return R.string.appwidget_toggle_door_complete;

            default:
                return R.string.appwidget_toggle_door_error;
        }
    }

    public static String getToggleString(@NonNull Context context, @Nullable @StringRes Integer status, @StringRes int progress) {
        return context.getString(getToggleStringRes(status, progress));
    }
}
