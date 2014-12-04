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
import android.view.View;
import android.widget.RemoteViews;

import net.alopix.morannon.GarageApp;
import net.alopix.morannon.R;
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

        boolean loading = status == GarageService.STATUS_LOADING;

        for (int widgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget);

            PendingIntent pendingIntent = null;
            if (!loading) {
                Intent serviceIntent = new Intent(context, GarageService.class);
                pendingIntent = PendingIntent.getService(context, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            remoteViews.setOnClickPendingIntent(R.id.toggle_button, pendingIntent);
            remoteViews.setViewVisibility(R.id.progress_view, loading ? View.VISIBLE : View.INVISIBLE);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
