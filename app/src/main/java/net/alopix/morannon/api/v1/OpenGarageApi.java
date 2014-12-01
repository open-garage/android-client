package net.alopix.morannon.api.v1;

import net.alopix.morannon.api.v1.response.SystemInfosResponse;
import net.alopix.morannon.api.v1.request.ToggleRequest;
import net.alopix.morannon.api.v1.response.ToggleResponse;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by P40809 on 01.12.2014.
 */
public interface OpenGarageApi {
    static final String BASE_PATH = "/api/v1";

    @GET(BASE_PATH + "/")
    void systemInfos(Callback<SystemInfosResponse> cb);

    @POST(BASE_PATH + "/toggle")
    void toggle(@Body ToggleRequest request, Callback<ToggleResponse> cb);
}
