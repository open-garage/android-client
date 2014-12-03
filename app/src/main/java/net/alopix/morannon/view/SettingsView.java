package net.alopix.morannon.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import net.alopix.morannon.GarageApp;
import net.alopix.morannon.api.v1.response.SystemInfosResponse;

import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by P40809 on 03.12.2014.
 */
public class SettingsView extends FrameLayout {
    private static final String TAG = SettingsView.class.getSimpleName();

    public SettingsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

        loadServerInfo();
        // TODO: display server info and let user change server url

    }

    private void loadServerInfo() {
        ((GarageApp) getContext().getApplicationContext()).getApiService().systemInfos(new Callback<SystemInfosResponse>() {
            @Override
            public void success(SystemInfosResponse systemInfosResponse, Response response) {
                Log.d(TAG, "Welcome to the Open Garage System!");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Welcome to the Open Garage System (ERROR)!");
            }
        });
    }
}
