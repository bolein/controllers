package ru.xfit.domain;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.controllers.Controller;
import com.crashlytics.android.Crashlytics;
import com.hwangjr.rxbus.Bus;
import com.hwangjr.rxbus.annotation.Subscribe;
import io.fabric.sdk.android.Fabric;
import ru.xfit.R;
import ru.xfit.misc.events.OptionsItemSelectedEvent;
import ru.xfit.model.retrorequest.LogoutEvent;
import ru.xfit.screens.DrawerController;
import ru.xfit.screens.clubs.ClubsController;
import ru.xfit.screens.schedule.ClubClassesController;
import ru.xfit.screens.schedule.MyScheduleController;

import javax.inject.Inject;


public class MainActivity extends XFitActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    @Inject Bus bus;

    public MyScheduleController myScheduleController;
    public ClubsController clubsController;

    private Toolbar toolbar;
    private NavigationView navView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private boolean showFilterAndSearch;

    private boolean toolBarNavigationListenerIsRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getInjector().inject(this);
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        setControllerContainer(R.id.container);

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);

        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navView.setNavigationItemSelectedListener(this);

        myScheduleController = new MyScheduleController();
        clubsController = new ClubsController();
        if (savedInstanceState == null) {
            show(myScheduleController, 0, 0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Override
    protected void onControllerChanged(Controller controller) {

        setVisibleHamburgerIcon(!(controller instanceof DrawerController));

        if (controller instanceof ClubClassesController) {
            // TODO: push to the top controller or make a menu presenter
            showFilterAndSearch = true;
            invalidateOptionsMenu();
        } else {
            showFilterAndSearch = false;
            invalidateOptionsMenu();
        }

        setTitle(controller.getTitle());

        super.onControllerChanged(controller);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_schedules, menu);
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.search).setVisible(showFilterAndSearch);
        menu.findItem(R.id.filter).setVisible(showFilterAndSearch);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        App.getBus().post(new OptionsItemSelectedEvent(item));
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_schedule:
                replace(myScheduleController);
                setTitle(getResources().getString(R.string.my_schedule_title));
                drawer.closeDrawers();
                return true;
            case R.id.my_xfit:
                return true;
            case R.id.services:
                return true;
            case R.id.clubs:
                replace(clubsController);
                setTitle(getResources().getString(R.string.clubs_title));
                drawer.closeDrawers();
                return true;
            case R.id.contacts:
                return true;
            case R.id.settings:
                return true;
            case R.id.quit:
                logOut();
                return true;
        }
        return true;
    }

    @Subscribe
    public void onTriggerLogout(LogoutEvent logoutEvent) {
        logOut();
    }

    public void logOut() {
        StartActivity.start(this);
        finishAffinity();
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void setVisibleHamburgerIcon(boolean visible) {
        if (visible) {
            toggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            if (!toolBarNavigationListenerIsRegistered) {
                toggle.setToolbarNavigationClickListener(v -> onBackPressed());

                toolBarNavigationListenerIsRegistered = true;
            }

        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.setToolbarNavigationClickListener(null);
            toolBarNavigationListenerIsRegistered = false;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}
