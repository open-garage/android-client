/*
 * OpenGarage
 *
 * Created by Dustin Steiner on 3.12.2014.
 * Copyright (c) 2014 Dustin Steiner. All rights reserved.
 */

package net.alopix.morannon;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.alopix.morannon.pathview.FramePathContainerView;
import net.alopix.morannon.pathview.HandlesBack;
import net.alopix.morannon.pathview.HandlesUp;
import net.alopix.morannon.pathview.HasTitle;
import net.alopix.morannon.util.FlowBundler;

import flow.Flow;
import flow.HasParent;
import flow.Path;
import flow.PathContainerView;

/**
 * Created by P40809 on 01.12.2014.
 */
public class MainActivity extends ActionBarActivity implements Flow.Dispatcher {
    private static final String TAG = MainActivity.class.getSimpleName();

    private PathContainerView mContainer;
    private HandlesBack mContainerAsBackTarget;
    private HandlesUp mContainerAsUpTarget;

    private Flow mFlow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFlow = getFlowBundler().onCreate(savedInstanceState);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);

        setContentView(R.layout.root_layout);

        mContainer = (PathContainerView) findViewById(R.id.container);
        mContainerAsBackTarget = (HandlesBack) mContainer;
        mContainerAsUpTarget = (HandlesUp) mContainer;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFlow.setDispatcher(this);
    }

    @Override
    protected void onPause() {
        mFlow.removeDispatcher(this);
        super.onPause();
    }

    @Override
    public Object getSystemService(String name) {
        if (Flow.isFlowSystemService(name)) {
            return mFlow;
        }
        return super.getSystemService(name);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getFlowBundler().onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO: can this be improved somehow? due to traversal/animation this will use the old view instead at first
        if (((FramePathContainerView) mContainer.getContainerView()).isDisabled()) {
            return true;
        }

        //Path screen = Flow.get(this).getBackstack().current();
        if (mContainer.getCurrentChild() instanceof HandlesOptionsMenu) {
            ((HandlesOptionsMenu) mContainer.getCurrentChild()).onCreateOptionsMenu(getMenuInflater(), menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return mContainerAsUpTarget.onUpPressed();

            default:
                // TODO: can this be improved somehow? due to traversal/animation this will use the old view instead at first
                //Path screen = Flow.get(this).getBackstack().current();
                if (mContainer.getCurrentChild() instanceof HandlesOptionsMenu) {
                    if (((HandlesOptionsMenu) mContainer.getCurrentChild()).onOptionsItemSelected(item)) {
                        return true;
                    }
                }

                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mContainerAsBackTarget.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void dispatch(Flow.Traversal traversal, final Flow.TraversalCallback traversalCallback) {
        Path path = traversal.destination.current();
        mContainer.dispatch(traversal, new Flow.TraversalCallback() {
            @Override
            public void onTraversalCompleted() {
                supportInvalidateOptionsMenu();

                if (traversalCallback != null) {
                    traversalCallback.onTraversalCompleted();
                }
            }
        });

        if (path instanceof HasTitle) {
            setTitle(((HasTitle) path).getTitle());
        } else {
            setTitle(path.getClass().getSimpleName());
        }

        final ActionBar actionBar = getSupportActionBar();
        boolean hasUp = path instanceof HasParent;
        actionBar.setDisplayHomeAsUpEnabled(hasUp);
        actionBar.setHomeButtonEnabled(hasUp);

        supportInvalidateOptionsMenu();
    }

    private FlowBundler getFlowBundler() {
        return ((GarageApp) getApplication()).getFlowBundler();
    }
}
