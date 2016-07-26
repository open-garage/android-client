/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 3.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon.api.v1;

import net.alopix.morannon.api.v1.request.DoorStatusRequest;
import net.alopix.morannon.api.v1.request.ToggleRequest;
import net.alopix.morannon.api.v1.response.DoorStatusResponse;
import net.alopix.morannon.api.v1.response.SystemInfosResponse;
import net.alopix.morannon.api.v1.response.ToggleResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by dustin on 01.12.2014.
 */
public interface OpenGarageService {
	String BASE_PATH = "/api/v1";

	@GET(BASE_PATH + "/")
	Call<SystemInfosResponse> systemInfos();

	@POST(BASE_PATH + "/toggle")
	Call<ToggleResponse> toggle(@Body ToggleRequest request);

	@POST(BASE_PATH + "/status")
	Call<DoorStatusResponse> doorStatus(@Body DoorStatusRequest request);
}
