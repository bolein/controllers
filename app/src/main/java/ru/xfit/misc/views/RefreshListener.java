package ru.xfit.misc.views;


import android.support.v4.widget.SwipeRefreshLayout;

public interface RefreshListener {
    void onRefresh(SwipeRefreshLayout refreshLayout);
}