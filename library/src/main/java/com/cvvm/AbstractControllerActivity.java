package com.cvvm;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vadym Ovcharenko
 * 18.10.2016.
 */

abstract class AbstractControllerActivity extends AppCompatActivity {

    private static final String KEY_STACK = "_controller_stack";
    private static final String KEY_CONTAINER_ID = "_container_id";

    protected ControllerStack stack;
    private @IdRes int containerId;

    protected void setControllerContainer(@IdRes int containerResId) {
        this.containerId = containerResId;
    }

    protected AbstractController show(@NonNull AbstractController next,
                        @AnimRes int enter, @AnimRes int exit) {

        AbstractController prev = stack.peek();
        if (beforeControllersChanged(prev, next) || prev != null && prev.beforeChanged(next)) {
            return null;
        }

        stack.add(next);
        next.onAttachedToStackInternal();

        return changeControllersInternal(prev, next, enter, exit);
    }

    private void restore(@NonNull AbstractController controller) {
        changeControllersInternal(null, controller, 0, 0);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable protected AbstractController back(@AnimRes int enter, @AnimRes int exit) {
        if (stack.size() <= 1) throw new IllegalStateException("Stack must be bigger than 1");

        AbstractController prev = stack.peek();
        AbstractController next = stack.peek(1);

        if (beforeControllersChanged(prev, next) || prev != null && prev.beforeChanged(next)) {
            return null;
        }

        stack.pop();
        prev.onDetachedFromStackInternal();

        return changeControllersInternal(prev, next, enter, exit);
    }

    protected AbstractController goBackTo(AbstractController controller, @AnimRes int enter, @AnimRes int exit) {
        AbstractController prev = stack.peek();
        AbstractController next = null;

        int i = 0;
        boolean found = false;

        for (AbstractController one : stack) {
            if (one == controller) {
                next = one;
                found = true;
                break;
            }
            i++;
        }

        if (!found) {
            throw new IllegalArgumentException("Controller is not in stack");
        }

        if (next == null || beforeControllersChanged(prev, next) ||
                prev != null && prev.beforeChanged(next)) {
            return null;
        }

        for (AbstractController c : stack.pop(i)) {
            c.onDetachedFromStackInternal();
        }

        return replaceInternal(prev, next, enter, exit);
    }

    @Nullable protected AbstractController replace(AbstractController next,
                                                   @AnimRes int enter, @AnimRes int exit) {
        AbstractController prev = stack.peek();

        if (beforeControllersChanged(prev, next) || prev != null && prev.beforeChanged(next)) {
            return null;
        }

        return replaceInternal(prev, next, enter, exit);
    }

    // replaces top controller with the new one
    private AbstractController replaceInternal(AbstractController prev, AbstractController next,
                                 @AnimRes int enter, @AnimRes int exit) {
        stack.pop();
        prev.onDetachedFromStackInternal();
        stack.add(next);
        next.onAttachedToStackInternal();
        return changeControllersInternal(prev, next, enter, exit);
    }

    private AbstractController changeControllersInternal(AbstractController prev, final AbstractController next,
                                                         @AnimRes int enter, @AnimRes int exit) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (enter != 0 || exit != 0) {
            transaction.setCustomAnimations(enter, exit);
        }

        if (prev != null) prev.onDetachedFromScreen();

        transaction.replace(containerId, next.asFragment(), next.getTag())
                .commitNowAllowingStateLoss();
        next.onAttachedToScreen();
        onControllerChanged(next);
        return prev;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    protected <T extends AbstractController> T findByClass(Class<T> clazz) {
        if (stack == null) throw new IllegalStateException();
        for (AbstractController controller : stack) {
            if (controller.getClass() == clazz) {
                return (T) controller;
            }
        }
        return null;
    }

    @Nullable
    protected AbstractController findByTag(String tag){
        if (stack == null) throw new IllegalStateException();
        for (AbstractController controller : stack) {
            if (controller.getTag().equals(tag)) {
                return controller;
            }
        }
        return null;
    }

    boolean beforeControllersChanged(AbstractController previous, AbstractController next) {
        return false; // stub
    }

    protected void onControllerChanged(AbstractController controller) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(noFragmentsRestore(savedInstanceState));
        if (savedInstanceState != null) {
            this.containerId = savedInstanceState.getInt(KEY_CONTAINER_ID);
            this.stack = (ControllerStack) savedInstanceState.getSerializable(KEY_STACK);
            if (stack == null) {
                throw new IllegalStateException("Stack should be saved");
            }

            for (AbstractController controller : stack) {
                controller.onRestored();
            }
        } else {
            stack = new ControllerStack();
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // restore top controller after recreation
            AbstractController controller = stack.peek();
            if (controller == null) throw new IllegalStateException();
            restore(controller);
        }
    }

    /**
     * Improve bundle to prevent restoring of fragments.
     *
     * @param bundle bundle container
     * @return improved bundle with removed "fragments parcelable"
     */
    private static Bundle noFragmentsRestore(Bundle bundle) {
        if (bundle != null) {
            bundle.remove("android:support:fragments");
        }
        return bundle;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isChangingConfigurations()) {
            for (AbstractController controller : stack) {
                controller.onDetachedFromScreen();
            }
        }
        outState.putInt(KEY_CONTAINER_ID, containerId);
        outState.putSerializable(KEY_STACK, stack);
    }

    /**
     * Shows controller with default animation
     */
    protected AbstractController show(@NonNull AbstractController controller) {
        return show(controller, R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Pops top controller with default animation
     */
    protected AbstractController back() {
        return back(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * Backs to given controller with default animation
     */
    protected AbstractController goBackTo(AbstractController controller) {
        return goBackTo(controller, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    protected AbstractController replace(AbstractController controller) {
        return replace(controller, android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Nullable protected Controller getTop() {
        if (stack != null) {
            return (Controller) stack.peek();
        } else {
            return null;
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override public void onBackPressed() {
        if (stack == null || stack.size() == 0 || !stack.peek().onBackPressed()) {
            super.onBackPressed();
        }
    }
}
