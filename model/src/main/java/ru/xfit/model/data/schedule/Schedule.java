package ru.xfit.model.data.schedule;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TESLA on 03.11.2017.
 */

public class Schedule implements Serializable {
    public String id;
    public String subscriptionId;
    public String datetime;
    public Boolean popular;
    public Integer length;
    public Boolean commercial;
    public Boolean bookingOpened;
    public Boolean preEntry;
    public Boolean firstFree;
    public Boolean _new;
    public Activity activity;
    public List<Trainer> trainers;
    public Item room;
    public Item group;
}
