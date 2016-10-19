package com.mnewservice.mcontent.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeekDayItem {

    private Calendar day;
    private Long renewals = 0L;
    private Long unSubscriptions = 0L;
    private Long subscriptions = 0L;

    public WeekDayItem(Calendar day) {
        this.day = (Calendar) day.clone();
    }

    public boolean isSameDay(Date date) {
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);

        return day.get(Calendar.YEAR) == dateCal.get(Calendar.YEAR)
                && day.get(Calendar.DAY_OF_YEAR) == dateCal.get(Calendar.DAY_OF_YEAR);
    }

    public String getDayString() {
        return WeekItem.dateToString(this.day.getTime());
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

    public static List<WeekDayItem> createList(Calendar firstDay, int numOfDays, boolean toFuture) {
        Calendar weekDay = (Calendar) firstDay.clone();
        List<WeekDayItem> weekDataList = new ArrayList<>();
        int i = numOfDays;
        while (i > 0) {
            weekDataList.add(new WeekDayItem(weekDay));
            weekDay.add(Calendar.DAY_OF_YEAR, toFuture ? 1 : -1);
            i--;
        }
        return weekDataList;
    }


//<editor-fold defaultstate="collapsed" desc="getters/setters">
    public Calendar getDay() {
        return day;
    }

    public void setDay(Calendar day) {
        this.day = day;
    }

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
