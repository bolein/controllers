package ru.xfit.screens.clubs;

import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;
import android.view.View;

import com.controllers.Request;

import java.util.List;

import ru.xfit.R;
import ru.xfit.databinding.LayoutAboutClubBinding;
import ru.xfit.domain.App;
import ru.xfit.misc.NavigationClickListener;
import ru.xfit.misc.TransparentStatusBar;
import ru.xfit.misc.views.MessageDialog;
import ru.xfit.model.data.club.ClubItem;
import ru.xfit.model.data.contract.Contract;
import ru.xfit.model.service.Api;
import ru.xfit.screens.BlankToolbarController;
import ru.xfit.screens.clubs.news.NewsController;
import ru.xfit.screens.schedule.ClubClassesController;

/**
 * Created by TESLA on 13.11.2017.
 */

public class AboutClubController extends BlankToolbarController<LayoutAboutClubBinding>
        implements NavigationClickListener, MessageDialog.DialogResultListener, TransparentStatusBar {
    public final ObservableBoolean progress = new ObservableBoolean();
    public ClubItem club;
    public ObservableBoolean isMyClub = new ObservableBoolean();

    public AboutClubController(ClubItem club, boolean isMyClub) {
        this.club = club;
        this.isMyClub.set(isMyClub);
    }

    public void back(View view) {
        back();
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_about_club;
    }

    public String getAddress() {
        if (club.city.startsWith("!")) {
            return club.city.substring(1, club.city.length()) + " " + club.address;
        } else
            return club.city + " " + club.address;
    }

    public void getRoute(View view) {
//        if (club.latitude == 0 || club.longitude == 0)
//            return;
        Uri gmmIntentUri = Uri.parse(String.format("google.navigation:q=%s,%s", club.latitude, club.longitude));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(view.getContext().getPackageManager()) != null) {
            if (getActivity() != null)
                getActivity().startActivity(mapIntent);
        }
    }

    public void getCall(View view) {
        if (club.phone == null)
            return;

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + club.phone));
        if (getActivity() != null)
            getActivity().startActivity(intent);
    }

    public void sendEmail(View view) {
        if (club.email == null)
            return;

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", club.email, null));
        if (getActivity() != null)
            getActivity().startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public void getSchedule(View view) {
        progress.set(true);
        Request.with(this, Api.class)
                .create(api -> api.getClassesForClub(club.id))
                .onFinally(() -> progress.set(false))
                .execute(schedule -> {
                    AboutClubController.this.show(new ClubClassesController(schedule));
                });

    }

    @Override
    public String getTitle() {
        return club.title;
    }

    public void suspendCard(View view) {
        progress.set(true);
        Request.with(this, Api.class)
                .create(Api::getContracts)
                .execute(this::searchCurrentContract);
    }

    private void searchCurrentContract(List<Contract> contractList) {
        Contract currentContract = null;
        for (Contract contract : contractList) {
            if (contract.clubId.equals(this.club.id)) {
                currentContract = contract;
                show(new SuspendCardController(contract));
            }
        }
        progress.set(false);
        if (currentContract == null) {
            MessageDialog messageDialog = new MessageDialog.Builder()
                    .setMessage(App.getContext().getResources().getString(R.string.dialog_phone_not_found))
                    .build();
            messageDialog.setController(this);
            messageDialog.show(getActivity().getSupportFragmentManager(), "MY_TAG");
        }
    }

    public void getTrainers(View view) {
        show(new TrainersController(club));
    }

    public void getNews(View view) {
        show(new NewsController(club.id));
    }

    public void getGuestVisit(View view) {
        Snackbar.make(view, "Coming soon...", BaseTransientBottomBar.LENGTH_SHORT).show();
    }

    public void buyCard(View view) {
        Snackbar.make(view, "Coming soon...", BaseTransientBottomBar.LENGTH_SHORT).show();
    }

    @Override
    public void onNavigationClick() {
        back();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onPositive(@NonNull String tag) {

    }

    @Override
    public void onNegative(@NonNull String tag) {

    }
}
