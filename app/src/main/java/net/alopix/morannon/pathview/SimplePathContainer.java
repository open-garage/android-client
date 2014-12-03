package net.alopix.morannon.pathview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.alopix.morannon.util.Utils;

import flow.Flow;
import flow.Path;
import flow.PathContainer;
import flow.PathContext;
import flow.PathContextFactory;

import static flow.Flow.Direction.REPLACE;

/**
 * Provides basic right-to-left transitions. Saves and restores view state.
 * Uses {@link flow.PathContext} to allow customized sub-containers.
 * <p/>
 * Created by dustin on 03.12.2014.
 */
public class SimplePathContainer extends PathContainer {
    private final PathContextFactory mContextFactory;

    SimplePathContainer(int tagKey, PathContextFactory contextFactory) {
        super(tagKey);
        mContextFactory = contextFactory;
    }

    @Override
    protected void performTraversal(final ViewGroup containerView,
                                    final TraversalState traversalState, final Flow.Direction direction,
                                    final Flow.TraversalCallback callback) {
        final PathContext context;
        final PathContext oldPath;
        if (containerView.getChildCount() > 0) {
            oldPath = PathContext.get(containerView.getChildAt(0).getContext());
        } else {
            oldPath = PathContext.root(containerView.getContext());
        }

        Path to = traversalState.toPath();

        ViewGroup newView;
        context = PathContext.create(oldPath, to, mContextFactory);
        int layout = getLayout(to);
        newView = (ViewGroup) LayoutInflater.from(context)
                .cloneInContext(context)
                .inflate(layout, containerView, false);

        View fromView = null;
        if (traversalState.fromPath() != null) {
            fromView = containerView.getChildAt(0);
            traversalState.saveViewState(fromView);
        }
        traversalState.restoreViewState(newView);

        if (fromView == null || direction == REPLACE) {
            containerView.removeAllViews();
            containerView.addView(newView);
            oldPath.destroyNotIn(context, mContextFactory);
            callback.onTraversalCompleted();
        } else {
            containerView.addView(newView);
            final View finalFromView = fromView;
            Utils.waitForMeasure(newView, new Utils.OnMeasuredCallback() {
                @Override
                public void onMeasured(View view, int width, int height) {
                    runAnimation(containerView, finalFromView, view, direction, new Flow.TraversalCallback() {
                        @Override
                        public void onTraversalCompleted() {
                            containerView.removeView(finalFromView);
                            oldPath.destroyNotIn(context, mContextFactory);
                            callback.onTraversalCompleted();
                        }
                    });
                }
            });
        }
    }

    private void runAnimation(final ViewGroup container, final View from, final View to,
                              Flow.Direction direction, final Flow.TraversalCallback callback) {
        Animator animator = createSegue(from, to, direction);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                container.removeView(from);
                callback.onTraversalCompleted();
            }
        });
        animator.start();
    }

    private Animator createSegue(View from, View to, Flow.Direction direction) {
        boolean backward = direction == Flow.Direction.BACKWARD;
        int fromTranslation = backward ? from.getWidth() : -from.getWidth();
        int toTranslation = backward ? -to.getWidth() : to.getWidth();

        AnimatorSet set = new AnimatorSet();

        set.play(ObjectAnimator.ofFloat(from, View.TRANSLATION_X, fromTranslation));
        set.play(ObjectAnimator.ofFloat(to, View.TRANSLATION_X, toTranslation, 0));

        return set;
    }
}
