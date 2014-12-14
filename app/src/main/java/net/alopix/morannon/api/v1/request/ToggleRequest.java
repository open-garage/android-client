/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 3.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon.api.v1.request;

import com.google.gson.annotations.SerializedName;

import net.alopix.morannon.BuildConfig;

/**
 * Created by dustin on 01.12.2014.
 */
public class ToggleRequest extends AuthenticatedRequest {
    @SerializedName("debug")
    private int mDebug;

    public ToggleRequest(String token) {
        super(token);

        mDebug = BuildConfig.DEBUG ? 1 : 0;
    }
}
