package net.alopix.morannon.api.v1.request;

import net.alopix.morannon.Config;

/**
 * Created by P40809 on 01.12.2014.
 */
public class ToggleRequest extends AuthenticatedRequest {
    public ToggleRequest() {
        setToken(Config.API_TOKEN);
    }
}
