package ru.xfit.misc.utils.validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.xfit.R;
import ru.xfit.domain.App;

/**
 * Created by TESLA on 11.11.2017.
 */

public class AgeValidator extends StringValidator {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public String validate(String... args) {
        String result = super.validate(args[0]);
        if (result != null)
            return result;
        try {
            Date date = dateFormat.parse(args[0]);
            Calendar minAge = Calendar.getInstance();
            minAge.set(Calendar.YEAR, minAge.get(Calendar.YEAR) - 14);

            Calendar maxAge = new GregorianCalendar(1900, Calendar.JANUARY, 1);

            if (minAge.getTime().before(date) || maxAge.getTime().after(date))
                return App.getContext().getString(R.string.validation_incorrect_date);
        } catch (ParseException e) {
            e.printStackTrace();
            return App.getContext().getString(R.string.validation_incorrect_date);
        }
        return null;
    }
}
