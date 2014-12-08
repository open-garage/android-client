/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 8.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon.api.v1.request;

/**
 * Created by dustin on 08.12.2014.
 */
public class DoorStatusRequest extends AuthenticatedRequest {
    public DoorStatusRequest(String token) {
        super(token);
    }
}
