/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 3.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon.util;

import android.os.Bundle;
import android.support.annotation.Nullable;

import flow.Backstack;
import flow.Flow;
import flow.Parceler;

/**
 * Handles Bundle persistence of a Flow.
 * <p/>
 * Created by dustin on 03.12.2014.
 */
public abstract class FlowBundler {
    private static final String FLOW_KEY = "flow_key";

    private final Parceler mParceler;

    private Flow mFlow;

    protected FlowBundler(Parceler parceler) {
        mParceler = parceler;
    }

    public Flow onCreate(@Nullable Bundle savedInstanceState) {
        if (mFlow != null) {
            return mFlow;
        }

        Backstack restoredBackstack = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(FLOW_KEY)) {
            restoredBackstack = Backstack.from(savedInstanceState.getParcelable(FLOW_KEY), mParceler);
        }
        mFlow = new Flow(getColdStartBackstack(restoredBackstack));
        return mFlow;
    }

    public void onSaveInstanceState(Bundle outState) {
        Backstack backstack = getBackstackToSave(mFlow.getBackstack());
        if (backstack == null) {
            return;
        }
        outState.putParcelable(FLOW_KEY, backstack.getParcelable(mParceler));
    }

    /**
     * Returns the backstack that should be archived by {@link #onSaveInstanceState}. Overriding
     * allows subclasses to handle cases where the current configuration is not one that should
     * survive process death.  The default implementation returns a BackStackToSave that specifies
     * that view state should be persisted.
     *
     * @return the stack to archive, or null to archive nothing
     */
    @Nullable
    protected Backstack getBackstackToSave(Backstack backstack) {
        return backstack;
    }

    /**
     * Returns the backstack to initialize the new flow.
     *
     * @param restoredBackstack the backstack recovered from the bundle passed to {@link #onCreate},
     *                          or null if there was no bundle or no backstack was found
     */
    protected abstract Backstack getColdStartBackstack(@Nullable Backstack restoredBackstack);
}
