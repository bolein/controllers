package com.controllers;

import android.support.annotation.AnimRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface Router {

    @Nullable
    Controller show(Controller controller);

    @Nullable
    Controller show(@NonNull Controller next,
              @AnimRes int enter, @AnimRes int exit);

    @Nullable
    Controller back();

    @Nullable
    Controller back(@AnimRes int enter, @AnimRes int exit);

    @Nullable
    Controller replace(Controller controller);

    @Nullable
    Controller replace(Controller next, @AnimRes int enter, @AnimRes int exit);

    @Nullable
    Controller goBackTo(Controller controller);

    @Nullable
    Controller goBackTo(Controller controller, @AnimRes int enter, @AnimRes int exit);

    /**
     * Clear the stack of controllers and place one on the top
     * @param controller controller to place on top
     * @return top controller
     */
    @Nullable
    Controller clear(Controller controller);

    @Nullable
    Controller clear(Controller controller, @AnimRes int enter, @AnimRes int exit);

    @Nullable
    <T> T findByClass(Class<T> clazz);

    @Nullable
    Controller findByTag(Object tag);

    @Nullable
    Controller getPrevious();

    @Nullable
    Controller getTop();

    @Nullable
    Controller getBottom();
}
