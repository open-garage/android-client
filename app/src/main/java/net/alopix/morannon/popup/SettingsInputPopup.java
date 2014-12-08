/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 8.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon.popup;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import net.alopix.morannon.GarageApp;
import net.alopix.morannon.R;
import net.alopix.morannon.api.v1.response.SystemInfosResponse;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by dustin on 08/12/14.
 */
public class SettingsInputPopup {
    private final Context mContext;

    private SettingsInput mInfo;
    private PopupResult<SettingsInput, SettingsInputResult> mPresenter;
    private Dialog mDialog;

    @InjectView(R.id.url_input)
    EditText mUrlInput;
    @InjectView(R.id.token_input)
    EditText mTokenInput;

    public SettingsInputPopup(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public void show(SettingsInput info, final PopupResult<SettingsInput, SettingsInputResult> presenter) {
        if (mDialog != null) {
            throw new IllegalStateException("Already showing, can't show " + info);
        }

        mInfo = info;
        mPresenter = presenter;

        mDialog = new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        View view = LayoutInflater.from(mContext).inflate(R.layout.popup_settings_input, null, false);
        ButterKnife.inject(this, view);
        mDialog.setContentView(view);

        updateContent();

        mDialog.show();
    }

    public boolean isShowing() {
        return mDialog != null;
    }

    public void dismiss() {
        mDialog.dismiss();
        mDialog = null;
    }

    private void updateContent() {
        mUrlInput.setText(mInfo.url);
        mTokenInput.setText(mInfo.token);
    }

    @OnClick(R.id.cancel_button)
    void onCancelClicked() {
        mPresenter.onPopupResult(mInfo, null);
        dismiss();
    }

    @OnClick(R.id.save_button)
    void onSaveClicked() {
        final String newUrl = mUrlInput.getText().toString();
        final String newToken = mTokenInput.getText().toString();
        // TODO: loading start

        ((GarageApp) mContext.getApplicationContext()).createApiService(newUrl)
                .systemInfos(new Callback<SystemInfosResponse>() {
                    @Override
                    public void success(SystemInfosResponse systemInfosResponse, Response response) {
                        mPresenter.onPopupResult(mInfo, new SettingsInputResult(newUrl, newToken));
                        dismiss();
                        // TODO: loading end
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // TODO: show error!
                        // TODO: loading end
                    }
                });
    }
}
