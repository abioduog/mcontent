package com.mnewservice.mcontent.manager;

import com.mnewservice.mcontent.domain.DeliveryTime;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Service
public class DeliveryManager {

    private static final Logger LOG = Logger.getLogger(DeliveryManager.class);

    public void deliverContent(DeliveryTime deliveryTime) {
        LOG.debug("deliveryTime: " + deliveryTime);
        //TODO
    }

    public void deliverExpirationNotification(DeliveryTime deliveryTime) {
        LOG.debug("deliveryTime: " + deliveryTime);
        //TODO
    }
}
