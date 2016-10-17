package com.mnewservice.mcontent.domain;

public class SubscriptionWeeklyReportDTO {

    private int year;
    private int week;
    private Long renewals;
    private Long unSubscriptions;

    public SubscriptionWeeklyReportDTO(int year, int week) {
        this.year = year;
        this.week = week;
        this.renewals = 0L;
        this.unSubscriptions = 0L;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public Long getRenewals() {
        return renewals;
    }

    public void addRenewals(Long renewals) {
        this.renewals += renewals;
    }

    public Long getUnSubscriptions() {
        return unSubscriptions;
    }

    public void addUnSubscriptions(Long unSubscriptions) {
        this.unSubscriptions += unSubscriptions;
    }


}
