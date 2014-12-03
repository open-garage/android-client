/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 3.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import net.alopix.morannon.api.v1.OpenGarageApi;
import net.alopix.morannon.util.FlowBundler;

import flow.Backstack;
import retrofit.RestAdapter;

/**
 * Created by P40809 on 01.12.2014.
 */
public class GarageApp extends Application {
    private static final String API_ENDPOINT_KEY = "api_endpoint_key";

    private final FlowBundler mFlowBundler = new FlowBundler(new GsonParceler(new Gson())) {
        @Override
        protected Backstack getColdStartBackstack(@Nullable Backstack restoredBackstack) {
            return restoredBackstack == null ? Backstack.single(new Paths.Toggle()) : restoredBackstack;
        }
    };

    private OpenGarageApi mApiService;

    @Override
    public void onCreate() {
        super.onCreate();

        createApiService();
    }

    private void createApiService() {
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(getApiServiceEndpoint())
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build();
        mApiService = adapter.create(OpenGarageApi.class);
    }

    public FlowBundler getFlowBundler() {
        return mFlowBundler;
    }

    public OpenGarageApi getApiService() {
        return mApiService;
    }

    public String getApiServiceEndpoint() {
        return getPreferences().getString(API_ENDPOINT_KEY, Config.API_ENDPOINT);
    }

    public void updateApiServiceEndpoint(String endpoint) {
        final SharedPreferences prefs = getPreferences();
        prefs.edit().putString(API_ENDPOINT_KEY, endpoint).apply();

        createApiService();
    }

    private SharedPreferences getPreferences() {
        return getSharedPreferences(GarageApp.class.getSimpleName(), MODE_PRIVATE);
    }
}
