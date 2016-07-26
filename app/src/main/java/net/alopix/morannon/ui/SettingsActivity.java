package net.alopix.morannon.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import net.alopix.morannon.BuildConfig;
import net.alopix.morannon.GarageApp;
import net.alopix.morannon.R;
import net.alopix.morannon.adapter.OnItemClickListener;
import net.alopix.morannon.adapter.SettingsRecyclerAdapter;
import net.alopix.morannon.api.v1.response.SystemInfosResponse;
import net.alopix.morannon.decoration.DividerItemDecoration;
import net.alopix.morannon.model.ClickableSettingItem;
import net.alopix.morannon.model.InputSettingItem;
import net.alopix.morannon.model.SettingItem;
import net.alopix.morannon.popup.PopupResult;
import net.alopix.morannon.popup.SettingsInput;
import net.alopix.morannon.popup.SettingsInputPopup;
import net.alopix.morannon.popup.SettingsInputResult;
import net.alopix.morannon.util.AppUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity implements OnItemClickListener {
	private static final String TAG = SettingsActivity.class.getSimpleName();

	private SettingsInputPopup mInputPopup;

	private SettingItem mServerHostSetting = new ServerHostSettingItem();
	private SettingItem mServerPortSetting = new ServerPortSettingItem();
	private SettingItem mApiTokenSetting = new ApiTokenSettingItem();
	private SettingItem mServerNameSetting = new SettingItem("Server Name", "?");
	private SettingItem mServerVersionSetting = new SettingItem("Server Version", "?");

	@BindView(R.id.recycler_view)
	RecyclerView mRecyclerView;
	SettingsRecyclerAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		ButterKnife.bind(this);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		mAdapter = new SettingsRecyclerAdapter();
		mAdapter.setOnItemClickListener(this);

		GarageApp app = (GarageApp) getApplicationContext();

		mServerHostSetting.setDescription(app.getApiServerHost());
		mServerPortSetting.setDescription(String.valueOf(app.getApiServerPort()));
		mApiTokenSetting.setDescription(app.getApiToken());

		mAdapter.add(mServerHostSetting);
		mAdapter.add(mServerPortSetting);
		mAdapter.add(mApiTokenSetting);
		mAdapter.add(mServerNameSetting);
		mAdapter.add(mServerVersionSetting);
		mAdapter.add(new SettingItem("App Version", String.format("v%s (build %d%s)", AppUtil.getVersionName(this), AppUtil.getVersion(this), BuildConfig.DEBUG ? " - Debug Mode" : "")));

		mInputPopup = new SettingsInputPopup(this);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.addItemDecoration(new DividerItemDecoration(
				getResources().getDrawable(R.drawable.list_item_divider)));
	}

	@Override
	protected void onStart() {
		super.onStart();

		loadServerInfo();
	}

	private void loadServerInfo() {
		((GarageApp) getApplicationContext()).getApiService().systemInfos().enqueue(new Callback<SystemInfosResponse>() {
			@Override
			public void onResponse(Call<SystemInfosResponse> call, Response<SystemInfosResponse> response) {
				Log.d(TAG, "Welcome to the Open Garage System!");

				mServerNameSetting.setDescription(response.body().getName());
				mServerVersionSetting.setDescription(response.body().getVersion());
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure(Call<SystemInfosResponse> call, Throwable t) {
				Log.d(TAG, "Welcome to the Open Garage System (ERROR)!");

				// TODO: display error to the user
			}
		});
	}

	@Override
	public void onItemClicked(View view, int position) {
		SettingItem item = mAdapter.getItem(position);

		if (item instanceof ClickableSettingItem) {
			((ClickableSettingItem) item).onClicked(position);
		}
	}

	private class ServerHostSettingItem extends InputSettingItem implements PopupResult<SettingsInput, SettingsInputResult> {
		public ServerHostSettingItem() {
			super("Server Host", "");
		}

		@Override
		public void onClicked(int position) {
			mInputPopup.show(new SettingsInput(getTitle(), getDescription()), this);
		}

		@Override
		public void onPopupResult(SettingsInput settingsInput, SettingsInputResult result) {
			if (result != null) {
				((GarageApp) getApplicationContext()).setApiServerHost(result.value);
				setDescription(result.value);
				mAdapter.notifyDataSetChanged();

				loadServerInfo();
			}
		}
	}

	private class ServerPortSettingItem extends InputSettingItem implements PopupResult<SettingsInput, SettingsInputResult> {
		public ServerPortSettingItem() {
			super("Server Port", "");
		}

		@Override
		public void onClicked(int position) {
			mInputPopup.show(new SettingsInput(getTitle(), getDescription(), true), this);
		}

		@Override
		public void onPopupResult(SettingsInput settingsInput, SettingsInputResult result) {
			if (result != null) {
				((GarageApp) getApplicationContext()).setApiServerPort(Integer.parseInt(result.value));
				setDescription(result.value);
				mAdapter.notifyDataSetChanged();

				loadServerInfo();
			}
		}
	}

	private class ApiTokenSettingItem extends InputSettingItem implements PopupResult<SettingsInput, SettingsInputResult> {
		public ApiTokenSettingItem() {
			super("Api Token", "");
		}

		@Override
		public void onClicked(int position) {
			mInputPopup.show(new SettingsInput(getTitle(), getDescription()), this);
		}

		@Override
		public void onPopupResult(SettingsInput settingsInput, SettingsInputResult result) {
			if (result != null) {
				((GarageApp) getApplicationContext()).setApiToken(result.value);
				setDescription(result.value);
				mAdapter.notifyDataSetChanged();
			}
		}
	}
}
