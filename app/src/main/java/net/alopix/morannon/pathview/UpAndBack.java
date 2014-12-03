package net.alopix.morannon.pathview;

import android.view.View;

import flow.Flow;

/**
 * Support for {@link HandlesUp} and {@link HandlesBack}.
 * <p/>
 * Created by dustin on 03.12.2014.
 */
public final class UpAndBack {
    public static boolean onUpPressed(View childView) {
        if (childView instanceof HandlesUp) {
            if (((HandlesUp) childView).onUpPressed()) {
                return true;
            }
        }
        // Try to go up.  If up isn't supported, go back.
        return Flow.get(childView).goUp() || onBackPressed(childView);
    }

    public static boolean onBackPressed(View childView) {
        if (childView instanceof HandlesBack) {
            if (((HandlesBack) childView).onBackPressed()) {
                return true;
            }
        }
        return Flow.get(childView).goBack();
    }

    private UpAndBack() {
    }
}
