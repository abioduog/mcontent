package com.mnewservice.mcontent.domain;

import java.util.Calendar;
import java.util.Date;

public abstract class AbstractWeekDay {

    private Calendar day;

    public AbstractWeekDay(Calendar day) {
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


//<editor-fold defaultstate="collapsed" desc="getters/setters">
    public Calendar getDay() {
        return day;
    }

    public void setDay(Calendar day) {
        this.day = day;
    }

//</editor-fold>
}
