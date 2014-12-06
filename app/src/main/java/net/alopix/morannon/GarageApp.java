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
import com.squareup.okhttp.OkHttpClient;

import net.alopix.morannon.api.v1.OpenGarageApi;
import net.alopix.morannon.service.GarageService;
import net.alopix.morannon.util.FlowBundler;
import net.alopix.util.SelfSignedCertHelper;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import flow.Backstack;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by dustin on 01.12.2014.
 */
public class GarageApp extends Application {
    private static final String API_ENDPOINT_KEY = "api_endpoint_key";
    private static final String CURRENT_STATUS_KEY = "current_status_key";

    private static final int HTTP_TIMEOUT = 15;

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
        OkHttpClient client = configureClient();

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(getApiServiceEndpoint())
                .setClient(new OkClient(client))
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build();
        mApiService = adapter.create(OpenGarageApi.class);
    }

    private OkHttpClient configureClient() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS);
        client.setReadTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS);
        SSLSocketFactory sslSocketFactory = SelfSignedCertHelper.getPinnedCertSslSocketFactory(getResources().openRawResource(R.raw.skynet), Config.TRUST_STORE_PASSWORD);
        if (sslSocketFactory != null) {
            client.setSslSocketFactory(sslSocketFactory);
        }
        return client;
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

    public int getCurrentStatus() {
        return getPreferences().getInt(CURRENT_STATUS_KEY, GarageService.STATUS_IDLE);
    }

    public void setCurrentStatus(int status) {
        getPreferences().edit().putInt(CURRENT_STATUS_KEY, status).apply();
    }

    private SharedPreferences getPreferences() {
        return getSharedPreferences(GarageApp.class.getSimpleName(), MODE_PRIVATE);
    }
}
