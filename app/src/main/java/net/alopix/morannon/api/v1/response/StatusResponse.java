package net.alopix.morannon.api.v1.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by P40809 on 01.12.2014.
 */
public abstract class StatusResponse {
    @SerializedName("error")
    private int mError;

    public StatusResponse() {
    }

    public int getError() {
        return mError;
    }

    public boolean isSuccess() {
        return mError == 0;
    }
}
