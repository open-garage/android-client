package net.alopix.morannon.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.dd.CircularProgressButton;

import net.alopix.morannon.GarageApp;
import net.alopix.morannon.R;
import net.alopix.morannon.api.v1.request.DoorStatusRequest;
import net.alopix.morannon.api.v1.response.DoorStatusResponse;
import net.alopix.morannon.service.GarageService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GarageActivity extends AppCompatActivity {
	private static final String TAG = GarageActivity.class.getSimpleName();

	private static final int RESET_BUTTON_TIMEOUT = 3000;

	private Handler mHandler = new Handler();

	private final Runnable mResetButtonCallback = new Runnable() {
		@Override
		public void run() {
			resetButton();
		}
	};

	private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(GarageService.ACTION_TOGGLE_GARAGE_STATUS)) {
				if (intent.hasExtra(GarageService.EXTRA_PROGRESS)) {
					int progress = intent.getIntExtra(GarageService.EXTRA_PROGRESS, GarageService.PROGRESS_IDLE);
					mToggleButton.setProgress(progress);
				}
			}
		}
	};

	@BindView(R.id.door_status_label)
	TextView mDoorStatusLabel;
	@BindView(R.id.toggle_button)
	CircularProgressButton mToggleButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_garage);
		ButterKnife.bind(this);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
	}

	@Override
	protected void onStart() {
		super.onStart();

		final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(GarageService.ACTION_TOGGLE_GARAGE_STATUS);
		broadcastManager.registerReceiver(mBroadcastReceiver, intentFilter);

		loadDoorState();

		mToggleButton.setIndeterminateProgressMode(true);
		mToggleButton.setProgress(((GarageApp) getApplicationContext()).getCurrentProgress());
	}

	@Override
	protected void onPause() {
		super.onPause();

		removeButtonResetHandler();

		final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
		broadcastManager.unregisterReceiver(mBroadcastReceiver);
	}

	private void loadDoorState() {
		mDoorStatusLabel.setText(R.string.door_state_loading);

		((GarageApp) getApplicationContext()).getApiService().doorStatus(new DoorStatusRequest(((GarageApp) getApplicationContext()).getApiToken())).enqueue(new Callback<DoorStatusResponse>() {
			@Override
			public void onResponse(Call<DoorStatusResponse> call, Response<DoorStatusResponse> response) {
				mDoorStatusLabel.setText(getDoorStatusString(response.body().getStatus()));
			}

			@Override
			public void onFailure(Call<DoorStatusResponse> call, Throwable t) {
				mDoorStatusLabel.setText(getDoorStatusString(DoorStatusResponse.STATUS_TOKEN_ERROR));
				startButtonReset();
			}
		});
	}

	private String getDoorStatusString(int status) {
		String statusStr;
		switch (status) {
			case DoorStatusResponse.STATUS_CLOSED:
				statusStr = getString(R.string.door_closed);
				break;

			case DoorStatusResponse.STATUS_OPEN:
				statusStr = getString(R.string.door_open);
				break;

			default:
				statusStr = getString(R.string.door_state_unavailable);
				break;
		}
		return getString(R.string.door_state, statusStr);
	}

	@OnClick(R.id.toggle_button)
	void onToggleClicked() {
		if (mToggleButton.getProgress() > 0) {
			return;
		}

		removeButtonResetHandler();
		resetButton();
		mToggleButton.setProgress(0);
		Intent serviceIntent = new Intent(this, GarageService.class);
		startService(serviceIntent);
	}

	private void removeButtonResetHandler() {
		if (mResetButtonCallback != null) {
			mHandler.removeCallbacks(mResetButtonCallback);
		}
	}

	private void startButtonReset() {
		removeButtonResetHandler();
		mHandler.postDelayed(mResetButtonCallback, RESET_BUTTON_TIMEOUT);
	}

	private void resetButton() {
		mToggleButton.setProgress(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.toggle_view, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_refresh_state:
				loadDoorState();
				return true;

			case R.id.action_settings:
				openSettings();
				return true;

			default:
				return false;
		}
	}

	private void openSettings() {
		startActivity(new Intent(this, SettingsActivity.class));
	}
}
