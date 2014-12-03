/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 3.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by dustin on 03.12.2014.
 */
public interface HandlesOptionsMenu {
    boolean onCreateOptionsMenu(MenuInflater menuInflater, Menu menu);

    boolean onOptionsItemSelected(MenuItem item);
}
