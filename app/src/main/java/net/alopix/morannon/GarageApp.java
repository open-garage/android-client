package net.alopix.morannon;

import android.app.Application;

import net.alopix.morannon.api.v1.OpenGarageApi;

import retrofit.RestAdapter;

/**
 * Created by P40809 on 01.12.2014.
 */
public class GarageApp extends Application {
    private OpenGarageApi mApiService;

    @Override
    public void onCreate() {
        super.onCreate();

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(Config.API_ENDPOINT)
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build();
        mApiService = adapter.create(OpenGarageApi.class);
    }

    public OpenGarageApi getApiService() {
        return mApiService;
    }
}
