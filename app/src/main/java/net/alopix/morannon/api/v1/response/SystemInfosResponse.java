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
public class SystemInfosResponse {
    @SerializedName("name")
    private String mName;
    @SerializedName("version")
    private String mVersion;

    public SystemInfosResponse() {
    }

    public String getName() {
        return mName;
    }

    public String getVersion() {
        return mVersion;
    }
}
