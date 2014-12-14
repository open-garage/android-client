/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 14.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon.model;

/**
 * Created by dustin on 14/12/14.
 */
public abstract class InputSettingItem extends SettingItem implements ClickableSettingItem {
    public InputSettingItem() {
        super();
    }

    public InputSettingItem(String title, String description) {
        super(title, description);
    }
}
