package com.mnewservice.mcontent.domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class AbstractWeek<T> {

    private static String DATE_FORMAT = "d MMM yyyy";
    private static String WEEK_TEXT = "%s - %s";
    private Calendar firstCalendarDay;
    private Calendar lastCalendarDay;
    private List<T> weekDays;

    public AbstractWeek(Date date) {
        firstCalendarDay = WeekItem.setToStartOfDay(WeekItem.getCalendar(date)); // Transfer date to Calendar and set Hours = 0, Minutes = 0 etc...
        firstCalendarDay.add(Calendar.DAY_OF_YEAR, firstCalendarDay.getFirstDayOfWeek() - firstCalendarDay.get(Calendar.DAY_OF_WEEK)); // Adjust to begining of the week
        lastCalendarDay = WeekItem.setToEndOfDay((Calendar) firstCalendarDay.clone()); // Use 1st day but change hours=23, minutes=59 etc ...
        lastCalendarDay.add(Calendar.DAY_OF_YEAR, 6); // Move 6 days ahead to last day of the week
        weekDays = new ArrayList<>();
    }

    public abstract void initWeekData();

    public boolean isInWeek(Date date) {
        return (date.equals(this.firstCalendarDay.getTime())
                || date.equals(this.lastCalendarDay.getTime())
                || (date.after(this.firstCalendarDay.getTime()) && date.before(this.lastCalendarDay.getTime())));
    }

    public static String getDateFormat() {
        return DATE_FORMAT;
    }

    public static String dateToString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(WeekItem.getDateFormat());
        return df.format(date);
    }

    public static Date getDate(String dateStr) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(AbstractWeek.getDateFormat());
            return df.parse(dateStr);
        } catch (Exception ex) {
            return new Date();
        }
    }

    public static Calendar getCalendar(String dateStr) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(AbstractWeek.getDateFormat());
            Calendar day = Calendar.getInstance();
            day.setTime(df.parse(dateStr));
            return day;
        } catch (Exception ex) {
            return Calendar.getInstance();
        }
    }

    public static Calendar getCalendar(Date date) {
        try {
            Calendar day = Calendar.getInstance();
            day.setTime(date);
            return day;
        } catch (Exception ex) {
            return Calendar.getInstance();
        }
    }

    public static Calendar setToStartOfDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static Calendar setToEndOfDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal;
    }

    public String getStartDate() {
        return WeekItem.dateToString(this.firstCalendarDay.getTime());
    }

    public String getEndDate() {
        return WeekItem.dateToString(this.lastCalendarDay.getTime());
    }

    public String getWeekText() {
        return String.format(WEEK_TEXT, this.getStartDate(), this.getEndDate());
    }


//<editor-fold defaultstate="collapsed" desc="getters/setters">
    public List<T> getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(List<T> weekDays) {
        this.weekDays = weekDays;
    }

    public Calendar getFirstCalendarDay() {
        return firstCalendarDay;
    }

    public void setFirstCalendarDay(Calendar firstCalendarDay) {
        this.firstCalendarDay = firstCalendarDay;
    }

    public Calendar getLastCalendarDay() {
        return lastCalendarDay;
    }

    public void setLastCalendarDay(Calendar lastCalendarDay) {
        this.lastCalendarDay = lastCalendarDay;
    }

//</editor-fold>
}
