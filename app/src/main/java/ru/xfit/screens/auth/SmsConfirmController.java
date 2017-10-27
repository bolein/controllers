package ru.xfit.screens.auth;

import android.databinding.ObservableField;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.controllers.Request;

import ru.xfit.R;
import ru.xfit.databinding.LayoutSmsConfirmBinding;
import ru.xfit.model.data.register.RegisterRequest;
import ru.xfit.model.service.Api;
import ru.xfit.screens.HomeController;
import ru.xfit.screens.XFitController;

/**
 * Created by TESLA on 27.10.2017.
 */

public class SmsConfirmController extends XFitController<LayoutSmsConfirmBinding> {

    public ObservableField<String> code1 = new ObservableField<>("");
    public ObservableField<String> code2 = new ObservableField<>("");
    public ObservableField<String> code3 = new ObservableField<>("");
    public ObservableField<String> code4 = new ObservableField<>("");
    public ObservableField<String> code5 = new ObservableField<>("");
    public ObservableField<String> code6 = new ObservableField<>("");
    public ObservableField<String> errorResponse = new ObservableField<>();

    private RegisterRequest regData;

    public SmsConfirmController(RegisterRequest regData) {
        this.regData = regData;
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_sms_confirm;
    }

    public void activate(View view) {
        StringBuilder code = new StringBuilder();
        code.append(code1.get());
        code.append(code2.get());
        code.append(code3.get());
        code.append(code4.get());
        code.append(code5.get());
        code.append(code6.get());

        regData.phoneConfirmation = code.toString();

        Request.with(this, Api.class)
                .create(api -> api.register(regData))
                .onError(error -> {
                    errorResponse.set(error.getMessage());
                    Snackbar.make(view, "Error: " + error.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
                })
                .execute(registrationResponse -> {
                    //save user
                    show(new HomeController());
                });
    }

}
