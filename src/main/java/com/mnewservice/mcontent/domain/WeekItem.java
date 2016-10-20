package com.mnewservice.mcontent.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeekItem extends AbstractWeek {

    public WeekItem(Date date) {
        super(date);
    }

    @Override
    public void initWeekData() {
        setWeekDays(WeekDayItem.createDayList(this.getFirstCalendarDay(), 7, true));
    }

    public static List<WeekItem> createWeekList(Calendar firstDay, int numOfWeeks, boolean toFuture) {
        Calendar day = (Calendar) firstDay.clone();
        List<WeekItem> weekList = new ArrayList<>();
        int i = numOfWeeks;
        while (i > 0) {
            weekList.add(new WeekItem(day.getTime()));
            day.add(Calendar.DAY_OF_YEAR, toFuture ? 7 : -7);
            i--;
        }
        return weekList;
    }

}
