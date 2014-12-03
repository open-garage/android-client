package net.alopix.morannon;

import net.alopix.morannon.pathview.HasTitle;

import flow.HasParent;
import flow.Layout;
import flow.Path;

/**
 * Created by dustin on 03.12.2014.
 */
public class Paths {
    @Layout(R.layout.toggle_view)
    public static class Toggle extends Path implements HasTitle {
        public Toggle() {
            super();
        }

        @Override
        public int getTitle() {
            return R.string.title_toggle;
        }
    }

    @Layout(R.layout.settings_views)
    public static class Settings extends Path implements HasTitle, HasParent {
        @Override
        public int getTitle() {
            return R.string.title_settings;
        }

        @Override
        public Path getParent() {
            return new Toggle();
        }
    }
}
