package com.mnewservice.mcontent.domain;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionWeeklyReportList {

    private List<SubscriptionWeeklyReportDTO> list;

    public SubscriptionWeeklyReportList() {
        this.list = new ArrayList<>();
    }

    public void add(int year, int week, Long renewals, Long un_subscriptions) {
        SubscriptionWeeklyReportDTO entry
                = list.
                stream().
                filter(x -> (x.getYear() == year && x.getWeek() == week)).
                findFirst().
                orElse(new SubscriptionWeeklyReportDTO(year, week));

        entry.addRenewals(renewals);
        entry.addUnSubscriptions(un_subscriptions);
    }

    public List<SubscriptionWeeklyReportDTO> getList() {
        return list;
    }

}
