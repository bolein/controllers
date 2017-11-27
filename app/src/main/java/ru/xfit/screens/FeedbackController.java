package ru.xfit.screens;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ViewDataBinding;
import android.widget.PopupWindow;

import ru.xfit.model.data.club.ClubItem;

/**
 * Created by TESLA on 27.11.2017.
 */

public abstract class FeedbackController<B extends ViewDataBinding> extends XFitController<B> {
    public ObservableArrayList<ClubItem> clubs = new ObservableArrayList<>();
    public ObservableField<ClubItem> selectedClub = new ObservableField<>();
    public transient ObservableField<PopupWindow> prevPopup = new ObservableField<>();
}
