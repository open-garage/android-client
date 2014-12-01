package net.alopix.morannon;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import net.alopix.morannon.api.v1.request.ToggleRequest;
import net.alopix.morannon.api.v1.response.SystemInfosResponse;
import net.alopix.morannon.api.v1.response.ToggleResponse;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by P40809 on 01.12.2014.
 */
public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((GarageApp) getApplication()).getApiService().systemInfos(new Callback<SystemInfosResponse>() {
            @Override
            public void success(SystemInfosResponse systemInfosResponse, Response response) {
                Log.d(TAG, "Welcome to the Open Garage System!");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Welcome to the Open Garage System (ERROR)!");
            }
        });

        ((GarageApp) getApplication()).getApiService().toggle(new ToggleRequest(), new Callback<ToggleResponse>() {
            @Override
            public void success(ToggleResponse toggleResponse, Response response) {
                Log.d(TAG, "Toggle Open Garage System!");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Toggle Garage System (ERROR)!");
            }
        });
    }
}
