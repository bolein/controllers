package com.controllers;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.controllers.core.View;
import com.controllers.core.ViewModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Vadym Ovcharenko
 * 18.10.2016.
 *
 * Lifecycle contract: stack lifecycle does not depend on the screen lifecycle. A controller
 * may stay on screen even after it was detached from router (e.g. for ending transitions). It may
 * also be attached to screen before router in some cases.
 */

public abstract class Controller<B extends ViewDataBinding> extends
    BaseObservable implements ViewModel<FragmentBindingView<B>>, Serializable {

    /**
     * ID is generated once per creation of the controller and is a unique identifier.
     * // TODO: uuid is slow
     */
    public final String ID = UUID.randomUUID().toString();


    /**
     * View in terms of Android
     */
    interface BindingView<B extends ViewDataBinding> extends View {
        @Nullable Activity getActivity();
        @Nullable B getBinding();
        void subscribe(ViewLifecycleConsumer consumer);
        void unsubscribe(ViewLifecycleConsumer consumer);
    }

    /**
     * TODO: document use cases. Example: MapView
     */
    public interface ViewLifecycleConsumer {
        void onCreate(Bundle var1);
        void onStart();
        void onResume();
        void onPause();
        void onStop();
        void onDestroy();
    }

    enum ViewLifecycleEvent {
        CREATE(0), START(1), RESUME(2), PAUSE(3), STOP(4), DESTROY(5);

        int order;

        ViewLifecycleEvent(int order) {
            this.order = order;
        }
    }

    public interface Observer extends Serializable {
        void onAttachedToRouter(@NonNull Controller observable);
        void onDetachedFromRouter(@NonNull Controller observable);
        void onAttachedToScreen(@NonNull Controller observable);
        void onDetachedFromScreen(@NonNull Controller observable);
        void onRestored(@NonNull Controller observable);
    }

    @Nullable private transient FragmentBindingView<B> view;

    private transient boolean attachedToScreen;

    private boolean attachedToRouter;
    private Set<Observer> observers;
    private Logger logger = new DefaultLogger(getClass().getSimpleName());

    protected abstract @LayoutRes int getLayoutId();

    boolean addObserver(Observer observer) {
        if (observers == null) {
            observers = new HashSet<>();
        }
        return observers.add(observer);
    }

    boolean removeObserver(Observer observer) {
        return observers != null && observers.remove(observer);
    }

    @Nullable
    @Override
    public FragmentBindingView<B> getView() {
        return view;
    }

    @Override
    public void onAttachedToScreen(@NonNull FragmentBindingView<B> view) {
        if (attachedToScreen) throwIllegalState("already attached");
        logger.logi("onAttachedToScreen", "");
        this.view = view;
        attachedToScreen = true;
        if (observers != null) {
            for (Observer observer : new ArrayList<>(observers)) {
                observer.onAttachedToScreen(this);
            }
        }
    }

    @Override
    public void onDetachedFromScreen(@NonNull FragmentBindingView<B> view) {
        if (!attachedToScreen) throwIllegalState("already detached");
        logger.logi("onDetachedFromScreen", "");
        // we don't null view reference in case controller will be reused
        attachedToScreen = false;
        if (observers != null) {
            for (Observer observer : new ArrayList<>(observers)) {
                observer.onDetachedFromScreen(this);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onAttachedToRouter() {
        if (attachedToRouter) throwIllegalState("already attached");
        logger.logi("onAttachedToRouter", "");
        attachedToRouter = true;
        if (observers != null) {
            for (Observer observer : new ArrayList<>(observers)) {
                observer.onAttachedToRouter(this);
            }
        }
    }

    @Override
    public void onDetachedFromRouter() {
        if (!attachedToRouter) throwIllegalState("already detached");
        logger.logi("onDetachedFromRouter", "");
        attachedToRouter = false;
        if (observers != null) {
            for (Observer observer : new ArrayList<>(observers)) {
                observer.onDetachedFromRouter(this);
            }
        }
    }

    public final boolean isAttachedToScreen() {
        return attachedToScreen;
    }

    public final boolean isAttachedToRouter() {
        return attachedToRouter;
    }

    @Nullable
    protected B getBinding() {
        return view != null ? view.getBinding() : null;
    }

    /**
     * @return true if want to override default behaviour
     */
    public boolean onBackPressed() {
        return false;
    }

    // TODO: I don't really like it
    public String getTitle() {
        return "Untitled controller";
    }

    @Deprecated
    protected boolean shouldRetainView() {
        return false;
    }

    boolean beforeChanged(Controller next) {
        return false;
    }

    private void throwIllegalState(String message) {
        throw new IllegalStateException(getClass().getSimpleName() + ": " + message);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Logger getLogger() {
        return this.logger;
    }
}
