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
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import net.alopix.morannon.api.v1.OpenGarageService;
import net.alopix.morannon.api.v1.request.ToggleRequest;
import net.alopix.morannon.service.GarageService;
import net.alopix.morannon.util.FlowBundler;
import net.alopix.util.SelfSignedCertHelper;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import flow.Backstack;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;


/**
 * Created by dustin on 01.12.2014.
 */
public class GarageApp extends Application implements BootstrapNotifier, BeaconConsumer {
    private static final String TAG = GarageApp.class.getSimpleName();

    private static final String API_SERVER_HOST_KEY = "api_server_host_key";
    private static final String API_SERVER_PORT_KEY = "api_server_port_key";
    private static final String API_TOKEN_KEY = "api_token_key";

    private static final String DEFAULT_SERVER_HOST = "localhost";
    private static final int DEFAULT_SERVER_PORT = 8000;
    private static final String DEFAULT_API_TOKEN = "A";

    private static final String CURRENT_PROGRESS_KEY = "current_progress_key";

    private static final int HTTP_TIMEOUT = 15;

    private final FlowBundler mFlowBundler = new FlowBundler(new GsonParceler(new Gson())) {
        @Override
        protected Backstack getColdStartBackstack(@Nullable Backstack restoredBackstack) {
            return restoredBackstack == null ? Backstack.single(new Paths.Toggle()) : restoredBackstack;
        }
    };

    private OpenGarageService mApiService;

    private BeaconManager mBeaconManager;
    private BackgroundPowerSaver mBeaconPowerSaver;

    @Override
    public void onCreate() {
        super.onCreate();

        createApiService();
        initBeaconManager();
    }

    public OpenGarageService createApiService(String endpoint) {
        OkHttpClient client = configureClient();

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setClient(new OkClient(client))
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setConverter(new GsonConverter(new GsonBuilder()
                        .registerTypeAdapter(ToggleRequest.State.class, new ToggleRequest.StateSerializer())
                        .create()))
                .build();
        return adapter.create(OpenGarageService.class);
    }

    private void createApiService() {
        mApiService = createApiService("https://" + getApiServerHost() + ":" + getApiServerPort());
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

    private void initBeaconManager() {
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        mBeaconManager.bind(this);

        mBeaconPowerSaver = new BackgroundPowerSaver(this);
    }

    public FlowBundler getFlowBundler() {
        return mFlowBundler;
    }

    public OpenGarageService getApiService() {
        return mApiService;
    }

    public String getApiServerHost() {
        return getPreferences().getString(API_SERVER_HOST_KEY, DEFAULT_SERVER_HOST);
    }

    public int getApiServerPort() {
        return getPreferences().getInt(API_SERVER_PORT_KEY, DEFAULT_SERVER_PORT);
    }

    public String getApiToken() {
        return getPreferences().getString(API_TOKEN_KEY, DEFAULT_API_TOKEN);
    }

    public void setApiServerHost(String host) {
        getPreferences().edit()
                .putString(API_SERVER_HOST_KEY, host)
                .apply();

        createApiService();
    }

    public void setApiServerPort(int port) {
        getPreferences().edit()
                .putInt(API_SERVER_PORT_KEY, port)
                .apply();

        createApiService();
    }

    public void setApiToken(String token) {
        getPreferences().edit()
                .putString(API_TOKEN_KEY, token)
                .apply();

        createApiService();
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

    @Override
    public void onBeaconServiceConnect() {
        Log.d(TAG, "onBeaconServiceConnected");

        mBeaconManager.setMonitorNotifier(this);
        try {
            mBeaconManager.startMonitoringBeaconsInRegion(new Region("OpenGarageMonitor", null, null, null));
        } catch (Exception ex) {
            Log.d(TAG, "Could not start monitoring", ex);
        }
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.i(TAG, "didEnterRegion: " + region);
    }

    @Override
    public void didExitRegion(Region region) {
        Log.i(TAG, "didExitRegion: " + region);
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        Log.i(TAG, "didDetermineStateForRegion: " + i + ", " + region);
    }
}
