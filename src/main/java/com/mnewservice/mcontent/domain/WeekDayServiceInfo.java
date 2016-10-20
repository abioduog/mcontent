package com.mnewservice.mcontent.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeekDayServiceInfo extends WeekDayItem {

    private Long renewals = 0L;
    private Long unSubscriptions = 0L;
    private Long subscriptions = 0L;

    public WeekDayServiceInfo(Calendar day) {
        super(day);
    }

    public static List<WeekDayItem> createDayList(Calendar firstDay, int numOfDays, boolean toFuture) {
        Calendar weekDay = (Calendar) firstDay.clone();
        List<WeekDayItem> weekDataList = new ArrayList<>();
        int i = numOfDays;
        while (i > 0) {
            weekDataList.add(new WeekDayServiceInfo(weekDay));
            weekDay.add(Calendar.DAY_OF_YEAR, toFuture ? 1 : -1);
            i--;
        }
        return weekDataList;
    }

    public void addRenewal() {
        this.renewals++;
    }

    public void addSubscription() {
        this.subscriptions++;
    }

    public void addUnSubscription() {
        this.unSubscriptions++;
    }



//<editor-fold defaultstate="collapsed" desc="getters/setters">
    public Long getRenewals() {
        return renewals;
    }

    public void setRenewals(Long renewals) {
        this.renewals = renewals;
    }

    public Long getUnSubscriptions() {
        return unSubscriptions;
    }

    public void setUnSubscriptions(Long unSubscriptions) {
        this.unSubscriptions = unSubscriptions;
    }

    public Long getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Long subscriptions) {
        this.subscriptions = subscriptions;
    }

//</editor-fold>
}
