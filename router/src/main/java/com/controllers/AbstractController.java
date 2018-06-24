package com.controllers;

import android.databinding.BaseObservable;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;


/**
 * Created by Vadym Ovcharenko
 * 18.10.2016.
 */

public abstract class AbstractController<B extends ViewDataBinding> extends
        BaseObservable implements IController, Router {

    public static final String TAG = AbstractController.class.getSimpleName();

    /**
     * ViewStrategy of the Controller representation in terms of Android
     */
    interface ViewStrategy<B extends ViewDataBinding> {
        ControllerActivity getActivity();
        Fragment asFragment();
        B getBinding();
        void subscribe(ViewLifecycleConsumer consumer);
        void unsubscribe(ViewLifecycleConsumer consumer);
    }

    public interface ViewLifecycleConsumer {
        void onCreate(Bundle var1);
        void onStart();
        void onResume();
        void onPause();
        void onStop();
        void onDestroy();
    }

    private ControllerActivity host;

    private ViewStrategy<B> viewStrategy;

    private boolean attachedToScreen;
    private boolean attachedToStack;

    // must be public with no arguments
    public AbstractController() {
        viewStrategy = createStrategy();
        if (viewStrategy == null) throw new IllegalStateException();
    }

    abstract ViewStrategy<B> createStrategy();

    protected abstract @LayoutRes int getLayoutId();

    @Nullable
    public final ControllerActivity getActivity() {
        return host;
    }

    @NonNull
    public final Fragment asFragment() {
        return viewStrategy.asFragment();
    }

    @Override
    public void onAttachedToScreen() {
        if (attachedToScreen) throwIllegalState("already attached");
        attachedToScreen = true;
    }

    @Override
    public void onDetachedFromScreen() {
        if (!attachedToScreen) throwIllegalState("already detached");
        attachedToScreen = false;
    }

    @Override
    public void onAttachedToStack(@NonNull Router router) {
        if (attachedToStack || host == null) throw new IllegalStateException();
        this.host = (ControllerActivity) router;
        attachedToStack = true;
    }

    @Override
    public void onDetachedFromStack(@NonNull Router router) {
        if (!attachedToStack) throw new IllegalStateException();
        attachedToStack = false;
        this.host = null;
    }

    void onRestoredInternal() {
        if (attachedToScreen || !attachedToStack) throw new IllegalStateException();
        onRestored();
    }

    /**
     * This method is called when the controllers stack was restored after the
     * activity recreation.
     *.
     */
    void onRestored() {

    }

    boolean isAttachedToScreen() {
        return attachedToScreen;
    }

    boolean isAttachedToStack() {
        return attachedToStack;
    }

    @Nullable
    protected B getBinding() {
        return viewStrategy.getBinding();
    }

    /**
     * @return true if want to override default behaviour
     */
    protected boolean onBackPressed() {
        ControllerActivity activity = getActivity();
        if (activity != null) {
            if (activity.stack != null && activity.stack.size() > 1) {
                activity.back();
                return true;
            }
        }
        return false;
    }

    @Override
    public final boolean show(Controller controller) {
        if (attachedToScreen && getActivity() != null) {
            return getActivity().show(controller);
        } else {
            Log.w(TAG, "show: ignored call from detached controller");
            return false;
        }
    }

    @Override
    public final boolean show(@NonNull Controller next,
                           @AnimRes int enter, @AnimRes int exit) {
        if (attachedToScreen && getActivity() != null) {
            return getActivity().show(next, enter, exit);
        } else {
            Log.w(TAG, "show: ignored call from detached controller");
            return false;
        }
    }

    @Override
    public final boolean back() {
        if (attachedToScreen && getActivity() != null) {
            return getActivity().back();
        } else {
            Log.w(TAG, "back: ignored call from detached controller");
            return false;
        }
    }

    @Override
    public final boolean back(@AnimRes int enter, @AnimRes int exit) {
        if (attachedToScreen && getActivity() != null) {
            return getActivity().back(enter, exit);
        } else {
            Log.w(TAG, "back: ignored call from detached controller");
            return false;
        }
    }

    @Override
    public final boolean replace(Controller controller, @AnimRes int enter, @AnimRes int exit) {
        // TODO: should pop all the controllers laying above it before replace
        if (attachedToScreen && getActivity() != null) {
            return getActivity().replace(controller, enter, exit);
        } else {
            Log.w(TAG, "replace: ignored call from detached controller");
            return false;
        }
    }

    @Override
    public final boolean replace(Controller controller) {
        if (attachedToScreen && getActivity() != null) {
            return getActivity().replace(controller);
        } else {
            Log.w(TAG, "replace: ignored call from detached controller");
            return false;
        }
    }

    @Override
    public boolean clear(Controller controller) {
        if (attachedToScreen && getActivity() != null) {
            return getActivity().clear(controller);
        } else {
            Log.w(TAG, "clear: ignored call from detached controller");
            return false;
        }
    }

    @Override
    public boolean clear(Controller controller, int enter, int exit) {
        if (attachedToScreen && getActivity() != null) {
            return getActivity().clear(controller, enter, exit);
        } else {
            Log.w(TAG, "clear: ignored call from detached controller");
            return false;
        }
    }

    @Override
    public final boolean goBackTo(Controller controller) {
        if (attachedToScreen && getActivity() != null) {
            return getActivity().goBackTo(controller);
        } else {
            Log.w(TAG, "goBackTo: ignored call from detached controller");
            return false;
        }
    }

    @Override
    public final boolean goBackTo(Controller controller,
                               @AnimRes int enter, @AnimRes int exit) {
        if (attachedToScreen && getActivity() != null) {
            return getActivity().goBackTo(controller, enter, exit);
        } else {
            Log.w(TAG, "goBackTo: ignored call from detached controller");
            return false;
        }
    }

    @Override
    @Nullable
    public final <T> T findByClass(Class<T> clazz) {
        if (getActivity() != null) {
            return getActivity().findByClass(clazz);
        } else {
            return null;
        }
    }

    @Override
    @Nullable
    public final Controller findByTag(Object tag) {
        if (getActivity() != null) {
            return getActivity().findByTag(tag);
        } else {
            return null;
        }
    }

    @Override
    @Nullable
    public Controller getPrevious() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            return getActivity().getPrevious();
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public Controller getTop() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            return getActivity().getTop();
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public Controller getBottom() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            return getActivity().getBottom();
        } else {
            return null;
        }
    }

    // TODO: I don't really like it
    public String getTitle() {
        return "Untitled controller";
    }

    @Deprecated
    protected boolean shouldRetainView() {
        return false;
    }

    boolean beforeChanged(AbstractController next) {
        return false;
    }

    void subscribe(ViewLifecycleConsumer consumer) {
        viewStrategy.subscribe(consumer);
    }

    void unsubscribe(ViewLifecycleConsumer consumer) {
        viewStrategy.unsubscribe(consumer);
    }

    private void throwIllegalState(String message) {
        throw new IllegalStateException(getClass().getSimpleName() + ": " + message);
    }
}
