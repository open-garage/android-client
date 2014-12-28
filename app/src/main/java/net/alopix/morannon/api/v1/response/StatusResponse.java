/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 3.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon.api.v1.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dustin on 01.12.2014.
 */
public abstract class StatusResponse {
    public static final int STATUS_OK = 0;
    public static final int STATUS_TOKEN_ERROR = -1;

    @SerializedName("status")
    private int mStatus;

    public StatusResponse() {
    }

    public int getStatus() {
        return mStatus;
    }

    public boolean isSuccess() {
        return mStatus == STATUS_OK;
    }
}
