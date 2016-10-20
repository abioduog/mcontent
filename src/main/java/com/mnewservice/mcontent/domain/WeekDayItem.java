package com.mnewservice.mcontent.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeekDayItem {

    private Calendar day;

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

//<editor-fold defaultstate="collapsed" desc="getters/setters">
    public Calendar getDay() {
        return day;
    }

    public void setDay(Calendar day) {
        this.day = day;
    }

//</editor-fold>
}
