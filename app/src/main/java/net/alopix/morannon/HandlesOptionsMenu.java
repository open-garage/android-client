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
