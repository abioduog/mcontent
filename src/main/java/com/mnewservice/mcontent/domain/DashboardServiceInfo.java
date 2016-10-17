package com.mnewservice.mcontent.domain;

import org.springframework.beans.BeanUtils;

public class DashboardServiceInfo extends Service {

    private long numOfSubscribers;

    public DashboardServiceInfo init(Service service, long numOfSubscribers) {
        BeanUtils.copyProperties(service, this);
        this.numOfSubscribers = numOfSubscribers;
        return this;
    }

    public long getNumOfSubscribers() {
        return numOfSubscribers;
    }

    public void setNumOfSubscribers(long numOfSubscribers) {
        this.numOfSubscribers = numOfSubscribers;
    }

}
