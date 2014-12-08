/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 3.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon.api.v1.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dustin on 01.12.2014.
 */
public class AuthenticatedRequest {
    @SerializedName("token")
    private String mToken;

    public AuthenticatedRequest(String token) {
        mToken = token;
    }

    public String getToken() {
        return mToken;
    }
}
