package ru.xfit.screens.auth;

import android.databinding.ObservableField;
import android.view.View;

import com.controllers.Request;

import ru.xfit.MainActivity;
import ru.xfit.R;
import ru.xfit.databinding.LayoutAuthBinding;
import ru.xfit.misc.utils.PrefUtils;
import ru.xfit.model.data.auth.User;
import ru.xfit.model.service.Api;
import ru.xfit.screens.XFitController;

import static ru.xfit.domain.App.PREFS_IS_USER_ALREADY_LOGIN;

/**
 * Created by TESLA on 25.10.2017.
 */

public class AuthController extends XFitController<LayoutAuthBinding> {

    public ObservableField<String> phone = new ObservableField<>("");
    public ObservableField<String> password = new ObservableField<>("");
    public ObservableField<String> errorResponse = new ObservableField<>();

    @Override
    public int getLayoutId() {
        return R.layout.layout_auth;
    }

    public void register(View view) {
        show(new RegisterController());
    }

    public void auth(View view) {
        Request.with(this, Api.class)
                .create(api -> api.authByPhone(phone.get(), password.get()))
                .onError(error -> {
                    errorResponse.set(error.getMessage());
                })
                .execute(user -> {

                    PrefUtils.getPreferences().edit().putBoolean(PREFS_IS_USER_ALREADY_LOGIN, true).commit();

                    user.user.language = user.language;
                    user.user.city = user.city;
                    user.user.token = user.token;

                    saveUser(user.user);

                    MainActivity.start(getActivity());
                    getActivity().finish();
                });
    }

    private void saveUser(User user) {
        Request.with(this, Api.class)
                .create(api -> api.saveUser(user))
                .execute();
    }

    public void onForgotPasswordClicked(View view) {

    }
}
