package ru.xfit.model.data.notifications;

import java.io.Serializable;

/**
 * Created by TESLA on 29.11.2017.
 */

public class Notification implements Serializable {
    public String id;
    public String message;
    public String publishDate;
    public Data data;
}
