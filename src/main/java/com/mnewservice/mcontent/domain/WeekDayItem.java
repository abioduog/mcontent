package com.mnewservice.mcontent.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeekDayItem extends AbstractWeekDay {

    public WeekDayItem(Calendar day) {
        super(day);
    }

    public static List<WeekDayItem> createDayList(Calendar firstDay, int numOfDays, boolean toFuture) {
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

}
