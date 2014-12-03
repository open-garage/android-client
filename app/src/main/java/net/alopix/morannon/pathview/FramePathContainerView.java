package net.alopix.morannon.pathview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import net.alopix.morannon.R;

import flow.Flow;
import flow.Path;
import flow.PathContainer;
import flow.PathContainerView;

/**
 * A FrameLayout that can show screens for a {@link flow.Flow}.
 * <p/>
 * Created by dustin on 03.12.2014.
 */
public class FramePathContainerView extends FrameLayout implements HandlesBack, HandlesUp, PathContainerView {
    private final PathContainer mContainer;
    private boolean mDisabled;

    @SuppressWarnings("UnusedDeclaration") // Used by layout inflation, of course!
    public FramePathContainerView(Context context, AttributeSet attrs) {
        this(context, attrs, new SimplePathContainer(R.id.screen_switcher_tag, Path.contextFactory()));
    }

    /**
     * Allows subclasses to use custom {@link flow.PathContainer} implementations. Allows the use
     * of more sophisticated transition schemes, and customized context wrappers.
     */
    protected FramePathContainerView(Context context, AttributeSet attrs, PathContainer container) {
        super(context, attrs);
        this.mContainer = container;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return !mDisabled && super.dispatchTouchEvent(ev);
    }

    @Override
    public ViewGroup getContainerView() {
        return this;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public void dispatch(Flow.Traversal traversal, final Flow.TraversalCallback callback) {
        mDisabled = true;
        mContainer.executeTraversal(this, traversal, new Flow.TraversalCallback() {
            @Override
            public void onTraversalCompleted() {
                callback.onTraversalCompleted();
                mDisabled = false;
            }
        });
    }

    @Override
    public boolean onUpPressed() {
        return UpAndBack.onUpPressed(getCurrentChild());
    }

    @Override
    public boolean onBackPressed() {
        return UpAndBack.onBackPressed(getCurrentChild());
    }

    @Override
    public ViewGroup getCurrentChild() {
        return (ViewGroup) getContainerView().getChildAt(0);
    }

    public boolean isDisabled() {
        return mDisabled;
    }
}
