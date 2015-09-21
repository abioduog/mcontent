package com.mnewservice.mcontent.job;

import com.mnewservice.mcontent.domain.DeliveryTime;
import com.mnewservice.mcontent.manager.DeliveryManager;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class DeliveryJob implements Job {

    public static String DELIVERY_TIME_PARAM = "DELIVERY_TIME";

    private static final Logger LOG = Logger.getLogger(DeliveryJob.class);

    @Autowired
    private DeliveryManager deliveryManager;

    @Override
    public void execute(JobExecutionContext jobExecutionContext)
            throws JobExecutionException {
        LOG.info("Delivery job started.");
        try {
            JobDataMap dataMap
                    = jobExecutionContext.getJobDetail().getJobDataMap();
            DeliveryTime deliveryTime
                    = (DeliveryTime) dataMap.getOrDefault(
                            DELIVERY_TIME_PARAM,
                            null
                    );

            deliveryManager.deliverContent(deliveryTime);

            if (DeliveryTime.T0800.equals(deliveryTime)) {
                // send expiration notifications daily at 8am
                deliveryManager.deliverExpirationNotification(deliveryTime);
            }

        } catch (Exception ex) {
            LOG.debug(ex);
            throw new JobExecutionException(ex);
        } finally {
            LOG.info("Delivery job ended.");
        }
    }

}
