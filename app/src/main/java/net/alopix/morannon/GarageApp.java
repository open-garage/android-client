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

import net.alopix.morannon.api.v1.OpenGarageService;
import net.alopix.morannon.service.GarageService;
import net.alopix.morannon.util.FlowBundler;
import net.alopix.util.SelfSignedCertHelper;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import flow.Backstack;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by dustin on 01.12.2014.
 */
public class GarageApp extends Application {
    private static final String API_ENDPOINT_KEY = "api_endpoint_key";
    private static final String API_TOKEN_KEY = "api_token_key";

    private static final String CURRENT_PROGRESS_KEY = "current_progress_key";
    private static final String CURRENT_STATUS_KEY = "current_status_key";

    private static final int HTTP_TIMEOUT = 15;

    private final FlowBundler mFlowBundler = new FlowBundler(new GsonParceler(new Gson())) {
        @Override
        protected Backstack getColdStartBackstack(@Nullable Backstack restoredBackstack) {
            return restoredBackstack == null ? Backstack.single(new Paths.Toggle()) : restoredBackstack;
        }
    };

    private OpenGarageService mApiService;
    private String mApiToken;

    @Override
    public void onCreate() {
        super.onCreate();

        createApiService();
    }

    public OpenGarageService createApiService(String endpoint) {
        OkHttpClient client = configureClient();

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setClient(new OkClient(client))
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build();
        return adapter.create(OpenGarageService.class);
    }

    private void createApiService() {
        mApiService = createApiService(getApiServiceEndpoint());
        mApiToken = getPreferences().getString(API_TOKEN_KEY, Config.API_TOKEN);
    }

    private OkHttpClient configureClient() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS);
        client.setReadTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS);
        SSLSocketFactory sslSocketFactory = SelfSignedCertHelper.getPinnedCertSslSocketFactory(getResources().openRawResource(R.raw.skynet), Config.TRUST_STORE_PASSWORD);
        if (sslSocketFactory != null) {
            client.setSslSocketFactory(sslSocketFactory);
        }
        client.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        return client;
    }

    public FlowBundler getFlowBundler() {
        return mFlowBundler;
    }

    public OpenGarageService getApiService() {
        return mApiService;
    }

    public String getApiServiceEndpoint() {
        return getPreferences().getString(API_ENDPOINT_KEY, Config.API_ENDPOINT);
    }

    public void updateApiServiceEndpoint(String endpoint, String token) {
        final SharedPreferences prefs = getPreferences();
        prefs.edit()
                .putString(API_ENDPOINT_KEY, endpoint)
                .putString(API_TOKEN_KEY, token)
                .apply();

        createApiService();
    }

    public String getApiToken() {
        return mApiToken;
    }

    public int getCurrentStatus() {
        return getPreferences().getInt(CURRENT_STATUS_KEY, GarageService.PROGRESS_IDLE);
    }

    public void setCurrentStatus(int status) {
        getPreferences().edit().putInt(CURRENT_STATUS_KEY, status).apply();
    }

    public int getCurrentProgress() {
        return getPreferences().getInt(CURRENT_PROGRESS_KEY, GarageService.PROGRESS_IDLE);
    }

    public void setCurrentProgress(int progress) {
        getPreferences().edit().putInt(CURRENT_PROGRESS_KEY, progress).apply();
    }

    private SharedPreferences getPreferences() {
        return getSharedPreferences(GarageApp.class.getSimpleName(), MODE_PRIVATE);
    }
}
