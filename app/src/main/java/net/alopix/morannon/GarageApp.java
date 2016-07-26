/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 3.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon;

import android.app.Application;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.alopix.morannon.api.v1.OpenGarageService;
import net.alopix.morannon.api.v1.request.ToggleRequest;
import net.alopix.morannon.service.GarageService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dustin on 01.12.2014.
 */
public class GarageApp extends Application {
	private static final String API_SERVER_HOST_KEY = "api_server_host_key";
	private static final String API_SERVER_PORT_KEY = "api_server_port_key";
	private static final String API_TOKEN_KEY = "api_token_key";

	private static final String DEFAULT_SERVER_HOST = "localhost";
	private static final int DEFAULT_SERVER_PORT = 8000;
	private static final String DEFAULT_API_TOKEN = "A";

	private static final String CURRENT_PROGRESS_KEY = "current_progress_key";

	private static final int HTTP_TIMEOUT = 15;

	private OpenGarageService mApiService;

	@Override
	public void onCreate() {
		super.onCreate();

		createApiService();
	}

	public OpenGarageService createApiService(String endpoint) {
		OkHttpClient client = configureClient();
		Gson gson = createGson();


		Retrofit adapter = new Retrofit.Builder()
				.baseUrl(endpoint)
				.addConverterFactory(GsonConverterFactory.create(gson))
				.client(client)
				.build();
		return adapter.create(OpenGarageService.class);
	}

	private Gson createGson() {
		return new GsonBuilder().registerTypeAdapter(ToggleRequest.State.class, new ToggleRequest.StateSerializer()).create();
	}

	private void createApiService() {
		mApiService = createApiService("https://" + getApiServerHost() + ":" + getApiServerPort());
	}

	private OkHttpClient configureClient() {
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

		OkHttpClient client = new OkHttpClient.Builder()
				.addInterceptor(interceptor)
				.connectTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
				.readTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
				.build();
		return client;
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
}
