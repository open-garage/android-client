/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 3.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon.api.v1.response;

import com.google.gson.annotations.SerializedName;

import net.alopix.morannon.api.v1.OpenGarageService;

/**
 * Created by dustin on 01.12.2014.
 */
public abstract class StatusResponse {
    @SerializedName("status")
    private int mStatus;

    public StatusResponse() {
    }

    public int getStatus() {
        return mStatus;
    }

    public boolean isSuccess() {
        return mStatus != OpenGarageService.STATUS_ERROR;
    }
}
