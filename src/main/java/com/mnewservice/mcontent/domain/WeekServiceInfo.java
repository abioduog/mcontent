package com.mnewservice.mcontent.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class WeekServiceInfo extends WeekItem {

    public WeekServiceInfo(Date date) {
        super(date);
    }

    @Override
    public void initWeekData() {
        this.setWeekDays(WeekDayServiceInfo.createDayList(this.getFirstCalendarDay(), 7, true));
    }

    public static List<WeekItem> createWeekList(Calendar firstDay, int numOfWeeks, boolean toFuture) {
        Calendar day = (Calendar) firstDay.clone();
        List<WeekItem> weekList = new ArrayList<>();
        int i = numOfWeeks;
        while (i > 0) {
            weekList.add(new WeekServiceInfo(day.getTime()));
            day.add(Calendar.DAY_OF_YEAR, toFuture ? 7 : -7);
            i--;
        }
        return weekList;
    }


    public Long getRenewals() {
        return ((List<WeekDayServiceInfo>) getWeekDays()).stream().collect(Collectors.summingLong(p -> p.getRenewals()));
    }

    public Long getSubscriptions() {
        return ((List<WeekDayServiceInfo>) getWeekDays()).stream().collect(Collectors.summingLong(p -> p.getSubscriptions()));
    }

    public Long getUnSubscriptions() {
        return ((List<WeekDayServiceInfo>) getWeekDays()).stream().collect(Collectors.summingLong(p -> p.getUnSubscriptions()));
    }

}
