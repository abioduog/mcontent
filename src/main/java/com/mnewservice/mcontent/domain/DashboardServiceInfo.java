package com.mnewservice.mcontent.domain;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;

public class DashboardServiceInfo extends Service {

    private long numOfSubscribers;
    private List<WeekItem> weeks;

    public DashboardServiceInfo init(Service service, List<WeekItem> weekList, long numOfSubscribers) {
        BeanUtils.copyProperties(service, this);
        this.numOfSubscribers = numOfSubscribers;
        this.weeks = new ArrayList<>(weekList);
        return this;
    }

    public long getNumOfSubscribers() {
        return numOfSubscribers;
    }

    public void setNumOfSubscribers(long numOfSubscribers) {
        this.numOfSubscribers = numOfSubscribers;
    }

    public List<WeekItem> getWeeks() {
        return weeks;
    }

    public void setWeeks(List<WeekItem> weeks) {
        this.weeks = weeks;
    }

}
