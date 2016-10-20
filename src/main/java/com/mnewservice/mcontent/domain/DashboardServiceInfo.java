package com.mnewservice.mcontent.domain;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;

public class DashboardServiceInfo extends Service {

    private long numOfSubscribers;
    private List<WeekServiceInfo> weeks;

    public DashboardServiceInfo init(Service service, long numOfSubscribers) {
        BeanUtils.copyProperties(service, this);
        this.numOfSubscribers = numOfSubscribers;
        this.weeks = new ArrayList<>();
        return this;
    }

    public long getNumOfSubscribers() {
        return numOfSubscribers;
    }

    public void setNumOfSubscribers(long numOfSubscribers) {
        this.numOfSubscribers = numOfSubscribers;
    }

    public List<WeekServiceInfo> getWeeks() {
        return weeks;
    }

    public void setWeeks(List<WeekServiceInfo> weeks) {
        this.weeks = weeks;
    }

}
