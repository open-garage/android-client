/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 3.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon.api.v1.request;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import net.alopix.morannon.BuildConfig;

import java.lang.reflect.Type;

/**
 * Created by dustin on 01.12.2014.
 */
public class ToggleRequest extends AuthenticatedRequest {
    @SerializedName("debug")
    private int mDebug;

    @SerializedName("state")
    private State mState;

    public ToggleRequest(State state, String token) {
        super(token);

        mDebug = BuildConfig.DEBUG ? 1 : 0;
        mState = state;
    }

    public static enum State {
        CLOSE, OPEN, TOGGLE
    }

    public static class StateSerializer implements JsonSerializer<State> {
        @Override
        public JsonElement serialize(State state, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(state.name().toLowerCase());
        }
    }
}
