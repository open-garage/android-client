/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 8.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon.popup;

/**
 * Created by dustin on 08/12/14.
 */
public interface PopupResult<Info, Result> {
    void onPopupResult(Info info, Result result);
}
