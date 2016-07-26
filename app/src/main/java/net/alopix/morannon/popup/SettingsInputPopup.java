/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 8.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon.popup;

import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import net.alopix.morannon.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dustin on 08/12/14.
 */
public class SettingsInputPopup {
    private final Context mContext;

    private SettingsInput mInfo;
    private PopupResult<SettingsInput, SettingsInputResult> mPresenter;
    private Dialog mDialog;

    @BindView(R.id.title_label)
    TextView mTitleLabel;
    @BindView(R.id.value_input)
    EditText mValueInput;

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
        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        View view = LayoutInflater.from(mContext).inflate(R.layout.popup_settings_input, null, false);
        ButterKnife.bind(this, view);
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
        if (mInfo.isNumber) {
            mValueInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        mTitleLabel.setText(mInfo.title);
        mValueInput.setText(mInfo.value);
    }

    @OnClick(R.id.cancel_button)
    void onCancelClicked() {
        mPresenter.onPopupResult(mInfo, null);
        dismiss();
    }

    @OnClick(R.id.save_button)
    void onSaveClicked() {
        final String newValue = mValueInput.getText().toString();
        mPresenter.onPopupResult(mInfo, new SettingsInputResult(newValue));
        dismiss();
    }
}
