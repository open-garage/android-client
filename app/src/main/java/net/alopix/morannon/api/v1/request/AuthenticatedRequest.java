package net.alopix.morannon.api.v1.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by P40809 on 01.12.2014.
 */
public class AuthenticatedRequest {
    @SerializedName("token")
    private String mToken;

    public AuthenticatedRequest() {
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String mToken) {
        this.mToken = mToken;
    }
}
