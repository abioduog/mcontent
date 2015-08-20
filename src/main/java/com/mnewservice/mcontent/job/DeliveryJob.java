package com.mnewservice.mcontent.job;

import com.mnewservice.mcontent.domain.DeliveryTime;
import com.mnewservice.mcontent.manager.DeliveryManager;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class DeliveryJob implements Job {

    private static final Logger LOG = Logger.getLogger(DeliveryJob.class);

    @Autowired
    private DeliveryManager deliveryManager;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOG.debug("jobExecutionContext: " + jobExecutionContext);

        DeliveryTime deliveryTime = (DeliveryTime) jobExecutionContext.get("DELIVERY_TIME");
        deliveryManager.deliverContent(deliveryTime);
        deliveryManager.deliverExpirationNotification(deliveryTime);
    }

}
