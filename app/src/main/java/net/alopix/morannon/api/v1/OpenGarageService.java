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

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by dustin on 01.12.2014.
 */
public interface OpenGarageService {
    static final String BASE_PATH = "/api/v1";

    public static final int DOOR_OPEN = 0;
    public static final int DOOR_CLOSED = 1;
    public static final int STATUS_ERROR = -1;

    @GET(BASE_PATH + "/")
    void systemInfos(Callback<SystemInfosResponse> cb);

    @POST(BASE_PATH + "/toggle")
    void toggle(@Body ToggleRequest request, Callback<DoorStatusResponse> cb);


    @POST(BASE_PATH + "/toggle")
    ToggleResponse toggleSync(@Body ToggleRequest request);

    @POST(BASE_PATH + "/status")
    void doorStatus(@Body DoorStatusRequest request, Callback<DoorStatusResponse> cb);

    @POST(BASE_PATH + "/status")
    DoorStatusResponse doorStatusSync(@Body DoorStatusRequest request);
}
