package com.cvvm;

import android.databinding.BaseObservable;
import android.databinding.ViewDataBinding;
import android.support.annotation.AnimRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;


/**
 * Created by Vadim Ovcharenko
 * 18.10.2016.
 */

public abstract class AbstractController<B extends ViewDataBinding> extends
        BaseObservable implements IController {

    public static final String TAG = AbstractController.class.getSimpleName();

    /**
     * Strategy of the Controller representation in terms of Android
     */
    interface Strategy<B extends ViewDataBinding> {
        ControllerActivity getActivity();

        Fragment asFragment();

        B getBinding();
    }

    private transient Strategy<B> strategy;
    private transient boolean attachedToScreen;

    private boolean attachedToStack;

    // must be public with no arguments
    public AbstractController() {
        strategy = createStrategy();
        if (strategy == null) throw new IllegalStateException();
    }

    abstract Strategy<B> createStrategy();

    @Nullable
    public ControllerActivity getActivity() {
        return strategy.getActivity();
    }

    @NonNull
    @Override
    public final Fragment asFragment() {
        return strategy.asFragment();
    }

    void onAttachedToScreen() {
        if (attachedToScreen) throw new IllegalStateException();
        attachedToScreen = true;
    }

    void onDetachedFromScreen() {
        if (!attachedToScreen) throw new IllegalStateException();
        attachedToScreen = false;
    }

    void onAttachedToStackInternal() {
        if (attachedToStack) throw new IllegalStateException();
        attachedToStack = true;
        onAttachedToStack();
    }

    void onDetachedFromStackInternal() {
        if (!attachedToStack) throw new IllegalStateException();
        attachedToStack = false;
        onDetachedFromStack();
    }

    protected void onAttachedToStack() {

    }

    protected void onDetachedFromStack() {

    }

    void onRestored() {

    }

    boolean isAttachedToScreen() {
        return attachedToScreen;
    }

    @Nullable
    protected B getBinding() {
        return strategy.getBinding();
    }

    /**
     * @return true if want to override default behaviour
     */
    protected boolean onBackPressed() {
        AbstractControllerActivity activity = getActivity();
        if (activity != null) {
            if (activity.stack != null && activity.stack.size() > 1) {
                activity.back();
                return true;
            }
        }
        return false;
    }

    public final void show(AbstractController controller) {
        if (attachedToScreen && getActivity() != null && !getActivity()
                .isFinishing()) {
            getActivity().show(controller);
        } else {
            Log.w(TAG, "show: ignored call from detached controller");
        }
    }

    public final void show(@NonNull AbstractController next,
                           @AnimRes int enter, @AnimRes int exit) {
        if (attachedToScreen && getActivity() != null && !getActivity()
                .isFinishing()) {
            getActivity().show(next, enter, exit);
        } else {
            Log.w(TAG, "show: ignored call from detached controller");
        }
    }

    public final void back() {
        if (attachedToScreen && getActivity() != null && !getActivity()
                .isFinishing()) {
            getActivity().back();
        } else {
            Log.w(TAG, "back: ignored call from detached controller");
        }
    }

    public final void back(@AnimRes int enter, @AnimRes int exit) {
        if (attachedToScreen && getActivity() != null && !getActivity()
                .isFinishing()) {
            getActivity().back(enter, exit);
        } else {
            Log.w(TAG, "back: ignored call from detached controller");
        }
    }


    public final void replace(AbstractController controller) {
        if (attachedToScreen && getActivity() != null && !getActivity()
                .isFinishing()) {
            getActivity().replace(controller);
        } else {
            Log.w(TAG, "replace: ignored call from detached controller");
        }
    }

    protected final void goBackTo(AbstractController controller) {
        if (attachedToScreen && getActivity() != null && !getActivity()
                .isFinishing()) {
            getActivity().goBackTo(controller);
        } else {
            Log.w(TAG, "goBackTo: ignored call from detached controller");
        }
    }

    // go back with custom
    protected final void goBackTo(AbstractController controller, @AnimRes int
            enter, @AnimRes int exit) {
        if (attachedToScreen && getActivity() != null && !getActivity()
                .isFinishing()) {
            getActivity().goBackTo(controller, enter, exit);
        } else {
            Log.w(TAG, "goBackTo: ignored call from detached controller");
        }
    }

    @Nullable
    protected final <T extends Controller> T findByClass(Class<T> clazz) {
        if (getActivity() != null) {
            return getActivity().findByClass(clazz);
        } else {
            return null;
        }
    }

    @Nullable
    protected final AbstractController findByTag(String tag) {
        if (getActivity() != null) {
            return getActivity().findByTag(tag);
        } else {
            return null;
        }
    }

    @Nullable
    public Controller getPrevious() {
        if (getActivity() != null && getActivity().stack.size() > 1) {
            return (Controller) getActivity().stack.peek(1);
        } else {
            return null;
        }
    }

    public String getTitle() {
        return "Untitled controller";
    }

    boolean beforeChanged(AbstractController next) {
        return false;
    }

    // result will be returned to the calling controller
    protected void requestPermission(String permission, PermissionListener
            callback) {
        if (getActivity() != null) {
            getActivity().requestPermission(permission, callback);
        }
    }
}
