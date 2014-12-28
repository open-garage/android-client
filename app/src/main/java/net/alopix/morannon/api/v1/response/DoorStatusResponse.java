/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 8.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon.api.v1.response;

/**
 * Created by dustin on 08.12.2014.
 */
public class DoorStatusResponse extends StatusResponse {
    public static final int STATUS_OPEN = 0;
    public static final int STATUS_CLOSED = 1;

    public DoorStatusResponse() {
    }
}